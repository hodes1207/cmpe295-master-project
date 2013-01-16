package datamining;

import java.util.ArrayList;

import datamining.libsvm.*;

public class LibSVMClassifier  extends SVMInterface {
	
	LibSVMClassifier(int dim) 
	{ 
		m_nDim = dim; 
		m_dbC = 0.0315; 
		m_dbG = 1.0/dim; 
	}
	
	void setC(double c) { m_dbC = c; }
	void setG(double g) { m_dbG = g; }

	public void BuildModel(ArrayList<CLASSIFY_ENTITY> dataset)
	{
		svm_problem prob = new svm_problem();
		prob.l = dataset.size();
		prob.y = new double[prob.l];
		prob.x = new svm_node[prob.l][m_nDim];
		
		for (int i = 0; i < prob.l; i++)
	    {
			//dimension not equal
			if (dataset.get(i).vectors.size() != m_nDim)
				continue;

		    prob.y[i] = dataset.get(i).nClsId;
			for(int j=0; j < m_nDim; j++)
			{
				prob.x[i][j] = new svm_node();
				prob.x[i][j].index = j+1;
				prob.x[i][j].value = dataset.get(i).vectors.get(j);
			}
	    }
		
		//parameters
		svm_parameter svmPara = new svm_parameter();
		svmPara.C = m_dbC;
		svmPara.gamma = m_dbG;
		svmPara.degree = 3;
		svmPara.svm_type = svm_parameter.C_SVC;
		svmPara.kernel_type = svm_parameter.RBF;
		svmPara.eps = 0.001;
		svmPara.probability = 1;
		svmPara.p = 0.1;
		svmPara.coef0 = 0;
		svmPara.nu = 0.5;
		svmPara.cache_size = 100;
		svmPara.eps = 1e-3;
		svmPara.shrinking = 1;
		svmPara.nr_weight = 0;
		svmPara.weight_label = new int[0];
		svmPara.weight = new double[0];
		
		m_model = null;
		m_model = svm.svm_train(prob, svmPara);
		
		prob = null;
		svmPara = null;
	}
	
	public PROB_ESTIMATION_RES Classify(ArrayList<Double> vectors)
	{
		ArrayList<CLASSIFY_RES> resultList = new ArrayList<CLASSIFY_RES>();
		if (vectors.size() != m_nDim || m_model == null)
			return null;

		//get number of unique classes
		int nrClass = svm.svm_get_nr_class(m_model);
		if (nrClass <= 0)
			return null;

		//set vector to svmNodes
		svm_node[] svmNodes = new svm_node[m_nDim];
		for(int i = 0;i < m_nDim;i++){
			svmNodes[i] = new svm_node();
			svmNodes[i].index = i+1;
			svmNodes[i].value = vectors.get(i);
		}

		double[] probEstimates = new double[nrClass];
		int[] classIds = new int[nrClass];
		svm.svm_get_labels(m_model, classIds);
		double predict_label = svm.svm_predict_probability(m_model, svmNodes, probEstimates);

		for(int i = 0;i < nrClass; i++){
			resultList.add(new CLASSIFY_RES(classIds[i], probEstimates[i]));
		}
		
		PROB_ESTIMATION_RES ret = new PROB_ESTIMATION_RES();
		ret.nClsId = (int) predict_label;
		ret.probRes = resultList;
		
		return ret;
	}
	
	private svm_model m_model;
	private double m_dbC;
	private double m_dbG;
	private int m_nDim;

}
