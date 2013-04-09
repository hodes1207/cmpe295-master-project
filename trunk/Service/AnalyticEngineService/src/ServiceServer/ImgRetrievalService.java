package ServiceServer;

import imgproc.ImgFeatureExtractionWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import MessageLayer.ImgDisResEntry;
import MessageLayer.ImgRetrieveInitMsg;
import MessageLayer.ImgServMsg;
import MessageLayer.KNNSearchResp;
import MessageLayer.MessageObject;
import MessageLayer.RetID;
import ServiceInterface.EngineService;
import database.databaseAPI;

public class ImgRetrievalService {
	
	public boolean initServer(String domainDBName, String classDBName, String medicalImageDBName, 
			String DBUrl, int imgListenPort, int numImgServs)
	{
		try 
		{
			totalNumImgServs = numImgServs;
			databaseAPI.getInstance().initDBInstance(domainDBName, classDBName,
					medicalImageDBName, DBUrl);
			
			serverChnl = ServerSocketChannel.open();
			InetSocketAddress isa = new InetSocketAddress("127.0.0.1", imgListenPort);
			serverChnl.socket().bind(isa);
			serverChnl.configureBlocking(false);
			
			selector = Selector.open();
			serverChnl.register(selector, SelectionKey.OP_ACCEPT);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return false;
		} 
		
		imgLock = new ReentrantLock( );
		imgConV = imgLock.newCondition();
		
		return true;
	}
	
	public void imgServMsgThrdFunc() throws IOException
	{
		while (selector.select() > 0) 
		{
			Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
			while (keyIterator.hasNext()) 
			{
				synchronized(this)
				{
					SelectionKey sk = keyIterator.next();
					keyIterator.remove();
					
					if (sk.isAcceptable()) 
					{
						SocketChannel sc = serverChnl.accept();
						sc.configureBlocking(true);

						// image retrieval server fully connected
						if (imgServers.size() >= totalNumImgServs) 
						{
							sc.close();
							continue;
						}

						ImgRetrieveInitMsg msg = new ImgRetrieveInitMsg();
						msg.totalMachines = totalNumImgServs;
						for (int i = 0; i < totalNumImgServs; i++) 
						{
							if (!imgServers.containsKey(i)) {
								msg.curIndex = i;
								imgServers.put(i, sc);
								break;
							}
						}

						ObjectOutputStream oos = new ObjectOutputStream(sc.socket()
								.getOutputStream());
						oos.writeObject(msg);
						oos.flush();
						
						sc.configureBlocking(false);
						sc.register(selector, SelectionKey.OP_READ);
						sk.interestOps(SelectionKey.OP_ACCEPT);
					} 
					else if (sk.isReadable()) 
					{
						SocketChannel sc = (SocketChannel) sk.channel();
						sk.cancel();
						
						try 
						{
							//extra select needed to cancel completely.
							selector.selectNow();  
						} 
						catch (IOException e) 
						{
							e.printStackTrace();
						}
						 
						sc.configureBlocking(true);
						if (!sc.isConnected())
						{
							sc.close();
							removeSocket(sc);
							continue;
						}

						try 
						{
							ObjectInputStream is = new ObjectInputStream(sc.socket()
									.getInputStream());
							KNNSearchResp resp = (KNNSearchResp)is.readObject();
							
							if (!imgRetreivalRes.containsKey(resp.msgId))
							{
								ImgRetrievalRes item = new ImgRetrievalRes();
								item.k = resp.k;
								item.res = new ArrayList<ArrayList<ImgDisResEntry>>();
									
								imgRetreivalRes.put(resp.msgId, item);
							}
								
							imgRetreivalRes.get(resp.msgId).res.add(resp.res);
							
							imgLock.lock();
							imgConV.signal();
							imgLock.unlock();
						} 
						catch ( Exception e) //IOException | ClassNotFoundException
						{
							sc.close();
							removeSocket(sc);

							continue;
						}

						sc.configureBlocking(false);
						sc.register(selector, SelectionKey.OP_READ);
					}
				}
			}
		}
	}
	
	private void removeSocket(SocketChannel sc)
	{
		Iterator<Integer> iterator = imgServers.keySet()
				.iterator();
		
		while (iterator.hasNext()) 
		{
			int nId = (int) iterator.next();
			SocketChannel cTmp = imgServers.get(nId);
			if (cTmp.equals(sc)) 
			{
				imgServers.remove(nId);
				break;
			}
		}
	}
	
