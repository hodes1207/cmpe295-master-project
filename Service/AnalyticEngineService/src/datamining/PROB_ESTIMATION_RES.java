package datamining;

import java.io.Serializable;
import java.util.ArrayList;

public class PROB_ESTIMATION_RES  implements Serializable
{
	
	private static final long serialVersionUID = 1L;
	
	public int nClsId = -1;
	public ArrayList<CLASSIFY_RES> probRes = new ArrayList<CLASSIFY_RES>();
	
}
