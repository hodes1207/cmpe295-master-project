package datamining;

import java.io.Serializable;

public class CLASSIFY_RES implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	CLASSIFY_RES(int id, double prob) 
	{
		nClsId = id;
		dbProb = prob;
	}
	
	public int nClsId = -1;
	public double dbProb = 0.0;
}
