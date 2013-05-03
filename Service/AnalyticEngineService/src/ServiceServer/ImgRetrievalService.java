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

import MessageLayer.*;
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
			InetSocketAddress isa = new InetSocketAddress(imgListenPort);
			
			System.out.println("Image service listening on port: " + imgListenPort);
			
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
						
						System.out.println("Image server connected, index: " + msg.curIndex );

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
								System.out.print("Receive SIM_SEARCH message from img server, ");
								System.out.print("msg id: ");
								System.out.println(resp.msgId);
								
								if (resp.searchResp != null)
								{
									if (!imgRetreivalRes.containsKey(resp.msgId))
									{
										ImgRetrievalRes item = new ImgRetrievalRes();
										item.k = resp.searchResp.k;
										item.res = new ArrayList<ArrayList<ImgDisResEntry>>();
											
										imgRetreivalRes.put(resp.msgId, item);
									}
										
									imgRetreivalRes.get(resp.msgId).res.add(resp.searchResp.res);
									
									imgLock.lock();
									imgConV.signal();
									imgLock.unlock();
								}
							}
							else if (resp.msgType == ImgServMsg.MsgType.CLASSIFICATION)
							{
								System.out.print("Receive CLASSIFICATION message from img server, ");
								System.out.print("msg id: ");
								System.out.println(resp.msgId);
								
								ClassifyResp clsResp = resp.clsResp;
								if (null != clsResp)
								{
									if (!imgClassifyRes.containsKey(resp.msgId))
									{
										ArrayList<PROB_ESTIMATION_RES> item = new ArrayList<PROB_ESTIMATION_RES>();
										imgClassifyRes.put(resp.msgId, item);
									}
									
									imgClassifyRes.get(resp.msgId).add(clsResp.clsRes);
									
									imgLock.lock();
									imgConV.signal();
									imgLock.unlock();
								}
							}
							else if (resp.msgType == ImgServMsg.MsgType.GET_MODEL_ACCURACY ||
										resp.msgType == ImgServMsg.MsgType.GET_MODEL_TRAININGINFO ||
										resp.msgType == ImgServMsg.MsgType.GET_MODEL_TUNINGINFO)
							{
								//Send message back to client
								int nId = resp.msgId;
								
								if (pendingMsg.containsKey(nId))
								{
									System.out.println("GET_MODEL_ACCURACY || GET_MODEL_TRAININGINFO || GET_MODEL_TUNINGINFO 2");
									
									ObjectOutputStream out = pendingMsg.get(nId).out;
									Socket soc = pendingMsg.get(nId).soc;
									if (soc.isConnected() && !soc.isClosed())
									{
										try 
										{
											MessageObject obj = new MessageObject();

											// assign message content
											if (resp.msgType == ImgServMsg.MsgType.GET_MODEL_ACCURACY)
											{
												System.out.println("Receive GET_MODEL_ACCURACY message from image server");
												
												obj.setrettype(RetID.BOOL);
												obj.modelAccuracy = resp.modelAccuracy;
												
												System.out.print("Model accuracy: ");
												System.out.println(resp.modelAccuracy);
											}
											else if (resp.msgType == ImgServMsg.MsgType.GET_MODEL_TRAININGINFO)
											{
												System.out.println("Receive GET_MODEL_TRAININGINFO message from image server");
												
												obj.setrettype(RetID.BOOL);
												obj.trainingInProgress = resp.trainingInProgress;
												
												System.out.print("Model in progress: ");
												System.out.println(resp.trainingInProgress);
											}
											else if (resp.msgType == ImgServMsg.MsgType.GET_MODEL_TUNINGINFO)
											{
												System.out.println("Receive GET_MODEL_TUNINGINFO message from image server");
												
												obj.setrettype(RetID.BOOL);
												obj.setStrval(resp.tuningInfo);
												
												System.out.print("Model tuning information: ");
												System.out.println(resp.tuningInfo);
											}
											
											out.writeObject(obj);
											out.flush();
										} 
										catch (IOException e) 
										{
											e.printStackTrace();
										}
									}
								}
							}
							else
							{
								System.out.println("Unknown message type found!!");
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
				
				//warning, might get blocked when client receive buffer is full 
				synchronized(this)
				{
					processPendingImgRetrievalRequest();
				}
				
				synchronized(this)
				{
					processPendingImgClassifyRequest();
				}
				
				synchronized(this)
				{
					timeOutMsgProcess();
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
	
	public void SimilaritySearchRequest(byte[] byteImg, int nNum, int domainId, ObjectOutputStream out, Socket soc) 
			throws IOException
	{
		//get normalized vector
		java.util.Random ran = new java.util.Random();
		ImgServMsg msg = new ImgServMsg(ImgServMsg.MsgType.SIM_SEARCH);
		msg.byteImg = byteImg;
		msg.k = nNum;
		msg.msgId = ran.nextInt();
		msg.domId = domainId;
			
		ReqInfo info = new ReqInfo();
		info.out = out;
		info.time = new Date();
		info.soc = soc;
		
		synchronized(this)
		{	
			pendingMsg.put(msg.msgId, info);
			broadCastMsg(msg);
			
			System.out.print("Broadcasting SIM_SEARCH to image servers, ");
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
	
	public synchronized ArrayList<ImgServerInfo> getImgServerInfo() throws IOException
	{
		Iterator<Integer> iterator = imgServers.keySet().iterator();
		ArrayList<ImgServerInfo> res = new ArrayList<ImgServerInfo>();
		
		while (iterator.hasNext()) 
		{
			int nId = (int) iterator.next();
			SocketChannel cTmp = imgServers.get(nId);
			
			if (cTmp != null)
			{
				InetSocketAddress addr = (InetSocketAddress)cTmp.getRemoteAddress();
				if (null != addr)
				{
					ImgServerInfo info = new ImgServerInfo();
					info.ServerIndex = nId;
					info.ServerDesc = addr.getHostName();
					res.add(info);
				}
			}
			
		}
		
		return res;
	}
	
	//all function below returns true if api call success, 
	//false if api call failed (usually due to connection failure of image  server)
	//accuracy[0] is the accuracy of the specific model
	public void getModelAccuracyRequest(int serverIndex, int domId, ObjectOutputStream out, Socket soc) throws IOException
	{
		java.util.Random ran = new java.util.Random();
		ImgServMsg msg = new ImgServMsg(ImgServMsg.MsgType.GET_MODEL_ACCURACY);
		msg.imgServIndex = serverIndex;
		msg.msgId = ran.nextInt();
		msg.domId = domId;
		
		ReqInfo info = new ReqInfo();
		info.out = out;
		info.time = new Date();
		info.soc = soc;
		
		synchronized(this)
		{	
			pendingMsg.put(msg.msgId, info);
			sendMsg(msg.imgServIndex, msg);
		}
	}

	public void getTuningInfoRequest(int serverIndex, int domId, ObjectOutputStream out, Socket soc) throws IOException
	{
		java.util.Random ran = new java.util.Random();
		ImgServMsg msg = new ImgServMsg(ImgServMsg.MsgType.GET_MODEL_TUNINGINFO);
		msg.imgServIndex = serverIndex;
		msg.msgId = ran.nextInt();
		msg.domId = domId;
		
		ReqInfo info = new ReqInfo();
		info.out = out;
		info.time = new Date();
		info.soc = soc;
		
		synchronized(this)
		{	
			pendingMsg.put(msg.msgId, info);
			sendMsg(msg.imgServIndex, msg);
		}
	}

	//info[0] ==> true: training in progress, false: not in  progress
	public void getTrainingInfoRequest(int serverIndex, int domId, ObjectOutputStream out, Socket soc) throws IOException
	{
		java.util.Random ran = new java.util.Random();
		ImgServMsg msg = new ImgServMsg(ImgServMsg.MsgType.GET_MODEL_TRAININGINFO);
		msg.imgServIndex = serverIndex;
		msg.msgId = ran.nextInt();
		msg.domId = domId;
		
		ReqInfo info = new ReqInfo();
		info.out = out;
		info.time = new Date();
		info.soc = soc;
		
		synchronized(this)
		{	
			pendingMsg.put(msg.msgId, info);
			sendMsg(msg.imgServIndex, msg);
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
			sendMsg(id, msg);
		}
	}
	
	public void sendMsg(int id, ImgServMsg msg) throws IOException
	{
		if (!imgServers.containsKey(id))
			return;
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
		ObjectOutputStream oosb = new ObjectOutputStream(baos); 
		oosb.writeObject(msg); 
		oosb.flush();

		ByteBuffer bb = ByteBuffer.wrap(baos.toByteArray());
		imgServers.get(id).write(bb);
	}
	
	private void timeOutMsgProcess()
	{
		Iterator<Integer> iterator = pendingMsg.keySet().iterator();
		
		while (iterator.hasNext()) 
		{
			Date timenow = new Date();
			int nId = iterator.next();
			
			if (timenow.getTime() - pendingMsg.get(nId).time.getTime() > 1000*10)
			{
				ObjectOutputStream out = pendingMsg.get(nId).out;
				Socket soc = pendingMsg.get(nId).soc;
				if (soc.isConnected() && !soc.isClosed())
				{
					try 
					{
						MessageObject obj = new MessageObject();
						obj.setrettype(RetID.TIMEOUT);
						out.writeObject(obj);
						out.flush();
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private void processPendingImgRetrievalRequest()
	{
		Iterator<Integer> iterator = imgRetreivalRes.keySet().iterator();
		
		while (iterator.hasNext()) 
		{
			int nId = iterator.next();
			Date timenow = new Date();
					
			if (pendingMsg.containsKey(nId))
			{
				boolean timeout = timenow.getTime() - pendingMsg.get(nId).time.getTime() > 1000*10;
				if (imgRetreivalRes.get(nId).res.size() >= imgServers.size() || timeout)
				{
					if (!timeout)
						System.out.println("All similarity results got");
					else
						System.out.println("Similarity search time out");
						
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
					iterator.remove();
					
					System.out.println("Image retrieval result merged and sent.......");
				}
			}
			else
			{
				iterator.remove();
			}
		}
	}
	
	private void processPendingImgClassifyRequest()
	{
		Iterator<Integer> iterator = imgClassifyRes.keySet().iterator();
		while (iterator.hasNext()) 
		{
			int nId = iterator.next();
			Date timenow = new Date();
			
			if (pendingMsg.containsKey(nId))
			{
				boolean timeout = timenow.getTime() - pendingMsg.get(nId).time.getTime() > 1000*10;
				if (imgClassifyRes.get(nId).size() >= imgServers.size() || timeout)
				{
					if (!timeout)
						System.out.println("All classification results got");
					else
						System.out.println("Classification time out");
					
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
					iterator.remove();
						
					System.out.println("Image classification results merged and sent.......");
				}
			}
			else
			{
				iterator.remove();
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