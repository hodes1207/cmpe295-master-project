package datamining;

public class CLASSIFY_RES {
	
	CLASSIFY_RES(int id, double prob) 
	{
		nClsId = id;
		dbProb = prob;
	}
	
	public int nClsId = -1;
	public double dbProb = 0.0;
}
