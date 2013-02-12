package datamining;

import java.util.ArrayList;
import datamining.liblinear.*;

public class LibLinearClassifier extends SVMInterface {
	
	LibLinearClassifier(int dim) { m_nDim = dim; m_dbC = 1;}
	
	void setC(double c) { m_dbC = c; }
	
	public void disableDebugOutput() { Linear.disableDebugOutput(); }
	public void enableDebugOutput() { Linear.enableDebugOutput(); }

	public void BuildModel(ArrayList<CLASSIFY_ENTITY> dataset)
	{
		Problem prob = new Problem();
		prob.n = m_nDim;
		prob.l = dataset.size();
		prob.y = new double[prob.l];
		prob.x = new Feature[prob.l][];
		
		for (int i = 0; i < prob.l; i++)
	    {
			//dimension not equal
			if (dataset.get(i).vectors.size() != m_nDim)
				continue;

		    prob.y[i] = dataset.get(i).nClsId;
		    prob.x[i] = new Feature[m_nDim];
			for(int j=0; j < m_nDim; j++)
			{
				prob.x[i][j] = new FeatureNode(j+1, dataset.get(i).vectors.get(j));
			}
	    }
		
		prob.bias = -1;
		
		//parameters
		Parameter svmPara = new Parameter(SolverType.L2R_LR, m_dbC, 0.001, 0.1);
		
		m_model = null;
		m_model = Linear.train(prob, svmPara);
		prob = null;
		svmPara = null;
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public PROB_ESTIMATION_RES Classify(ArrayList<Double> vectors)
	{
		ArrayList<CLASSIFY_RES> resultList = new ArrayList<CLASSIFY_RES>();
		if (vectors.size() != m_nDim || m_model == null)
			return null;

		//get number of unique classes
		int nrClass = m_model.getNrClass();
		if (nrClass <= 0)
			return null;

		//set vector to svmNodes
		Feature[] svmNodes = new Feature[m_nDim];
		for(int i = 0;i < m_nDim;i++){
			svmNodes[i] = new FeatureNode(i+1, vectors.get(i));
		}

		double[] probEstimates = new double[nrClass];
		int[] classIds = m_model.getLabels();
		double predict_label = Linear.predictProbability(m_model, svmNodes, probEstimates);

		for(int i = 0;i < nrClass; i++){
			resultList.add(new CLASSIFY_RES(classIds[i], probEstimates[i]));
		}
		
		PROB_ESTIMATION_RES ret = new PROB_ESTIMATION_RES();
		ret.nClsId = (int) predict_label;
		ret.probRes = resultList;
		
		return ret;
	}
	
	private Model m_model;
	private double m_dbC;
	private int m_nDim;
}
