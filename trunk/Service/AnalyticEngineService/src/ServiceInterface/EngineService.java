package ServiceInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import imgproc.ImgFeatureExtractionWrapper;
import datamining.CLASSIFY_ENTITY;
import datamining.PROB_ESTIMATION_RES;
import datamining.SemanticMerge;
import database.*;

public class EngineService 
{

	/*******************  Service API group ***********************************/
	//=================== Image management API =========================

	public ArrayList<Long> GetPicId(int nClassId)
	{
		ArrayList<Long> res = databaseAPI.getInstance().GetImageId(nClassId);
		return res;
	}

	public byte[] RetrieveImg(long nImgId) 
	{
		MedicalImage img = databaseAPI.getInstance().RetrieveImage(nImgId);
		return img.image;
	}

	public boolean DeleteImg(int nClassId, long nImgId) 
	{
		boolean res = databaseAPI.getInstance().DeleteImage(nImgId);
		int nDomainId = (nClassId >> 16);
		
		if (res)
		{
			for (int i = 0; i < m_imgs.get(nDomainId).size(); i++)
			{
				if (m_imgs.get(nDomainId).get(i).imageId == nImgId)
				{
					m_imgs.get(nDomainId).remove(i);
					break;
				}
			}
		}
			
		return res;
	}

	public boolean AddImg(int nClassId, long nImgId, byte[] byteImg)
	{
		int nDomainId = (nClassId >> 16);
		MedicalImage img = new MedicalImage();
		img.domainId = nDomainId;
		img.classId = nClassId;
		img.imageId = nImgId;
		img.image = byteImg;
		img.featureV = new ArrayList<Double>();
		
		double[] vectors = new double[ImgFeatureExtractionWrapper.TOTAL_DIM];
		ImgFeatureExtractionWrapper.extractFeature(byteImg, vectors);
		for (int i = 0; i < vectors.length; i++)
			img.featureV.add(vectors[i]);
		
		//the feature vector stored in the database is not normalized
		boolean bSuc = databaseAPI.getInstance().AddImage(img);
		
		if (bSuc)
		{
			synchronized(this)
			{
				m_imgs.get(img.domainId).add(img);
			}
			
			//the feature vector in memory is normalized
			m_nlzr.normalizeVector(img.featureV);
		}
		
		return bSuc;
	}

	//scope: first level classification
	public ArrayList<Domain> GetDomain()
	{
		ArrayList<Domain> res = databaseAPI.getInstance().getDomain();
		
		return res;
	}

	//second level classification
	public ArrayList<SecondLevelClass> GetClasses(int nDomianId)
	{
		ArrayList<SecondLevelClass> res = databaseAPI.getInstance().getClass(nDomianId);
		
		return res;
	}

	//================== Model tuning API ================================

	public boolean SetRBFKernelParam(int nDomainId, double c, double g, int nMaxSamples)
	{
		MedicalParameter param = databaseAPI.getInstance().getModelParameter(nDomainId);
		if (null == param)
			return false;
		
		param.bRBF = true;
		param.dbRBF_c = c;
		param.dbRBF_g = g;
		param.nMaxSampleNum = nMaxSamples;
		
		databaseAPI.getInstance().setModelParameter(nDomainId, param);
		return true;
	}

	public boolean SetLinearKernelParam(int nDomainId, double c, int nMaxSamples)
	{
		MedicalParameter param = databaseAPI.getInstance().getModelParameter(nDomainId);
		if (param == null)
			return false;
		
		param.bRBF = false;
		param.dbLinear_c = c;
		param.nMaxSampleNum = nMaxSamples;
		
		databaseAPI.getInstance().setModelParameter(nDomainId, param);
		
		return true;
	}

	public int GetAutoTuningFoldNum(int nDomainId)
	{
		MedicalParameter param = databaseAPI.getInstance().getModelParameter(nDomainId);
		
		return null == param ? 0 : param.nFold;
	}

	public boolean SetAutoTuningFoldNum(int nDomainId, int nFold)
	{
		MedicalParameter param = databaseAPI.getInstance().getModelParameter(nDomainId);
		if (param == null)
			return false;
		
		param.nFold = nFold;
		
		databaseAPI.getInstance().setModelParameter(nDomainId, param);
		return true;
	}
	
	public double getAutoTuningProgress(int nDomainId)
	{
		return m_modelMgr.getTuningProgress(nDomainId);
	}
	
	public String getAutoTuningInfo(int nDomainId)
	{
		return m_modelMgr.getTuningInfo(nDomainId);
	}
	
	//not in the auto tuning process
	public String GetCurrentModelInfo(int nDomainId)
	{
		return m_modelMgr.getModelInfo(nDomainId);
	}
	
	/*void stopTraining(int nDomainId)
	void stopTuning(int nDomainId)*/

