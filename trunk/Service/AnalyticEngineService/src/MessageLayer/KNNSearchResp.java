package MessageLayer;

import java.io.Serializable;
import java.util.ArrayList;

public class KNNSearchResp  implements Serializable {

	private static final long serialVersionUID = 1L;

	public ArrayList<ImgDisResEntry> res = null;
	public int k = 0;
	
}
