package ImgRetrieveServer;
import imgproc.ImgFeatureExtractionWrapper;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import MessageLayer.ClassifyResp;
import MessageLayer.ImgDisResEntry;
import MessageLayer.ImgRetrieveInitMsg;
import MessageLayer.ImgServMsg;
import MessageLayer.ImgServMsg.MsgType;
import MessageLayer.ImgServResp;
import MessageLayer.KNNSearchResp;
import MessageLayer.SysPerfInfo;
import ServiceInterface.ImgFeatureComparator;
import SystemMonitor.IMonitorService;
import SystemMonitor.MonitorInfoBean;
import SystemMonitor.MonitorServiceImpl;

import database.*;
import datamining.CLASSIFY_ENTITY;
import datamining.Normalizer;
import datamining.PROB_ESTIMATION_RES;

public class ImgRetrieveServer {
	
	public boolean initService(String xmlCfgFile)
	{
		//parse configuration file
		try 
		{
			System.out.println("Initializing ..... ");
			
			imgLock = new ReentrantLock( );
			imgConV = imgLock.newCondition();
			
			xmlcfgfile = xmlCfgFile;
			
			File cfgFile = new File(xmlCfgFile);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(cfgFile);
			doc.getDocumentElement().normalize();
			
			Element root = (Element)doc.getElementsByTagName("InitInfo").item(0);
			String classDBName = root.getAttribute("classDBName");
			String domainDBName = root.getAttribute("domainDBName");
			String medicalImageDBName = root.getAttribute("medicalImageDBName");
			String DBUrl = root.getAttribute("DBUrl");
			String strIP = root.getAttribute("ip");
			reloadhours = Integer.parseInt(root.getAttribute("reloadhours"));
			int port = Integer.parseInt(root.getAttribute("port"));
			
			//setup connection to service
			soc = new Socket(strIP, port);
			System.out.print("Connected on index : ");
			
			ObjectInputStream socIn = new ObjectInputStream(soc.getInputStream());
			ImgRetrieveInitMsg initMsg = (ImgRetrieveInitMsg)socIn.readObject();  
			totalMachines = initMsg.totalMachines;
			curIndex = initMsg.curIndex;
			System.out.println(curIndex);
			
			//Start loading data from database
			databaseAPI.getInstance().initDBInstance(domainDBName, classDBName, 
					medicalImageDBName, DBUrl);
			ArrayList<Domain> domains = databaseAPI.getInstance().getDomain();
			
			ArrayList<CLASSIFY_ENTITY> allClsEnt = new ArrayList<CLASSIFY_ENTITY>();
			
			//Load data from database
			System.out.println("Loading data from database ....");
			for (int i = 0; i < domains.size(); i++)
			{
				int nDomainId = domains.get(i).domainId;
				if (nDomainId == ModelManager.WHOLE_DOMAIN_ID)
					continue;
				
				ArrayList<MedicalImage> imgs = loadDomainImgs(nDomainId);
				
				m_modelMgr.addDomain(nDomainId);
				allImgs.addAll(imgs);
				
				ArrayList<CLASSIFY_ENTITY> clsEnt = new ArrayList<CLASSIFY_ENTITY>();
				for (int j = 0; j < imgs.size(); j++)
				{
					CLASSIFY_ENTITY e = new CLASSIFY_ENTITY();
					e.nClsId = imgs.get(j).classId;
					e.vectors = imgs.get(j).featureV;
					clsEnt.add(e);
					allClsEnt.add(e);
				}
				
				mpDom2DataSet.put(nDomainId, clsEnt);
			}
			
			System.out.println("Normalizing image data ....");
			
			for (int i = 0; i < allImgs.size(); i++)
			{
				norm.normalizeVector(allImgs.get(i).featureV);
			}
			
			for (int i = 0; i < domains.size(); i++)
			{
				int nDomainId = domains.get(i).domainId;
				if (nDomainId == ModelManager.WHOLE_DOMAIN_ID)
					continue;
				
				MedicalParameter param = databaseAPI.getInstance().getModelParameter(nDomainId);
				if (param == null)
					continue;
				
				//set model parameters
				System.out.println("Training domain model .....");
				m_modelMgr.setModelParameter(nDomainId, param.bRBF, param.dbRBF_c, param.dbRBF_g, param.dbLinear_c);
				m_modelMgr.initialBuildModel(nDomainId, mpDom2DataSet.get(nDomainId));
			}
			
			//build the whole model
			System.out.println("Training whole model .....");
			MedicalParameter param = databaseAPI.getInstance().getModelParameter(ModelManager.WHOLE_DOMAIN_ID);
			if (param == null)
				return false;
			
			m_modelMgr.setModelParameter(ModelManager.WHOLE_DOMAIN_ID, param.bRBF, param.dbRBF_c, 
					param.dbRBF_g, param.dbLinear_c);
			
			m_modelMgr.initialBuildModel(ModelManager.WHOLE_DOMAIN_ID, allClsEnt);
			
			System.out.println("Initialize finished");
		} 
		catch ( Exception e1) //ParserConfigurationException | SAXException | IOException
		{
			e1.printStackTrace();
			return false;
		} /*
		catch (ClassNotFoundException e) 
		{
			//e.printStackTrace();
			return false;
		} */
		
		return true;
	}
	
