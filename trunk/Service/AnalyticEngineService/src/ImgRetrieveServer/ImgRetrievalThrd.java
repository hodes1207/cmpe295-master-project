package ImgRetrieveServer;

public class ImgRetrievalThrd  extends Thread
{
	public ImgRetrievalThrd(ImgRetrieveServer s)
	{
		serv = s;
	}
	
	public void run()
	{
		serv.workerThrdFunc();
	}
	
	private ImgRetrieveServer serv = null;
}
