package ServiceServer;

public class ImgServResProcThrd extends Thread
{
	public ImgServResProcThrd(ImgRetrievalService imgRetrievalService)
	{
		serv = imgRetrievalService;
	}
	
	public void run()
	{
		serv.imgServResProcessFunc();
	}
	
	private ImgRetrievalService serv = null;
}