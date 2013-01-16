package datamining;

import java.util.ArrayList;

public class ClassifyModel {

	public void setRBFInfo(double dbC, double dbG)
	{
		m_dbRBF_C = dbC;
		m_dbRBF_G = dbG;
	}
	
	public void setLinearInfo(double dbC)
	{
		m_dbLinear_C = dbC;
	}
	
	public void useRBF() { m_bRBF = true; }
	public void useLinear() { m_bRBF = false; }
	
	//ModelParameter parameterTuning();
	
	public void BuildModel(ArrayList<CLASSIFY_ENTITY> dataset)
	{
		if (dataset == null || dataset.size() == 0)
			return;
		
		int nDim = dataset.get(0).vectors.size();
		
		if (m_bRBF)
		{
			LibSVMClassifier cls = new LibSVMClassifier(nDim);
			cls.setC(m_dbRBF_C);
			cls.setG(m_dbRBF_G);
			
			m_pModel = cls;
			cls = null;
			m_pModel.BuildModel(dataset);
		}
		else
		{
			LibLinearClassifier cls = new LibLinearClassifier(nDim);
			cls.setC(m_dbLinear_C);
			
			m_pModel = cls;
			cls = null;
			m_pModel.BuildModel(dataset);
		}
	}
	
	public PROB_ESTIMATION_RES Classify(ArrayList<Double> vectors)
	{
		if (null == m_pModel)
			return null;
		
		return m_pModel.Classify(vectors);
	}
	
	public double crossValidation(ArrayList<CLASSIFY_ENTITY> dataset)
	{
		if (null == m_pModel)
			return -1.0;
		
		return m_pModel.CrossValidation(dataset);
	}
	
	private SVMInterface m_pModel = null;
	private boolean m_bRBF = true;
	private double m_dbRBF_C = 0.03125;
	private double m_dbRBF_G = 0.001;
	private double m_dbLinear_C = 0.0315;
	private double m_dbCurAccuracy = -1.0;
}
