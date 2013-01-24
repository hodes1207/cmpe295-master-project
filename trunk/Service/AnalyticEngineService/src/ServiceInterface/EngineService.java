package ServiceInterface;

import java.util.ArrayList;
import java.util.HashMap;

import datamining.CLASSIFY_ENTITY;
import datamining.ClassifyModel;
import database.*;

public class EngineService 
{

	//=================== Image management API =========================
	
	boolean Initialized()  { return m_bInited; }

	public ArrayList<Long> GetPicId(int nClass)
	{
		return null;
	}

	public byte[] RetrieveImg(long nId) 
	{
		return null;
	}

	public boolean DeleteImg(long nId) 
	{
		return false;
	}

	public boolean AddImg(int nClass, byte[] byteImg)
	{
		return false;
	}

	//scope: first level classification
	public ArrayList<Integer> GetDomainIds()
	{
		return null;
	}

	//second level classification
	public ArrayList<Integer> GetClass(int nDomianId)
	{
		return null;
	}

	//================== Model tuning API ================================

	public void SetRBFKernelParam(int nScope, double c, double g)
	{
		
	}

	public void SetLinearKernelParam(int nScope, double c)
	{
		
	}

	public int GetAutoTuningFoldNum(int nDomainId)
	{
		return 0;
	}

	public void SetAutoTuningFoldNum(int nDomainId, int nFold)
	{
		
	}

	/*int GetTimeLimite(int nDomainId)
	{
		return 0;
	}

	boolean StopAutoTuning(int nDomainId)
	{
		return false;
	}*/

	public boolean StartAutoTuning(int nDomainId)
	{
		return false;
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

	//============ Recommendation API ====================================
	//return a list of picture ID
	public ArrayList<Long> SimilaritySearch(byte[] img, int nDomainId)
	{
		return null;
	}

	//String format:  classID+��:��+classification percentage, ranked by percentage
	public ArrayList<String> ClassificationEstimation(byte[] img, int nDomainId)
	{
		return null;
	}
	
	//================== Initialization ====================================
	public boolean StartService()
	{
		ArrayList<CLASSIFY_ENTITY> allImgs = new ArrayList<CLASSIFY_ENTITY>();
		
		//build individual model
		ArrayList<Integer> domains = databaseAPI.getDomain();
		for (int i = 0; i < domains.size(); i++)
		{
			int nDomainId = domains.get(i);
			
			ModelParameter param = databaseAPI.getModelParameter(nDomainId);
			if (param == null)
				continue;
			
			ArrayList<MedicalImage> imgs = databaseAPI.RetrieveImageList(nDomainId, -1, false);
			if (null == imgs)
				continue;
			
			m_domainModels.put(nDomainId, new ClassifyModel());
			ArrayList<CLASSIFY_ENTITY> clsEnt = new ArrayList<CLASSIFY_ENTITY>();
			for (int j = 0; j < imgs.size(); j++)
			{
				CLASSIFY_ENTITY e = new CLASSIFY_ENTITY();
				e.nClsId = imgs.get(j).classId;
				e.vectors = imgs.get(j).featureV;
				allImgs.add(e);
				clsEnt.add(e);
			}
			
			//set model parameters
			if (param.bRBF)
			{
				m_domainModels.get(nDomainId).useRBF();
				m_domainModels.get(nDomainId).setRBFInfo(param.dbRBF_c, param.dbRBF_g);
			}
			else 
			{
				m_domainModels.get(nDomainId).useLinear();
				m_domainModels.get(nDomainId).setLinearInfo(param.dbRBF_c);
			}
			
			m_domainModels.get(nDomainId).BuildModel(clsEnt);
			clsEnt = null;
		}
		
		//build the whole model
		m_wholeModel = new ClassifyModel(); 
		m_wholeModel.BuildModel(allImgs);
		
		m_bInited = true;
		
		return m_bInited;
	}
	
	//================== Member variables ==================================
	private HashMap<Integer, ClassifyModel> m_domainModels = new HashMap<Integer, ClassifyModel>();
	private ClassifyModel m_wholeModel = null;
	private boolean m_bInited = false;
	private int m_nWholeDomainId = 0;
}
