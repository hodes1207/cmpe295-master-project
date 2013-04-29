package ImgRetrieveServer;

import datamining.ClassifyModel;
import java.util.ArrayList;
import datamining.CLASSIFY_ENTITY;

public class ModelTuningThrd extends Thread
{
	public ModelTuningThrd(ArrayList<CLASSIFY_ENTITY> testDataSet, 
			ArrayList<CLASSIFY_ENTITY> buildDataSet, boolean bEnableRbf)
	{
		m_buildDataSet = buildDataSet;
		m_testDataSet = testDataSet;
		
		if (bEnableRbf)
			m_model.enableRBFTuning();
		else
			m_model.disableRBFTuning();
	}
	
	public void run()
	{
		m_model.modelTuning(m_testDataSet, m_buildDataSet);
		m_testDataSet = null;
		m_buildDataSet = null;
	}
	
	public ClassifyModel getModel() { return m_model; }
	
	private ClassifyModel m_model = new ClassifyModel();
	private ArrayList<CLASSIFY_ENTITY> m_testDataSet = null;
	private ArrayList<CLASSIFY_ENTITY> m_buildDataSet = null;
}
