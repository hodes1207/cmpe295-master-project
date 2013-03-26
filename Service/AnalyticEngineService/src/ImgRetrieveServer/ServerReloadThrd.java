package ImgRetrieveServer;

import java.io.IOException;

public class ServerReloadThrd extends Thread
{
	public ServerReloadThrd(ImgRetrieveServer s)
	{
		serv = s;
	}
	
	public void run()
	{
		try 
		{
			serv.reloadThrdFunc();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}
	
	private ImgRetrieveServer serv = null;
}
