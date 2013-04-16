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

	public MsgType msgType = ImgServMsg.MsgType.UNINIT;
	
	public ClassifyResp clsResp = null;
	public KNNSearchResp searchResp = null;
}
