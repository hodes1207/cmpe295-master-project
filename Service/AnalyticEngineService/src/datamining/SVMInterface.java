package datamining;
import java.util.ArrayList;

public abstract class SVMInterface {
	
	 public abstract void BuildModel(ArrayList<CLASSIFY_ENTITY> dataset);
	 
	 public abstract PROB_ESTIMATION_RES Classify(ArrayList<Double> vectors);
	 
	 public abstract void disableDebugOutput();
	 public abstract void enableDebugOutput();
	 
	 public double CrossValidation(ArrayList<CLASSIFY_ENTITY> dataset)
	 {
		 if (dataset.size() == 0)
			 return 0.0;
		 
		 double nPassed = 0.0;
		 for (int i = 0; i < dataset.size(); i++)
		 {
			 PROB_ESTIMATION_RES res = Classify(dataset.get(i).vectors);
			 if (res.nClsId == dataset.get(i).nClsId)
				 nPassed += 1.0;
		 }
		 
		 m_dbAccuracy = nPassed/dataset.size();
		 return m_dbAccuracy;
	 }
	 
	 public double getAccuracy() { return m_dbAccuracy; }
	
	 protected double m_dbAccuracy;
}

