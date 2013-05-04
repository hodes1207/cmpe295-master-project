package ServiceServer;

import java.io.IOException;


public class ImgServerMsgThrd  extends Thread
{
	public ImgServerMsgThrd(ImgRetrievalService imgRetrievalService)
	{
		serv = imgRetrievalService;
	}
	
	public void run()
	{
		try 
		{
			serv.imgServMsgThrdFunc();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	private ImgRetrievalService serv = null;
}