	public void imgServResProcessFunc()
	{
		while (true)
		{
			try 
			{
				imgLock.lock();
				imgConV.await(10, TimeUnit.SECONDS);
				imgLock.unlock();
				
				synchronized(this)
				{
					Date timenow = new Date();
					Iterator<Integer> iterator = imgRetreivalRes.keySet().iterator();
					while (iterator.hasNext()) 
					{
						int nId = iterator.next();
							
						if (imgRetreivalRes.get(nId).res.size() >= imgServers.size()
								|| timenow.getTime() - pendingMsg.get(nId).time.getTime() > 1000*20)
						{
							//merge the result
							ArrayList<ArrayList<ImgDisResEntry>> res = imgRetreivalRes.get(nId).res;
							int k = imgRetreivalRes.get(nId).k;
							ArrayList<Long> imgResId = new ArrayList<Long>();
								
							while (imgResId.size() < k)
							{
								int s = -1;
								for (int j = 0; j < res.size(); j++)
								{
									if (!res.get(j).isEmpty())
									{
										if (s < 0 || res.get(j).get(0).dist < res.get(s).get(0).dist)
											s = j;
									}
								}
									
								if (s < 0) break;
									
								imgResId.add(res.get(s).get(0).imgId);
								res.get(s).remove(0);
							}
								
							imgRetreivalRes.remove(nId);
								
							if (pendingMsg.containsKey(nId))
							{
								ObjectOutputStream out = pendingMsg.get(nId).out;
								Socket soc = pendingMsg.get(nId).soc;
								if (soc.isConnected() && !soc.isClosed())
								{
									try 
									{
										MessageObject obj = new MessageObject();
										obj.longlist = imgResId;
										obj.setrettype(RetID.LONG_LIST);
										out.writeObject(obj);
										out.flush();
									} 
									catch (IOException e) 
									{
										e.printStackTrace();
									}
								}
								
								pendingMsg.remove(nId);
							}
								
							System.out.println("Image retrieval result merged and sent.......");
							break;
						}
					}
				}
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public void run() throws InterruptedException
	{
		imgServMsgThrd.start();
		resProcThrd.start();
	}
	
	public void SimilaritySearchRequest(byte[] byteImg, int nNum, ObjectOutputStream out, Socket soc) 
			throws IOException
	{
		//get normalized vector
		double[] vectors = new double[ImgFeatureExtractionWrapper.TOTAL_DIM];
		ImgFeatureExtractionWrapper.extractFeature(byteImg, vectors);
			
		java.util.Random ran = new java.util.Random();
		ImgServMsg msg = new ImgServMsg(ImgServMsg.MsgType.SIM_SEARCH);
		msg.feature = vectors;
		msg.k = nNum;
		msg.msgId = ran.nextInt();
			
		ReqInfo info = new ReqInfo();
		info.out = out;
		info.time = new Date();
		info.soc = soc;
		
		synchronized(this)
		{
			pendingMsg.put(msg.msgId, info);
			
			Iterator<Integer> iterator = imgServers.keySet().iterator();
			while (iterator.hasNext()) 
			{
				int id = iterator.next();
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
				ObjectOutputStream oosb = new ObjectOutputStream(baos); 
				oosb.writeObject(msg); 
				oosb.flush();

				ByteBuffer bb = ByteBuffer.wrap(baos.toByteArray());
				imgServers.get(id).write(bb);
			}
		}
	}	
	
	public EngineService engineServ = new EngineService();
	
	private int totalNumImgServs = 1;
	private HashMap<Integer, SocketChannel> imgServers = new HashMap<Integer, SocketChannel>();
	private ServerSocketChannel serverChnl = null;
	private Selector selector = null;
	
	private ImgServerMsgThrd imgServMsgThrd = new ImgServerMsgThrd(this);
	private ImgServResProcThrd resProcThrd = new ImgServResProcThrd(this);
	
	private Lock imgLock = null;
	private Condition imgConV = null;
	
	//private HashMap<Integer, Integer> imgReqIdToServ = new HashMap<Integer, Integer>();
	class ReqInfo
	{
		public Date time = null;
		public ObjectOutputStream out = null;
		public Socket soc = null;
	}
	
	private HashMap<Integer, ReqInfo> pendingMsg = new HashMap<Integer, ReqInfo>();
	private HashMap<Integer, ImgRetrievalRes> imgRetreivalRes = 
			new HashMap<Integer, ImgRetrievalRes>();
}