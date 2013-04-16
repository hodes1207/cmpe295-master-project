package MessageLayer;

import java.io.Serializable;

public class ImgServMsg implements Serializable 
{
	public enum MsgType 
	{
		UNINIT,
		SIM_SEARCH,
		CLASSIFICATION,
		START_TUNING,
		START_TRAINING,
	}
	
	public ImgServMsg(MsgType type) { msgType = type; }
	
	private static final long serialVersionUID = 1L;
	
	public MsgType msgType = MsgType.UNINIT;
	public int msgId = -1;
	public byte[] byteImg = null; 
	
	public int k = 0;
	public int domId = -9999;
}