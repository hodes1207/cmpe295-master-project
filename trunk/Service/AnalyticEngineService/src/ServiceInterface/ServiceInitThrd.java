package ServiceInterface;

import java.util.ArrayList;
import java.util.HashMap;

import database.Domain;
import database.MedicalImage;
import database.MedicalParameter;
import database.SecondLevelClass;
import database.databaseAPI;
import datamining.CLASSIFY_ENTITY;

public class ServiceInitThrd  extends Thread {

	public ServiceInitThrd(EngineService serv)
	{
		m_serv = serv;
	}
	
	public void run()
	{
		ArrayList<CLASSIFY_ENTITY> allImgs = new ArrayList<CLASSIFY_ENTITY>();
		
		//build individual model
		ArrayList<Domain> domains = databaseAPI.getInstance().getDomain();
		double dbStep = 1.0/(1 + domains.size());
		
		int nClsIndex = 0;
		HashMap<Integer, ArrayList<CLASSIFY_ENTITY>> mpDom2DataSet = new HashMap<Integer, ArrayList<CLASSIFY_ENTITY>>();
		for (int i = 0; i < domains.size(); i++)
		{
			int nDomainId = domains.get(i).domainId;
			if (nDomainId == ModelManager.WHOLE_DOMAIN_ID)
				continue;
			
			//Initialize class map
			ArrayList<SecondLevelClass> clses = databaseAPI.getInstance().getClass(nDomainId);
			for (int j = 0; j < clses.size(); j++)
				m_serv.appendClass(clses.get(j).classId, nClsIndex++, clses.get(j).className);
			
			//databaseAPI.RetrieveImageList(nDomainId, -1, false);
			//if (null == imgs) continue;
			ArrayList<MedicalImage> imgs = new ArrayList<MedicalImage>();
			for (int k = 0; k < clses.size(); k++)
			{
				imgs.addAll(databaseAPI.getInstance().RetrieveImageList(clses.get(k).classId, false));
			}
			
			m_serv.addDomain(nDomainId);
			
			ArrayList<CLASSIFY_ENTITY> clsEnt = new ArrayList<CLASSIFY_ENTITY>();
			for (int j = 0; j < imgs.size(); j++)
			{
				CLASSIFY_ENTITY e = new CLASSIFY_ENTITY();
				e.nClsId = imgs.get(j).classId;
				e.vectors = imgs.get(j).featureV;
				allImgs.add(e);
				clsEnt.add(e);
				
				//Initialize image list
				m_serv.addImage(nDomainId, imgs.get(j));
			}
			
			mpDom2DataSet.put(nDomainId, clsEnt);
		}
		
		m_serv.reNormalize();
		m_serv.setInitialProgress(m_serv.getInitProgress()+ dbStep);
		
		for (int i = 0; i < domains.size(); i++)
		{
			int nDomainId = domains.get(i).domainId;
			if (nDomainId == ModelManager.WHOLE_DOMAIN_ID)
				continue;
			
			MedicalParameter param = databaseAPI.getInstance().getModelParameter(nDomainId);
			if (param == null)
				continue;
			
			//set model parameters
			m_serv.setModelParameter(nDomainId, param.bRBF, param.dbRBF_c, 
					param.dbRBF_g, param.dbLinear_c);
			
			m_serv.initialBuildModel(nDomainId, mpDom2DataSet.get(nDomainId));
			m_serv.setInitialProgress(m_serv.getInitProgress()+ dbStep);
		}
		
		//build the whole model
		MedicalParameter param = databaseAPI.getInstance().getModelParameter(ModelManager.WHOLE_DOMAIN_ID);
		if (param == null)
			return;
		
		m_serv.setModelParameter(ModelManager.WHOLE_DOMAIN_ID, param.bRBF, param.dbRBF_c, 
				param.dbRBF_g, param.dbLinear_c);
		
		m_serv.initialBuildModel(ModelManager.WHOLE_DOMAIN_ID, allImgs);
		
		m_serv.setInitialProgress(1.0);
		
		return;
	}
	
	private EngineService m_serv = null;
}
