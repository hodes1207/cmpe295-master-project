package ImgRetrieveServer;
import imgproc.ImgFeatureExtractionWrapper;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import MessageLayer.ImgDisResEntry;
import MessageLayer.ImgRetrieveInitMsg;
import MessageLayer.KNNSearchResp;
import MessageLayer.KNNsearchMsg;
import ServiceInterface.ImgFeatureComparator;
import ServiceInterface.ModelManager;

import database.*;
import datamining.Normalizer;

public class ImgRetrieveServer {
	
	public boolean initService(String xmlCfgFile)
	{
		//parse configuration file
		try 
		{
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
			
			databaseAPI.getInstance().initDBInstance(domainDBName, classDBName, 
					medicalImageDBName, DBUrl);
			
			ArrayList<SecondLevelClass> clses = databaseAPI.getInstance().getClass(ModelManager.WHOLE_DOMAIN_ID);
			
			//setup connection to service
			soc = new Socket(strIP, port);
			System.out.println("Connected");
			
			ObjectInputStream socIn = new ObjectInputStream(soc.getInputStream());
			ImgRetrieveInitMsg initMsg = (ImgRetrieveInitMsg)socIn.readObject();
			
			//get total machine  
			totalMachines = initMsg.totalMachines;
			curIndex = initMsg.curIndex;
			
			System.out.println(curIndex);
			
			for (int i = 0; i < clses.size(); i++)
			{
				int clsId = clses.get(i).classId;
				int limit = 2000;
				String strStDocId = null;
				
				while (true)
				{
					ArrayList<MedicalImage> tmpList = 
							databaseAPI.getInstance().RetrieveImageList(clsId, false, strStDocId, limit);
						
					if (null != tmpList && tmpList.size() > 0)
						strStDocId = tmpList.get(tmpList.size()-1).id;
				
					for (int j = 0; j < tmpList.size(); j++)
					{
						norm.InitialScan(tmpList.get(j).featureV);
						
						int a = (int) tmpList.get(j).imageId;
						int b = (a << 16) + (a >> 16);
						
						if (Math.abs(a^b)%totalMachines == curIndex)
						{
							imgs.add(tmpList.get(j).clone());
						}
					}
					
					if (tmpList.size() < limit)
						break;
						
					tmpList = null;
					System.gc();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			for (int i = 0; i < imgs.size(); i++)
			{
				norm.normalizeVector(imgs.get(i).featureV);
			}
		} 
		catch ( Exception e1) //ParserConfigurationException | SAXException | IOException
		{
			//e1.printStackTrace();
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
	
	public void stop() throws InterruptedException
	{
		bStopServer = true;
		reloadThrd.join();
		
		bStopWorker = true;
		workThrd.join();
		
		threadSignal.countDown();
	}
	
	void reloadThrdFunc() throws IOException, InterruptedException
	{
		while (!bStopServer)
		{
			Thread.sleep(1000*reloadhours*60*60);
			bStopWorker = true;
			soc.close();
			
			workThrd.join();
			
			//reset members
			imgs = new ArrayList<MedicalImage>();
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
				KNNsearchMsg inMsg = (KNNsearchMsg)socIn.readObject();
					
				KNNSearchResp resp = new KNNSearchResp();
				resp.k = inMsg.k;
				resp.msgId = inMsg.msgId;
				resp.res = searchKNN(inMsg.feature, inMsg.k);
					
				System.out.println("Search finished");
					
				ObjectOutputStream socOut = new ObjectOutputStream(soc.getOutputStream());
				socOut.writeObject(resp);
				System.out.println("Wrote in client");
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
	private ArrayList<ImgDisResEntry> searchKNN(double[] vectors, int k)
	{
		if (null == vectors || vectors.length != ImgFeatureExtractionWrapper.TOTAL_DIM || k <= 0)
			return new ArrayList<ImgDisResEntry>();
		
		ArrayList<Double> featureV = new ArrayList<Double>();
		for (int i = 0; i < vectors.length; i++)
			featureV.add(vectors[i]);
		
		norm.normalizeVector(featureV);
		
		ImgFeatureComparator comp = new ImgFeatureComparator(featureV, true);
		Queue<MedicalImage> knnQue =  new PriorityQueue<MedicalImage>(k, comp); 
		for (int i = 0; i < imgs.size(); i++)
		{
			if (knnQue.size() < k)
				knnQue.add(imgs.get(i));
			else
			{
				if (comp.compare(knnQue.peek(), imgs.get(i)) < 0)
				{
					knnQue.remove();
					knnQue.add(imgs.get(i));
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
			soc.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		bStopWorker = true;
		
		// wait for threads to end
		try {
			workThrd.join();
			reloadThrd.join();
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	String xmlcfgfile = "";
	private ArrayList<MedicalImage> imgs = new ArrayList<MedicalImage>();
	private int totalMachines = 1;
	private int curIndex = 0;
	private Normalizer norm = new Normalizer(imgproc.ImgFeatureExtractionWrapper.TOTAL_DIM);
	
	private Socket soc = null; 
	boolean bStopWorker = false;
	boolean bStopServer = false;
	int reloadhours = 15;
	
	ImgRetrievalThrd workThrd = new ImgRetrievalThrd(this);
	ServerReloadThrd reloadThrd = new ServerReloadThrd(this);
	CountDownLatch threadSignal = new CountDownLatch(1);
}
