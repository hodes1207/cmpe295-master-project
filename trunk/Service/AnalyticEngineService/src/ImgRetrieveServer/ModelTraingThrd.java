package ImgRetrieveServer;

import java.util.ArrayList;

import datamining.CLASSIFY_ENTITY;
import datamining.ClassifyModel; 

public class ModelTraingThrd  extends Thread
{
	public ModelTraingThrd(ModelManager modMgr, datamining.ModelParameter param, 
			ArrayList<CLASSIFY_ENTITY> dataset, int nDomainId)
	{
		m_modelMgr = modMgr;
		m_modleParam = param;
		m_dataset = dataset;
		m_nDomainId = nDomainId;
	}
	
	public boolean isTrainingInProgress() { return m_bTrainingInProgress; }
	
	public void run()
	{
		ClassifyModel model = new ClassifyModel();
		if (m_modleParam.bRBFKernel)
		{
			model.useRBF();
			model.setRBFInfo(m_modleParam.dbRBF_C, m_modleParam.dbRBF_G);
		}
		else
		{
			model.useLinear();
			model.setLinearInfo(m_modleParam.dbLinear_C);
		}
		
		System.out.println(" ======== Start model training ======== ");
		m_bTrainingInProgress = true;
		model.BuildModel(m_dataset);
		model.crossValidation(m_dataset);
		System.out.println(" ======== Model training finished. ========");
		
		m_modelMgr.replaceModel(m_nDomainId, model);
		
		m_dataset = null;
		m_bTrainingInProgress = false;
	}
	
	private int m_nDomainId = -100;
	private ModelManager m_modelMgr = null;
	private ArrayList<CLASSIFY_ENTITY> m_dataset;
	private datamining.ModelParameter m_modleParam = null;
	private boolean m_bTrainingInProgress = false;
}
