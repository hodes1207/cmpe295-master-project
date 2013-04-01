package datamining;

import java.util.ArrayList;
import java.util.Collections;

import database.MedicalParameter;

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
		
		LibLinearClassifier linearModel = new LibLinearClassifier(nDim);
		LinearTuningParam param2 = new LinearTuningParam();
		param2.DefaultInit();
		
		boolean bRBFEnabled = m_enableRBFTuning;
		
		RBFTuningParam param1 = new RBFTuningParam();
		param1.DefaultInit();
		
		double dbTotalTuneTimes = param2.matrixC.length;
		if (bRBFEnabled)
			dbTotalTuneTimes += param1.matrixC.length * param1.matrixG.length;
		
		double dbCurTunedTimes = 0;
		
		//Tune linear model
		{
			for (int i = 0; i < param2.matrixC.length; i++) {
				double c = param2.matrixC[i];
				linearModel.setC(c);

				if (bDebugOutput)
					linearModel.enableDebugOutput();
				else
					linearModel.disableDebugOutput();

				linearModel.BuildModel(buildDataSet);
				param2.matrixRes[i] = linearModel.CrossValidation(testDataSet);

				// Set tuning progress
				dbCurTunedTimes += 1.0;
				setTuningProgress(dbCurTunedTimes / dbTotalTuneTimes);

				// Append tuning information
				appendTuningInfo(param2.getTuneInfo(i));
			}

			int indexMaxC = 0;
			double dbBestAccuracy = -1;
			for (int i = 0; i < param2.matrixC.length; i++) {
				if (param2.matrixRes[i] > dbBestAccuracy) {
					indexMaxC = i;
					dbBestAccuracy = param2.matrixRes[i];
				}
			}

			param2.dbBestC = param2.matrixC[indexMaxC];
			param2.dbBestAccuracy = dbBestAccuracy;
		}
				
		
		// Tune RBF model
		if (bRBFEnabled)
		{
			LibSVMClassifier rbfModel = new LibSVMClassifier(nDim);
			
			for (int i = 0; i < param1.matrixC.length; i++)
			{
				for (int j = 0; j < param1.matrixG.length; j++)
				{
					double c = param1.matrixC[i];
					double g = param1.matrixG[j];
					
					rbfModel.setC(c);
					rbfModel.setG(g);
					
					if (bDebugOutput)
						rbfModel.enableDebugOutput();
					else
						rbfModel.disableDebugOutput();
					
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
		
		//Finished tuning, set tuning result
		ModelTuneResult res = new ModelTuneResult();
		if (bRBFEnabled && param1.dbBestAccuracy > param2.dbBestAccuracy)
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
		}
		else
		{
			LibLinearClassifier cls = new LibLinearClassifier(nDim);
			cls.setC(m_dbLinear_C);
			
			m_pModel = cls;
			cls = null;
		}
		
		if (bDebugOutput)
			m_pModel.enableDebugOutput();
		else
			m_pModel.disableDebugOutput();
		
		m_pModel.BuildModel(dataset);
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
	
	public MedicalParameter getModelInfo()
	{
		MedicalParameter param = new MedicalParameter();
		
		param.bRBF = true;
		param.dbRBF_c = m_dbRBF_C;
		param.dbRBF_g = m_dbRBF_G;
		param.dbLinear_c = m_dbLinear_C;
		
		return param;
	}
	
	public double crossValidation(ArrayList<CLASSIFY_ENTITY> dataset)
	{
		if (null == m_pModel)
			return -1.0;
		
		m_dbCurAccuracy = m_pModel.CrossValidation(dataset);
		
		return m_dbCurAccuracy;
	}
	
	public double getModelAccuracy()
	{
		return m_dbCurAccuracy;
	}
	
	private SVMInterface m_pModel = null;
	private boolean m_bRBF = true;
	private double m_dbRBF_C = 0.03125;
	private double m_dbRBF_G = 0.001;
	private double m_dbLinear_C = 0.03125;
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
	
	public void disableDebugOutput() { bDebugOutput = false; }
	public void enableDebugOutput() { bDebugOutput = true; }
	
	public void enableRBFTuning() { m_enableRBFTuning = true; }
	public void disableRBFTuning() { m_enableRBFTuning = false; }
	public boolean isRBFTuningEnabled() { return m_enableRBFTuning; }
	
	private boolean m_bIsTuning = false;
	private double m_dbTuningProgress = 0.0;
	private String m_strTuningInfo = new String("");
	private boolean bDebugOutput = false;
	boolean m_enableRBFTuning = false;
}