	public void run() throws InterruptedException
	{
		workThrd.start();
		reloadThrd.start();
		threadSignal.await();
	}
	
	void reloadThrdFunc()
	{
		while (true)
		{
			imgLock.lock();
			boolean timeout = false;
			try {
				timeout = !imgConV.await(reloadhours, TimeUnit.HOURS);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			imgLock.unlock();
			
			if (!timeout)
				break;
			
			bStopWorker = true;
			try {
				soc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				workThrd.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//reset members
			allImgs = new ArrayList<MedicalImage>();
			totalMachines = 1;
			curIndex = 0;
			norm = new Normalizer(imgproc.ImgFeatureExtractionWrapper.TOTAL_DIM);
			soc = null;
			bStopWorker = false;
			workThrd = new ImgRetrievalThrd(this);
			
			if (!initService(xmlcfgfile))
				return;
			
			workThrd.start();
		}
	}
	
	void workerThrdFunc()
	{
		try 
		{
			while (!bStopWorker)
			{
				ObjectInputStream socIn = new ObjectInputStream(soc.getInputStream());
				ImgServMsg inMsg = (ImgServMsg)socIn.readObject();
					
				if (inMsg.msgType == ImgServMsg.MsgType.SIM_SEARCH)
				{
					if (null == inMsg.byteImg)
						continue;
					
					double[] vectors = new double[ImgFeatureExtractionWrapper.TOTAL_DIM];
					ImgFeatureExtractionWrapper.extractFeature(inMsg.byteImg, vectors);
						
					KNNSearchResp knnResp = new KNNSearchResp();
					knnResp.k = inMsg.k;
					knnResp.res = searchKNN(vectors, inMsg.k, inMsg.domId);
					
					ImgServResp resp = new ImgServResp(ImgServMsg.MsgType.SIM_SEARCH);
					resp.searchResp = knnResp;
					resp.msgId = inMsg.msgId;
						
					ObjectOutputStream socOut = new ObjectOutputStream(soc.getOutputStream());
					socOut.writeObject(resp);
					
					System.out.print("Search result sent, msg id: ");
					System.out.println(resp.msgId);
				}
				else if (inMsg.msgType == ImgServMsg.MsgType.CLASSIFICATION)
				{
					if (null == inMsg.byteImg)
						continue;
					
					double[] vectors = new double[ImgFeatureExtractionWrapper.TOTAL_DIM];
					ImgFeatureExtractionWrapper.extractFeature(inMsg.byteImg, vectors);
					
					ArrayList<Double> featureV = new ArrayList<Double>();
					for (int i = 0; i < vectors.length; i++)
						featureV.add(vectors[i]);
					
					norm.normalizeVector(featureV);
					PROB_ESTIMATION_RES res = m_modelMgr.classify(featureV, inMsg.domId);
					
					ClassifyResp clsResp = new ClassifyResp();
					clsResp.clsRes = res;
					
					ImgServResp resp = new ImgServResp(ImgServMsg.MsgType.CLASSIFICATION);
					resp.clsResp = clsResp;
					resp.msgId = inMsg.msgId;
					
					ObjectOutputStream socOut = new ObjectOutputStream(soc.getOutputStream());
					socOut.writeObject(resp);
					
					System.out.println("Classification result sent");
				}
				else if (inMsg.msgType == ImgServMsg.MsgType.START_TRAINING)
				{
					MedicalParameter param = databaseAPI.getInstance().getModelParameter(inMsg.domId);
					if (null == param || !mpDom2DataSet.containsKey(inMsg.domId))
						continue;
					
					ArrayList<datamining.CLASSIFY_ENTITY> buildDataSet = mpDom2DataSet.get(inMsg.domId);
					
					//transition between to type of parameters
					datamining.ModelParameter paramNew = new datamining.ModelParameter();
					paramNew.bRBFKernel = param.bRBF;
					paramNew.dbLinear_C = param.dbLinear_c;
					paramNew.dbRBF_C = param.dbRBF_c;
					paramNew.dbRBF_G = param.dbRBF_g;
					
					m_modelMgr.requestTraining(inMsg.domId, paramNew, buildDataSet);
				}
				else if (inMsg.msgType == ImgServMsg.MsgType.START_TUNING)
				{
					int nDomainId = inMsg.domId;
					MedicalParameter param = databaseAPI.getInstance().getModelParameter(nDomainId);
					if (null == param || 
							(nDomainId != ModelManager.WHOLE_DOMAIN_ID && !mpDom2DataSet.containsKey(nDomainId)))
						continue;
					
					ArrayList<CLASSIFY_ENTITY> imgRef = new ArrayList<CLASSIFY_ENTITY>();
					ArrayList<CLASSIFY_ENTITY> imgSample = new ArrayList<CLASSIFY_ENTITY>();
					if (nDomainId != ModelManager.WHOLE_DOMAIN_ID)
						imgRef = mpDom2DataSet.get(nDomainId);
					else
					{
						imgRef = new ArrayList<CLASSIFY_ENTITY>();
						Iterator<Integer> iterator = mpDom2DataSet.keySet().iterator();
						while (iterator.hasNext()) 
						{
							int nId = (int)iterator.next();
							imgRef.addAll(mpDom2DataSet.get(nId));
						}
					}
					
					java.util.Random ran = new java.util.Random();
					for (int i = 1; i <= imgRef.size(); i++)
					{
						if (imgSample.size() < param.nMaxSampleNum)
							imgSample.add(imgRef.get(i-1));
						else
						{
							boolean bSelect = ((Math.abs(ran.nextInt())%i) <= (imgSample.size()-1)); 
				
							if (bSelect)
							{
								int nIndexSwap = Math.abs(ran.nextInt())%imgSample.size();
								imgSample.set(nIndexSwap, imgRef.get(i-1));
							}
						}
					}
					
					Collections.shuffle(imgSample);
					ArrayList<datamining.CLASSIFY_ENTITY> buildDataSet = new ArrayList<datamining.CLASSIFY_ENTITY>();
					ArrayList<datamining.CLASSIFY_ENTITY> testDataSet = new ArrayList<datamining.CLASSIFY_ENTITY>();
					for (int i = 0; i < imgSample.size(); i++)
					{
						if (i < imgSample.size()/param.nFold)
							testDataSet.add(imgSample.get(i));
						else
							buildDataSet.add(imgSample.get(i));
					}
					
					if(param.bRBF)
						m_modelMgr.requestTuning(nDomainId, testDataSet, buildDataSet, true);
					else
						m_modelMgr.requestTuning(nDomainId, testDataSet, buildDataSet, false);
				}
				else if (inMsg.msgType == ImgServMsg.MsgType.GET_MODEL_ACCURACY)
				{
					ImgServResp resp = new ImgServResp(MsgType.GET_MODEL_ACCURACY);
					resp.msgId = inMsg.msgId;
					resp.modelAccuracy = m_modelMgr.getModelAccuracy(inMsg.domId);
					
					System.out.print("Model accuracy: ");
					System.out.println(resp.modelAccuracy);
					
					ObjectOutputStream socOut = new ObjectOutputStream(soc.getOutputStream());
					socOut.writeObject(resp);
					
					System.out.println("Model accuracy result sent");
				}
				else if (inMsg.msgType == ImgServMsg.MsgType.GET_MODEL_TRAININGINFO)
				{
					ImgServResp resp = new ImgServResp(MsgType.GET_MODEL_TRAININGINFO);
					resp.msgId = inMsg.msgId;
					resp.trainingInProgress = m_modelMgr.isTrainingInProgress(inMsg.domId);
					
					System.out.print("Model in progress: ");
					System.out.println(resp.trainingInProgress);
					
					ObjectOutputStream socOut = new ObjectOutputStream(soc.getOutputStream());
					socOut.writeObject(resp);
					
					System.out.println("Model training information sent");
				}
				else if (inMsg.msgType == ImgServMsg.MsgType.GET_MODEL_TUNINGINFO)
				{
					ImgServResp resp = new ImgServResp(MsgType.GET_MODEL_TUNINGINFO);
					resp.msgId = inMsg.msgId;
					resp.tuningInfo = m_modelMgr.getTuningInfo(inMsg.domId);
					
					System.out.print("Model tuning information: ");
					System.out.println(resp.tuningInfo);
					
					ObjectOutputStream socOut = new ObjectOutputStream(soc.getOutputStream());
					socOut.writeObject(resp);
					
					System.out.println("Model tuning information sent");
				}
				else if (inMsg.msgType == ImgServMsg.MsgType.SYS_PERF_INFO)
				{
					ImgServResp resp = new ImgServResp(MsgType.SYS_PERF_INFO);
					resp.msgId = inMsg.msgId;
					
					IMonitorService service = new MonitorServiceImpl();
					MonitorInfoBean monitorInfo = new MonitorInfoBean();
					try {
						monitorInfo = service.getMonitorInfoBean();
					} catch (Exception e) {
						e.printStackTrace();
					}

					resp.perfInfo = new SysPerfInfo();
					resp.perfInfo.cpuPercent = monitorInfo.getCpuRatio();
					resp.perfInfo.freeJVMMem = monitorInfo.getFreeMemory();
					resp.perfInfo.maxJVMMem = monitorInfo.getMaxMemory();
					resp.perfInfo.totalJVMMem = monitorInfo.getTotalMemory();
					resp.perfInfo.thrdNum = monitorInfo.getTotalThread();
					resp.perfInfo.osName = monitorInfo.getOsName();
					
					System.out.print("Get system performance information .... ");
					
					ObjectOutputStream socOut = new ObjectOutputStream(soc.getOutputStream());
					socOut.writeObject(resp);
					
					System.out.println("Model tuning information sent");
				}
			}
		} 
		catch (ClassNotFoundException e) 
		{
		} 
		catch (IOException e) 
		{
		}
	}
	
	//return sorted closest k images
	private ArrayList<ImgDisResEntry> searchKNN(double[] vectors, int k, int domainId)
	{
		if (null == vectors || vectors.length != ImgFeatureExtractionWrapper.TOTAL_DIM || k <= 0)
			return new ArrayList<ImgDisResEntry>();
		
		ArrayList<Double> featureV = new ArrayList<Double>();
		for (int i = 0; i < vectors.length; i++)
			featureV.add(vectors[i]);
		
		norm.normalizeVector(featureV);
		
		ImgFeatureComparator comp = new ImgFeatureComparator(featureV, true);
		Queue<MedicalImage> knnQue =  new PriorityQueue<MedicalImage>(k, comp); 
		for (int i = 0; i < allImgs.size(); i++)
		{
			if (domainId != ModelManager.WHOLE_DOMAIN_ID && allImgs.get(i).domainId != domainId)
				continue;
			
			if (knnQue.size() < k)
				knnQue.add(allImgs.get(i));
			else
			{
				if (comp.compare(knnQue.peek(), allImgs.get(i)) < 0)
				{
					knnQue.remove();
					knnQue.add(allImgs.get(i));
				}
			}
		}
		
		ArrayList<ImgDisResEntry> res = new ArrayList<ImgDisResEntry>();
		while (knnQue.size() > 0)
		{
			ImgDisResEntry entry = new ImgDisResEntry();
			MedicalImage img = knnQue.remove();
			entry.imgId = img.imageId;
			entry.dist = ImgFeatureComparator.calcDist(featureV, img.featureV);
			
			res.add(0, entry);
		}
		
		return res;
	}
	
	public void shutdownServer()
	{
		try {
			if (soc != null)
				soc.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		imgLock.lock();
		imgConV.signal();
		imgLock.unlock();
		
		// wait for threads to end
		try {
			workThrd.join();
			reloadThrd.join();
			threadSignal.countDown();
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private ArrayList<MedicalImage> loadDomainImgs(int nDomainId)
	{
		//Initialize class map
		ArrayList<SecondLevelClass> clses = databaseAPI.getInstance().getClass(nDomainId);
		ArrayList<MedicalImage> imgs = new ArrayList<MedicalImage>();
		
		for (int k = 0; k < clses.size(); k++)
		{
			int limit = 2000;
			String strStDocId = null;
			
			while (true)
			{
				ArrayList<MedicalImage> tmpList = 
						databaseAPI.getInstance().RetrieveImageList(clses.get(k).classId, false,
						strStDocId, limit);
				
				if (null != tmpList && tmpList.size() > 0)
					strStDocId = tmpList.get(tmpList.size()-1).id;
				
				ArrayList<MedicalImage> selList = new ArrayList<MedicalImage>();
				
				//random select
				for (int j = 0; j < tmpList.size(); j++)
				{
					norm.InitialScan(tmpList.get(j).featureV);
					
					int a = (int) tmpList.get(j).imageId;
					int b = (a << 16) + (a >> 16);
					
					if (Math.abs(a^b)%totalMachines == curIndex)
					{
						selList.add(tmpList.get(j));
					}
				}
				
				imgs.addAll(selList);
				
				if (tmpList.size() < limit)
					break;
				
				tmpList = null;
				selList = null;
			}
		}
		
		return imgs;
	}

	
	/***************************** image feature and model information  **********************/
	private ArrayList<MedicalImage> allImgs = new ArrayList<MedicalImage>();
	private ModelManager m_modelMgr = new ModelManager();
	HashMap<Integer, ArrayList<CLASSIFY_ENTITY>> mpDom2DataSet = 
			new HashMap<Integer, ArrayList<CLASSIFY_ENTITY>>();
		
	//classes map
	int m_nTotalClasses = 0;
	/****************************************************************************************/
	
	String xmlcfgfile = "";
	private int totalMachines = 1;
	private int curIndex = 0;
	private Normalizer norm = new Normalizer(imgproc.ImgFeatureExtractionWrapper.TOTAL_DIM);
	
	private Socket soc = null; 
	boolean bStopWorker = false;
	int reloadhours = 15;
	
	ImgRetrievalThrd workThrd = new ImgRetrievalThrd(this);
	ServerReloadThrd reloadThrd = new ServerReloadThrd(this);
	CountDownLatch threadSignal = new CountDownLatch(1);
	
	private Condition imgConV = null;
	private Lock imgLock = null;
}
