package ServiceInterface;

import java.util.ArrayList;
import java.util.HashMap;

import datamining.CLASSIFY_ENTITY;
import datamining.ClassifyModel;
import datamining.PROB_ESTIMATION_RES;

public class ModelManager {
	
	/************************ Model management *********************************/
	public ModelManager() {}
	
	public void addDomain(int nDomainId)
	{
		synchronized(this)
		{
			m_domainModels.put(nDomainId, new ClassifyModel());
		}
	}
	
	String getModelInfo(int nDomainId)
	{
		synchronized(this)
		{
			if (nDomainId == WHOLE_DOMAIN_ID)
				return m_wholeModel.getModelInfo();
			
			if (!m_domainModels.containsKey(nDomainId))
				return "";
			
			return m_domainModels.get(nDomainId).getModelInfo();
		}
	}
	
	boolean setModelParameter(int nDomainId, boolean bRBF, double rbfc, double rbfg, double linearc)
	{
		synchronized(this)
		{
			if(WHOLE_DOMAIN_ID != nDomainId && !m_domainModels.containsKey(nDomainId))
				return false;
				
			ClassifyModel model = null;
			if (WHOLE_DOMAIN_ID == nDomainId)
				model = m_wholeModel;
			else
				model = m_domainModels.get(nDomainId);
				
			if (bRBF)
			{
				model.useRBF();
				model.setRBFInfo(rbfc, rbfg);
			}
			else 
			{
				model.useLinear();
				model.setLinearInfo(linearc);
			}
		}
		
		return true;
	}
	
	/************************** Model tuning & training *****************************/
	boolean requestTuning(int nDomainId, ArrayList<datamining.CLASSIFY_ENTITY> testDataSet, 
			ArrayList<datamining.CLASSIFY_ENTITY> buildDataSet)
	{
		
		ModelTuningThrd thrd = 
				new ModelTuningThrd(testDataSet, buildDataSet);
		
		synchronized(this)
		{
			m_domainTuning.put(nDomainId, thrd);
		}
		
		thrd.start();
			
		return true;
	}
	
	public boolean replaceModel(int nDomainId, ClassifyModel mod)
	{
		synchronized(this)
		{
			if (nDomainId == WHOLE_DOMAIN_ID)
			{
				m_wholeModel = mod;
				return true;
			}
			
			if (!m_domainModels.containsKey(nDomainId))
				return false;
			
			m_domainModels.put(nDomainId, mod);
			return true;
		}
	}
	
	boolean requestTraining(int nDomainId, datamining.ModelParameter param, 
			ArrayList<CLASSIFY_ENTITY> dataset)
	{
		synchronized(this)
		{
			if (nDomainId != WHOLE_DOMAIN_ID && !m_domainModels.containsKey(nDomainId))
				return false;
			
			if (m_domainTraining.containsKey(nDomainId) && m_domainTraining.get(nDomainId).isAlive())
				return false;
			
			ModelTraingThrd thrd = 
					new ModelTraingThrd(this, param, dataset, nDomainId);
			m_domainTraining.put(nDomainId, thrd);
			thrd.start();
			
			return true;
		}
	}
	
	boolean isTrainingFinished(int nDomainId)
	{
		synchronized(this)
		{
			if (!m_domainTraining.containsKey(nDomainId))
				return false;
			
			return m_domainTraining.get(nDomainId).IsDone();
		}
	}
	
	double getTuningProgress(int nDomainId)
	{
		synchronized(this)
		{
			if (!m_domainTuning.containsKey(nDomainId))
				return 0.0;
			
			return m_domainTuning.get(nDomainId).getModel().getTuningProgress();
		}
	}
	
	String getTuningInfo(int nDomainId)
	{
		synchronized(this)
		{
			if (!m_domainTuning.containsKey(nDomainId))
				return "Error, no tuning process activated for this model";
			
			return m_domainTuning.get(nDomainId).getModel().getTuningInfo();
		}
	}
	
	boolean initialBuildModel(int nDomainId, ArrayList<CLASSIFY_ENTITY> dataset)
	{
		ClassifyModel model = null;
		synchronized(this)
		{
			if (nDomainId != WHOLE_DOMAIN_ID && !m_domainModels.containsKey(nDomainId))
				return false;
			
			if (WHOLE_DOMAIN_ID == nDomainId)
				model = m_wholeModel;
			else
				model = m_domainModels.get(nDomainId);
		}
		
		model.BuildModel(dataset);
		model.crossValidation(dataset);
		
		return true;
	}
	
	//PROB_ESTIMATION_RES res = cls.Classify(entInput.vectors);
	public PROB_ESTIMATION_RES classify(ArrayList<Double> imgFeature, int nDomainId)
	{
		ClassifyModel model = null;
		synchronized(this)
		{
			if (null == imgFeature || (!m_domainModels.containsKey(nDomainId) && nDomainId != WHOLE_DOMAIN_ID))
				return null;
			
			if (nDomainId < 0)
				model = m_wholeModel;
			else
				model = m_domainModels.get(nDomainId);
		}
		
		return model.Classify(imgFeature);
	}
	
	private HashMap<Integer, ClassifyModel> m_domainModels = 
			new HashMap<Integer, ClassifyModel>();
	
	private HashMap<Integer, ModelTraingThrd> m_domainTraining = 
			new HashMap<Integer, ModelTraingThrd>();
	
	private HashMap<Integer, ModelTuningThrd> m_domainTuning = 
			new HashMap<Integer, ModelTuningThrd>();
	
	private ClassifyModel m_wholeModel = new ClassifyModel();
	
	static public int WHOLE_DOMAIN_ID = -1;

}