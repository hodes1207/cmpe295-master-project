package ImgRetrieveServer;

public class ServerReloadThrd extends Thread
{
	public ServerReloadThrd(ImgRetrieveServer s)
	{
		serv = s;
	}
	
	public void run()
	{
		serv.reloadThrdFunc();
	}
	
	private ImgRetrieveServer serv = null;
}
