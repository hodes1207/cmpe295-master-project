package MessageLayer;

import java.io.Serializable;
import datamining.PROB_ESTIMATION_RES;;

public class ClassifyResp  implements Serializable {

	private static final long serialVersionUID = 1L;

	public int msgId = -1;
	public PROB_ESTIMATION_RES clsRes = null;
}