package ImgRetrieveServer;

public class MainServer {
	
	private static class ExitTrigger extends Thread 
	{
		public ExitTrigger(ImgRetrieveServer server)
		{
			m_serv = server;
		}
		
	    public void run() 
	    {
	         System.out.println("Image server shutting down ....");
	         m_serv.shutdownServer();
	    }
	    
	    private ImgRetrieveServer m_serv = null;
	    
	 }
	
	public static void main(String[] args) throws InterruptedException
	{	
		ImgRetrieveServer server = new ImgRetrieveServer();
		Runtime.getRuntime().addShutdownHook(new ExitTrigger(server));
		
		String strCfgFile = "ImgRetrieveServ_ini.xml";
	      if (args.length > 0)
	    	  strCfgFile = args[0];
	      
		if (!server.initService(strCfgFile))
			return;
		
		server.run();
	}
}