	public boolean StartAutoTuning(int nDomainId)
	{
		MedicalParameter param = databaseAPI.getInstance().getModelParameter(nDomainId);
		if (null == param || (nDomainId != ModelManager.WHOLE_DOMAIN_ID && !m_imgs.containsKey(nDomainId)))
			return false;
		
		ArrayList<MedicalImage> imgSample = new ArrayList<MedicalImage>();
		synchronized(this)
		{
			ArrayList<MedicalImage> imgRef = null;
			if (nDomainId != ModelManager.WHOLE_DOMAIN_ID)
				imgRef = m_imgs.get(nDomainId);
			else
			{
				imgRef = new ArrayList<MedicalImage>();
				Iterator<Integer> iterator = m_imgs.keySet().iterator();
				while (iterator.hasNext()) 
				{
					int nId = (int)iterator.next();
					imgRef.addAll(m_imgs.get(nId));
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
		}
		
		Collections.shuffle(imgSample);
		ArrayList<datamining.CLASSIFY_ENTITY> buildDataSet = new ArrayList<datamining.CLASSIFY_ENTITY>();
		ArrayList<datamining.CLASSIFY_ENTITY> testDataSet = new ArrayList<datamining.CLASSIFY_ENTITY>();
		for (int i = 0; i < imgSample.size(); i++)
		{
			datamining.CLASSIFY_ENTITY ent = new datamining.CLASSIFY_ENTITY();
			ent.nClsId = imgSample.get(i).classId;
			ent.vectors = imgSample.get(i).featureV;
			
			if (i < imgSample.size()/param.nFold)
				testDataSet.add(ent);
			else
				buildDataSet.add(ent);
		}
		
		m_modelMgr.requestTuning(nDomainId, testDataSet, buildDataSet);
		
		return true;
	}
	
	public boolean startTraining(int nDomainId)
	{
		MedicalParameter param = databaseAPI.getInstance().getModelParameter(nDomainId);
		if (null == param || !m_imgs.containsKey(nDomainId))
			return false;
		
		ArrayList<datamining.CLASSIFY_ENTITY> buildDataSet = 
				new ArrayList<datamining.CLASSIFY_ENTITY>();
		synchronized(this)
		{
			ArrayList<MedicalImage> imgRef = m_imgs.get(nDomainId);
			
			for (int i = 0; i < imgRef.size(); i++)
			{
				datamining.CLASSIFY_ENTITY ent = new datamining.CLASSIFY_ENTITY();
				ent.nClsId = imgRef.get(i).classId;
				ent.vectors = imgRef.get(i).featureV;
				buildDataSet.add(ent);
			}
		}
		
		//transition between to type of parameters
		datamining.ModelParameter paramNew = new datamining.ModelParameter();
		paramNew.bRBFKernel = param.bRBF;
		paramNew.dbLinear_C = param.dbLinear_c;
		paramNew.dbRBF_C = param.dbRBF_c;
		paramNew.dbRBF_G = param.dbRBF_g;
		
		m_modelMgr.requestTraining(nDomainId, paramNew, buildDataSet);
		
		return true;
	}

	//================== Recommendation API ====================================
	//return a list of picture ID (nNum pictures)
	public ArrayList<Long> SimilaritySearch(byte[] byteImg, int nNum)
	{
		//get normalized vector
		double[] vectors = new double[ImgFeatureExtractionWrapper.TOTAL_DIM];
		ImgFeatureExtractionWrapper.extractFeature(byteImg, vectors);
		
		ArrayList<Double> featureV = new ArrayList<Double>();
		for (int i = 0; i < vectors.length; i++)
			featureV.add(vectors[i]);
		
		m_nlzr.normalizeVector(featureV);
		
		//get all image features
		ArrayList<MedicalImage> allImgs = new ArrayList<MedicalImage>();
		
		synchronized(this)
		{
			Iterator<Integer> iterator = m_imgs.keySet().iterator();
			while (iterator.hasNext()) 
			{
				int nId = (int)iterator.next();
				ArrayList<MedicalImage> lst = m_imgs.get(nId);
				allImgs.addAll(lst);
			}
		}
		
		//Append semantic vector to input image
		PROB_ESTIMATION_RES res = m_modelMgr.classify(featureV, ModelManager.WHOLE_DOMAIN_ID);
		SemanticMerge sm = new SemanticMerge(ImgFeatureExtractionWrapper.TOTAL_DIM, m_nTotalClasses);
		sm.merge(featureV, res);
				
		for (int i = 0; i < allImgs.size(); i++)
		{
			synchronized(this)
			{
				if (!m_clsIndexMap.containsKey(allImgs.get(i).classId))
					continue;
				
				sm.merge(allImgs.get(i).featureV, m_clsIndexMap.get(allImgs.get(i).classId));
			}
		}
		
		ImgFeatureComparator comp = new ImgFeatureComparator(featureV);
		Collections.sort(allImgs, comp);
		
		int nRetNum = allImgs.size();
		if (nNum > 0 && nNum < allImgs.size())
			nRetNum = nNum;
		
		ArrayList<Long> retList = new ArrayList<Long>();
		for (int i = 0; i < nRetNum; i++)
			retList.add(allImgs.get(i).imageId);
		
		return retList;
	}

	//String format:  classID+¡±:¡±+classification percentage, ranked by percentage
	public String classificationEstimation(byte[] img, int nDomainId)
	{
		//get normalized vector
		double[] vectors = new double[ImgFeatureExtractionWrapper.TOTAL_DIM];
		ImgFeatureExtractionWrapper.extractFeature(img, vectors);
				
		ArrayList<Double> featureV = new ArrayList<Double>();
		for (int i = 0; i < vectors.length; i++)
			featureV.add(vectors[i]);
				
		m_nlzr.normalizeVector(featureV);
		
		PROB_ESTIMATION_RES res = m_modelMgr.classify(featureV, nDomainId);
		if (null == res)		
			return null;
		
		String strRet = "Class probability estimation:  \n";
		for (int i = 0; i < res.probRes.size(); i++)
		{
			int nClsId = res.probRes.get(i).nClsId;
			if (!m_clsNameMap.containsKey(nClsId))
				continue;
			
			String strClsName = m_clsNameMap.get(nClsId);
			strRet += strClsName;
			strRet += " ====================> ";
			strRet += Double.toString(res.probRes.get(i).dbProb*100);
			strRet += "% \n";
		}
		
		return strRet;
	}
	
	//================== Initialize status API =================================
	public double getInitProgress()
	{
		return m_dbInitProgress;
	}
	/************************************************************************************/
	
	
	
	/************************  miscellaneous functions **********************************/
	public void setInitialProgress(double dbProg) 
	{ 
		m_dbInitProgress = dbProg; 
	}
	
	public void addDomain(int nDomainId)
	{
		m_modelMgr.addDomain(nDomainId);
	}
	
	public void addImage(int nDomainId, MedicalImage img)
	{
		if (!m_imgs.containsKey(nDomainId))
			m_imgs.put(nDomainId, new ArrayList<MedicalImage>());
		
		m_imgs.get(nDomainId).add(img);
	}
	
	public boolean setModelParameter(int nDomainId, boolean bRBF, double rbfc, double rbfg, double linearc)
	{
		return m_modelMgr.setModelParameter(nDomainId, bRBF, rbfc, rbfg, linearc);
	}
	
	public boolean initialBuildModel(int nDomainId, ArrayList<CLASSIFY_ENTITY> dataset)
	{
		return m_modelMgr.initialBuildModel(nDomainId, dataset);
	}
	
	public void startService()
	{
		databaseAPI.getInstance().initDBInstance("domainInfoTest", "classInfoTest", 
				"medicalImageTest", "http://localhost:5984");
		ServiceInitThrd initThrd = new ServiceInitThrd(this);
		initThrd.start();
	}
	
	public void appendClass(int nClsId, int nIndex, String strClsName)
	{
		synchronized(this)
		{
			m_nTotalClasses++;
			m_clsIndexMap.put(nClsId, nIndex);
			m_clsNameMap.put(nClsId, strClsName);
		}
	}
	
	public void reNormalize()
	{
		synchronized(this)
		{
			Iterator<Integer> iterator = m_imgs.keySet().iterator();
			while (iterator.hasNext()) 
			{
				int nId = (int)iterator.next();
				ArrayList<MedicalImage> lst = m_imgs.get(nId);
				
				for (int i = 0; i < lst.size(); i++)
					m_nlzr.InitialScan(lst.get(i).featureV);
			}
			
			iterator = m_imgs.keySet().iterator();
			while (iterator.hasNext()) 
			{
				int nId = (int)iterator.next();
				ArrayList<MedicalImage> lst = m_imgs.get(nId);
				
				for (int i = 0; i < lst.size(); i++)
					m_nlzr.normalizeVector(lst.get(i).featureV);
			}
		}
	}
	
	//================== Member variables ==================================
	
	//image content is not preserved (MedicalImage.image == null)
	//the "MedicalImage" is read only
	private HashMap<Integer, ArrayList<MedicalImage>> m_imgs = 
			new HashMap<Integer, ArrayList<MedicalImage>>();
	
	private double m_dbInitProgress = 0.0;
	private ModelManager m_modelMgr = new ModelManager();
	
	//classes map
	int m_nTotalClasses = 0;
	HashMap<Integer, Integer> m_clsIndexMap = new HashMap<Integer, Integer>();
	HashMap<Integer, String> m_clsNameMap = new HashMap<Integer, String>();
	
	//Normalizer
	datamining.Normalizer m_nlzr 
		= new datamining.Normalizer(ImgFeatureExtractionWrapper.TOTAL_DIM);
}
