package datamining;

import java.util.ArrayList;
import java.util.Collections;

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
	
	//used by multi-thread
	public ModelTuneResult modelTuning(ArrayList<CLASSIFY_ENTITY> testDataSet, 
			ArrayList<CLASSIFY_ENTITY> buildDataSet)
	{
		setTuningFlag(true);
		setTuningProgress(0.0);
		m_strTuningInfo = "Start parameter tuning .....";
		
		int nDim = buildDataSet.get(0).vectors.size();
		
		LibSVMClassifier rbfModel = new LibSVMClassifier(nDim);
		RBFTuningParam param1 = new RBFTuningParam();
		param1.DefaultInit();
		
		LibLinearClassifier linearModel = new LibLinearClassifier(nDim);
		LinearTuningParam param2 = new LinearTuningParam();
		param2.DefaultInit();
		
		double dbTotalTuneTimes = param1.matrixC.length * param1.matrixG.length + param2.matrixC.length;
		double dbCurTunedTimes = 0;
		
		// Tune RBF model
		{
			for (int i = 0; i < param1.matrixC.length; i++)
			{
				for (int j = 0; j < param1.matrixG.length; j++)
				{
					double c = param1.matrixC[i];
					double g = param1.matrixG[j];
					
					rbfModel.setC(c);
					rbfModel.setG(g);
					
					rbfModel.BuildModel(buildDataSet);
					param1.matrixRes[i][j] = rbfModel.CrossValidation(testDataSet);
					
					//Set tuning progress
					dbCurTunedTimes += 1.0;
					setTuningProgress(dbCurTunedTimes/dbTotalTuneTimes);
					
					//Append tuning information
					appendTuningInfo(param1.getTuneInfo(i, j));
				}
			}
			
			int indexMaxC = 0;
			int indexMaxG = 0;
			double dbBestAccuracy = -1;
			for (int i = 0; i < param1.matrixC.length; i++)
			{
				for (int j = 0; j < param1.matrixG.length; j++)
				{
					if (param1.matrixRes[i][j] > dbBestAccuracy)
					{
						indexMaxC = i;
						indexMaxG = j;
						dbBestAccuracy = param1.matrixRes[i][j];
					}
				}
			}
			
			param1.dbBestC = param1.matrixC[indexMaxC];
			param1.dbBestG = param1.matrixG[indexMaxG];
			param1.dbBestAccuracy = dbBestAccuracy;
		}
		
		//Tune linear model
		{
			for (int i = 0; i < param2.matrixC.length; i++)
			{
				double c = param2.matrixC[i];
				linearModel.setC(c);
					
				linearModel.BuildModel(buildDataSet);
				param2.matrixRes[i] = linearModel.CrossValidation(testDataSet);
				
				//Set tuning progress
				dbCurTunedTimes += 1.0;
				setTuningProgress(dbCurTunedTimes/dbTotalTuneTimes);
				
				//Append tuning information
				appendTuningInfo(param2.getTuneInfo(i));
			}
			
			int indexMaxC = 0;
			double dbBestAccuracy = -1;
			for (int i = 0; i < param2.matrixC.length; i++)
			{
				if (param2.matrixRes[i] > dbBestAccuracy)
				{
					indexMaxC = i;
					dbBestAccuracy = param2.matrixRes[i];
				}
			}
			
			param2.dbBestC = param2.matrixC[indexMaxC];
			param2.dbBestAccuracy = dbBestAccuracy;
		}
		
		//Finished tuning, set tuning result
		ModelTuneResult res = new ModelTuneResult();
		if (param1.dbBestAccuracy > param2.dbBestAccuracy)
		{
			res.m_bRBF = true;
			res.m_dbRBF_C = param1.dbBestC;
			res.m_dbRBF_G = param1.dbBestG;
		}
		else
		{
			res.m_bRBF = false;
			res.m_dbRBF_C = param2.dbBestC;
		}
		
		appendTuningInfo(res.getResInfo());
		
		/*********************** print tuning result **************************
		System.out.println("\n *************** RBF Tuning **************** \n");
		
		for (int i = 0; i < param1.matrixC.length; i++)
		{
			for (int j = 0; j < param1.matrixG.length; j++)
			{
				System.out.println(param1.getTuneInfo(i, j));
			}
		}
		
		System.out.println(param1.getBestRes());
		
		System.out.println("\n ************** Linear Tuning *************** \n");
		
		for (int i = 0; i < param2.matrixC.length; i++)
		{
			System.out.println(param2.getTuneInfo(i));
		}
		
		System.out.printf(param2.getBestRes());
		
		/**********************************************************************/
		
		setTuningFlag(false);
		
		return res;
	}
	
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
		
		PROB_ESTIMATION_RES res = m_pModel.Classify(vectors);
		
		ClassificationResComparator comp = new ClassificationResComparator();
		Collections.sort(res.probRes, comp);
		
		return res;
	}
	
	public String getModelInfo()
	{
		String strInfo = "";
		if (m_bRBF)
		{
			strInfo += "Model is using RBF kernel \n";
			
			strInfo += "Parameter C: ";
			strInfo += Double.toString(m_dbRBF_C);
			strInfo += "  \n";
			
			strInfo += "Parameter G: ";
			strInfo += Double.toString(m_dbRBF_G);
			strInfo += "  \n";
		}
		else
		{
			strInfo += "Model is using lieanr kernel \n";
			
			strInfo += "Parameter C: ";
			strInfo += Double.toString(m_dbLinear_C);
			strInfo += "  \n";
		}
		
		if (m_dbCurAccuracy < 0.0)
			strInfo += "Model accuracy has not been evaluate. \n";
		else
		{
			strInfo += "Model accuracy: ";
			strInfo += Double.toString(m_dbCurAccuracy);
			strInfo += "  \n";
		}
		
		return strInfo;
	}
	
	public double crossValidation(ArrayList<CLASSIFY_ENTITY> dataset)
	{
		if (null == m_pModel)
			return -1.0;
		
		m_dbCurAccuracy = m_pModel.CrossValidation(dataset);
		
		return m_dbCurAccuracy;
	}
	
	private SVMInterface m_pModel = null;
	private boolean m_bRBF = true;
	private double m_dbRBF_C = 0.03125;
	private double m_dbRBF_G = 0.001;
	private double m_dbLinear_C = 0.0315;
	private double m_dbCurAccuracy = -1.0;
	
	/******************** Model tuning related operation **************************/
	public boolean isTuning()
	{
		boolean bRet = false;
		synchronized(this)
		{
			bRet = m_bIsTuning;
		}
		
		return bRet;
	}
	
	private void setTuningFlag(boolean bTuning)
	{
		synchronized(this)
		{
			m_bIsTuning = bTuning;
		}
	}
	
	public double getTuningProgress()
	{
		synchronized(this)
		{
			return m_dbTuningProgress;
		}
	}
	
	private void setTuningProgress(double dbProg)
	{
		synchronized(this)
		{
			m_dbTuningProgress = dbProg;
		}
	}
	
	public String getTuningInfo()
	{
		synchronized(this)
		{
			String strRet = new String(m_strTuningInfo);
			return strRet;
		}
	}
	
	private void appendTuningInfo(String str)
	{
		synchronized(this)
		{
			m_strTuningInfo += '\n';
			m_strTuningInfo += str;
		}
	}
	
	private boolean m_bIsTuning = false;
	private double m_dbTuningProgress = 0.0;
	private String m_strTuningInfo = new String("");
}
