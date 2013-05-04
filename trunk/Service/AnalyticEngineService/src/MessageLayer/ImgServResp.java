package MessageLayer;

import java.io.Serializable;

import MessageLayer.ImgServMsg.MsgType;

public class ImgServResp implements Serializable 
{
	private static final long serialVersionUID = 1L;
	
	public ImgServResp(MsgType type)
	{
		msgType = type;
	}

	public int msgId = -1;
	public MsgType msgType = ImgServMsg.MsgType.UNINIT;
	
	public ClassifyResp clsResp = null;
	public KNNSearchResp searchResp = null;
	public SysPerfInfo perfInfo = null;

	public double modelAccuracy = 0.0;
	public String tuningInfo = "Error, message no assigned";
	public boolean trainingInProgress = false;
}
