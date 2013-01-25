package ServiceInterface;

import java.util.ArrayList;
import java.util.HashMap;
import imgproc.ImgFeatureExtractionWrapper;
import datamining.CLASSIFY_ENTITY;
import datamining.ClassifyModel;
import database.*;

public class EngineService 
{

	//=================== Image management API =========================
	
	boolean Initialized()  { return m_bInited; }

	public ArrayList<Long> GetPicId(int nDomainId, int nClassId)
	{
		ArrayList<Long> res = databaseAPI.GetImageId(nDomainId, nClassId);
		return res;
	}

	public byte[] RetrieveImg(long nImgId) 
	{
		MedicalImage img = databaseAPI.RetrieveImage(nImgId);
		return img.image;
	}

	public boolean DeleteImg(int nDomainId, int nClassId, long nImgId) 
	{
		boolean res = databaseAPI.DeleteImage(nImgId);
		
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

	public boolean AddImg(int nDomainId, int nClassId, long nImgId, byte[] byteImg)
	{
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
		
		boolean bSuc = databaseAPI.AddImage(img);
		if (bSuc)
			m_imgs.get(img.domainId).add(img);
		
		return bSuc;
	}

	//scope: first level classification
	public ArrayList<Domain> GetDomain()
	{
		ArrayList<Domain> res = databaseAPI.getDomain();
		
		return res;
	}

	//second level classification
	public ArrayList<SecondLevelClass> GetClasses(int nDomianId)
	{
		ArrayList<SecondLevelClass> res = databaseAPI.getClass(nDomianId);
		
		return res;
	}

	//================== Model tuning API ================================

	public void SetRBFKernelParam(int nDomainId, double c, double g, int nMaxSamples)
	{
		ModelParameter param = new ModelParameter();
		param.bRBF = true;
		param.dbRBF_c = c;
		param.dbRBF_g = g;
		param.nMaxSampleNum = nMaxSamples;
		
		databaseAPI.setModelParameter(nDomainId, param);
	}

	public void SetLinearKernelParam(int nDomainId, double c, int nMaxSamples)
	{
		ModelParameter param = databaseAPI.getModelParameter(nDomainId);
		if (param == null)
			param = new ModelParameter();
		
		param.bRBF = false;
		param.dbLinear_c = c;
		param.nMaxSampleNum = nMaxSamples;
		
		databaseAPI.setModelParameter(nDomainId, param);
	}

	public int GetAutoTuningFoldNum(int nDomainId)
	{
		ModelParameter param = databaseAPI.getModelParameter(nDomainId);
		
		return null == param ? 0 : param.nFold;
	}

	public void SetAutoTuningFoldNum(int nDomainId, int nFold)
	{
		ModelParameter param = databaseAPI.getModelParameter(nDomainId);
		if (param == null)
			param = new ModelParameter();
		
		param.nFold = nFold;
		
		databaseAPI.setModelParameter(nDomainId, param);
	}

	void stopTraining(int nDomainId)
	{
		
	}
	
	void stopTuning(int nDomainId)
	{
		
	}

	public boolean StartAutoTuning(int nDomainId)
	{
		return false;
	}
	
	double getAutoTuningProgress(int nDomainId)
	{
		return 0.0;
	}
	
	String getAutoTuningInfo()
	{
		return "";
	}

	// during auto tuning process
	public ArrayList<String>GetAutoTunningInfo(int nDomainId)
	{
		return null;
	}

	//not in the auto tuning process
	public ArrayList<String> GetCurrentModelInfo(int nDomainId)
	{
		return null;
	}

	//================== Recommendation API ====================================
	//return a list of picture ID
	public ArrayList<Long> SimilaritySearch(byte[] img, int nDomainId)
	{
		return null;
	}

	//String format:  classID+¡±:¡±+classification percentage, ranked by percentage
	public ArrayList<String> ClassificationEstimation(byte[] img, int nDomainId)
	{
		return null;
	}
	
	//======================= Initialization ====================================
	public boolean StartService()
	{
		ArrayList<CLASSIFY_ENTITY> allImgs = new ArrayList<CLASSIFY_ENTITY>();
		
		//build individual model
		ArrayList<Domain> domains = databaseAPI.getDomain();
		for (int i = 0; i < domains.size(); i++)
		{
			int nDomainId = domains.get(i).domainId;
			
			ModelParameter param = databaseAPI.getModelParameter(nDomainId);
			if (param == null)
				continue;
			
			ArrayList<MedicalImage> imgs = databaseAPI.RetrieveImageList(nDomainId, -1, false);
			if (null == imgs)
				continue;
			
			m_modelMgr.addDomain(nDomainId);
			m_imgs.put(nDomainId, new ArrayList<MedicalImage>());
			
			ArrayList<CLASSIFY_ENTITY> clsEnt = new ArrayList<CLASSIFY_ENTITY>();
			for (int j = 0; j < imgs.size(); j++)
			{
				CLASSIFY_ENTITY e = new CLASSIFY_ENTITY();
				e.nClsId = imgs.get(j).classId;
				e.vectors = imgs.get(j).featureV;
				allImgs.add(e);
				clsEnt.add(e);
				
				m_imgs.get(nDomainId).add(imgs.get(j));
			}
			
			//set model parameters
			m_modelMgr.setModelParameter(nDomainId, param.bRBF, param.dbRBF_c, 
					param.dbRBF_g, param.dbLinear_c);
			
			m_modelMgr.BuildModel(nDomainId, clsEnt);
			clsEnt = null;
		}
		
		//build the whole model
		ModelParameter param = databaseAPI.getModelParameter(ModelManager.WHOLE_DOMAIN_ID);
		if (param == null)
		{
			m_bInited = false;
			return false;
		}
		
		m_modelMgr.setModelParameter(ModelManager.WHOLE_DOMAIN_ID, param.bRBF, param.dbRBF_c, 
				param.dbRBF_g, param.dbLinear_c);
		
		m_modelMgr.BuildModel(ModelManager.WHOLE_DOMAIN_ID, allImgs);
		m_bInited = true;
		
		return m_bInited;
	}
	
	//================== Member variables ==================================
	
	//image content is not preserved (MedicalImage.image == null)
	private HashMap<Integer, ArrayList<MedicalImage>> m_imgs = 
			new HashMap<Integer, ArrayList<MedicalImage>>();
	
	private boolean m_bInited = false;
	
	private ModelManager m_modelMgr = new ModelManager();
}
