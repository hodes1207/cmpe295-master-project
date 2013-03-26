package ImgRetrieveServer;

public class MainServer {
	
	public static void main(String[] args) throws InterruptedException
	{
		ImgRetrieveServer server = new ImgRetrieveServer();
		if (!server.initService("ImgRetrieveServ_ini.xml"))
			return;
		
		server.run();
	}
}
