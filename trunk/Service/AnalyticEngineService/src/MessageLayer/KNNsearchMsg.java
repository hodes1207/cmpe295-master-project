package MessageLayer;

import java.io.Serializable;

public class KNNsearchMsg implements Serializable 
{
	private static final long serialVersionUID = 1L;
	public int k;
	public double[] feature = null;
	public int msgId = -1;
}
