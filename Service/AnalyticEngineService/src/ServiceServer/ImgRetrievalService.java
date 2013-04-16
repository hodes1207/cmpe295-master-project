package ServiceServer;

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

import MessageLayer.ClassifyResp;
import MessageLayer.ImgDisResEntry;
import MessageLayer.ImgRetrieveInitMsg;
import MessageLayer.ImgServMsg;
import MessageLayer.ImgServResp;
import MessageLayer.MessageObject;
import MessageLayer.RetID;
import database.databaseAPI;
import datamining.CLASSIFY_RES;
import datamining.PROB_ESTIMATION_RES;

public class ImgRetrievalService 
{	
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
		while (selector.select() >= 0) 
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
							System.out.println("Begin read sock");
							
							ObjectInputStream is = new ObjectInputStream(sc.socket()
									.getInputStream());
							ImgServResp resp = (ImgServResp)is.readObject();
							
							System.out.println("End read sock");
							
							if (resp.msgType == ImgServMsg.MsgType.SIM_SEARCH)
							{
								System.out.print("Receive SIM_SEARCH from img server, ");
								System.out.print("msg id: ");
								System.out.println(resp.searchResp.msgId);
								
								if (resp.searchResp != null)
								{
									if (!imgRetreivalRes.containsKey(resp.searchResp.msgId))
									{
										ImgRetrievalRes item = new ImgRetrievalRes();
										item.k = resp.searchResp.k;
										item.res = new ArrayList<ArrayList<ImgDisResEntry>>();
											
										imgRetreivalRes.put(resp.searchResp.msgId, item);
									}
										
									imgRetreivalRes.get(resp.searchResp.msgId).res.add(resp.searchResp.res);
									
									imgLock.lock();
									imgConV.signal();
									imgLock.unlock();
								}
							}
							else if (resp.msgType == ImgServMsg.MsgType.CLASSIFICATION)
							{
								ClassifyResp clsResp = resp.clsResp;
								if (null != clsResp)
								{
									if (!imgClassifyRes.containsKey(clsResp.msgId))
									{
										ArrayList<PROB_ESTIMATION_RES> item = new ArrayList<PROB_ESTIMATION_RES>();
										imgClassifyRes.put(clsResp.msgId, item);
									}
									
									imgClassifyRes.get(clsResp.msgId).add(clsResp.clsRes);
									
									imgLock.lock();
									imgConV.signal();
									imgLock.unlock();
								}
							}
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
					processPendingImgRetrievalRequest();
				}
				
				synchronized(this)
				{
					processPendingImgClassifyRequest();
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
		java.util.Random ran = new java.util.Random();
		ImgServMsg msg = new ImgServMsg(ImgServMsg.MsgType.SIM_SEARCH);
		msg.byteImg = byteImg;
		msg.k = nNum;
		msg.msgId = ran.nextInt();
			
		ReqInfo info = new ReqInfo();
		info.out = out;
		info.time = new Date();
		info.soc = soc;
		
		synchronized(this)
		{	
			pendingMsg.put(msg.msgId, info);
			broadCastMsg(msg);
			
			System.out.print("Broadcasting SIM_SEARCH to img servers, ");
			System.out.print("msg id: ");
			System.out.println(msg.msgId);
		}
	}	
	
	public void ClassificationRequest(byte[] byteImg, int domId, ObjectOutputStream out, Socket soc)
			throws IOException
	{
		java.util.Random ran = new java.util.Random();
		ImgServMsg msg = new ImgServMsg(ImgServMsg.MsgType.CLASSIFICATION);
		msg.byteImg = byteImg;
		msg.msgId = ran.nextInt();
		msg.domId = domId;
		
		ReqInfo info = new ReqInfo();
		info.out = out;
		info.time = new Date();
		info.soc = soc;
		
		synchronized(this)
		{	
			pendingMsg.put(msg.msgId, info);
			broadCastMsg(msg);
		}
	}
	
	public void StartTrainingRequest(int domId) throws IOException
	{
		ImgServMsg msg = new ImgServMsg(ImgServMsg.MsgType.START_TRAINING);
		msg.domId = domId;
		
		broadCastMsg(msg);
	}
	
	public void StartTuningRequest(int domId) throws IOException
	{
		ImgServMsg msg = new ImgServMsg(ImgServMsg.MsgType.START_TUNING);
		msg.domId = domId;
		
		broadCastMsg(msg);
	}
	
	public void broadCastMsg(ImgServMsg msg) throws IOException
	{
		if (null == msg)
			return;
		
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
	
	private void processPendingImgRetrievalRequest()
	{
		Date timenow = new Date();
		Iterator<Integer> iterator = imgRetreivalRes.keySet().iterator();
		while (iterator.hasNext()) 
		{
			int nId = iterator.next();
				
			if (imgRetreivalRes.get(nId).res.size() >= imgServers.size()
					|| timenow.getTime() - pendingMsg.get(nId).time.getTime() > 1000*10)
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
			}
		}
	}
	
	private void processPendingImgClassifyRequest()
	{
		Date timenow = new Date();
		Iterator<Integer> iterator = imgClassifyRes.keySet().iterator();
		while (iterator.hasNext()) 
		{
			int nId = iterator.next();
				
			if (imgClassifyRes.get(nId).size() >= imgServers.size()
					|| timenow.getTime() - pendingMsg.get(nId).time.getTime() > 1000*20)
			{
				//merge the result
				ArrayList<PROB_ESTIMATION_RES> res = imgClassifyRes.get(nId);
				HashMap<Integer, Integer> recNum = new HashMap<Integer, Integer>();
				HashMap<Integer, Double> recProb = new HashMap<Integer, Double>();
				for (int i = 0; i < res.size(); i++)
				{
					for (int j = 0; j < res.get(i).probRes.size(); j++)
					{
						int x = res.get(i).probRes.get(j).nClsId;
						if (!recNum.containsKey(x))
						{
							recNum.put(x, 0);
							recProb.put(x, 0.0);
						}
						
						recNum.put(x, recNum.get(x)+1);
						recProb.put(x, recProb.get(x) + res.get(i).probRes.get(j).dbProb);
					}
				}
				
				PROB_ESTIMATION_RES mergRes = new PROB_ESTIMATION_RES();
				Iterator<Integer> itRec = recNum.keySet().iterator();
				while (itRec.hasNext()) 
				{
					int clsid = itRec.next();
					double prob = recProb.get(clsid)/recNum.get(clsid);
					
					mergRes.probRes.add(new CLASSIFY_RES(clsid, prob));
				}
				
				int max = 0;
				for (int i = 1; i < mergRes.probRes.size(); i++)
				{
					if (mergRes.probRes.get(i).dbProb > mergRes.probRes.get(max).dbProb)
						max = i;
				}
				
				mergRes.nClsId = mergRes.probRes.get(max).nClsId;
					
				imgClassifyRes.remove(nId);
					
				if (pendingMsg.containsKey(nId))
				{
					ObjectOutputStream out = pendingMsg.get(nId).out;
					Socket soc = pendingMsg.get(nId).soc;
					if (soc.isConnected() && !soc.isClosed())
					{
						try 
						{
							MessageObject obj = new MessageObject();
							obj.classifyRes = mergRes;
							obj.setrettype(RetID.CLS_RES);
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
					
				System.out.println("Image classification results merged and sent.......");
			}
		}
	}
	
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
	
	private HashMap<Integer, ArrayList<PROB_ESTIMATION_RES>> imgClassifyRes = 
			new HashMap<Integer, ArrayList<PROB_ESTIMATION_RES>>();
}