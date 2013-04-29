package ServiceServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import MessageLayer.*;
import ServiceInterface.*;
import database.*;

public class MainServer {

	private static int PORT = 6753; 
	private static EngineService serv = new EngineService();
	
	private static HashSet<Handler> thrds = new HashSet<Handler>();
	
	private static class ExitTrigger extends Thread 
	{
		public ExitTrigger(EngineService server)
		{
			m_serv = server;
		}
		
	    public void run() 
	    {
	         System.out.println("Image server shutting down ....");
	         m_serv.shutdownServer();
	    }
	    
	    private EngineService m_serv = null;
	    
	 }
    
    public static void main(String[] args) throws Exception 
    {  	
      Runtime.getRuntime().addShutdownHook(new ExitTrigger(serv));
    	
      String strCfgFile = "ServiceServ_ini.xml";
      if (args.length > 0)
    	  strCfgFile = args[0];
    	
      initServer(strCfgFile);

      ServerSocket listener = new ServerSocket(PORT, 10);    
      System.out.println("The service control server is listening on port " + PORT);    
      
      try {           
    	      while (!listener.isClosed()) 
    	      {       
    	    	  Handler thrd = new Handler(listener.accept());   
    	    	  
    	    	  Iterator<Handler> it = thrds.iterator();
    	    	  while (it.hasNext())
    	    	  {
    	    		  Handler hd = it.next();
    	    		  if (!hd.isAlive())
    	    			  it.remove(); // remove dead connections
    	    	  }
    	    	  
    	    	  thrd.start();
    	    	  thrds.add(thrd);
    	      }      
      }
      catch (IOException e) { }
      
      Iterator<Handler> it = thrds.iterator();
      while (it.hasNext())
	  {
		  Handler hd = it.next();
		  hd.stopThrd();
	  }
      
      it = thrds.iterator();
      while (it.hasNext())
	  {
		  Handler hd = it.next();
		  hd.join();
	  }
    } 
    
    private static void initServer(String xmlCfgFile) 
    		throws ParserConfigurationException, SAXException, IOException, InterruptedException
    {
    	File cfgFile = new File(xmlCfgFile);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(cfgFile);
		doc.getDocumentElement().normalize();

		Element root = (Element) doc.getElementsByTagName("InitInfo").item(0);
		
		String classDBName = root.getAttribute("classDBName");
		String domainDBName = root.getAttribute("domainDBName");
		String medicalImageDBName = root.getAttribute("medicalImageDBName");
		String DBUrl = root.getAttribute("DBUrl");
		
		PORT = Integer.parseInt(root.getAttribute("servicePort"));
		int imgListenPort = Integer.parseInt(root.getAttribute("imgServListenPort"));
		int numImgServs = Integer.parseInt(root.getAttribute("imgServNum"));
		
		imgService.initServer(domainDBName, classDBName, medicalImageDBName, DBUrl, imgListenPort, numImgServs);
		imgService.run();
    }
    
    private static class Handler extends Thread {  
    	private Socket socket;       
    	private ObjectInputStream in;       
    	private ObjectOutputStream out;
    	private MessageObject result;
    	private boolean bExit = false;
    	
        public Handler(Socket socket) {      
        	this.socket = socket;   
        }
        
        public void stopThrd() 
        {
        	bExit = true;
        	try {
				socket.close();
			} catch (IOException e) { }
		}

		public void run() {   
        	try {  
        		// Create character streams for the socket.   
        		   System.out.println("Received client connection");
        		   
        	       in = new ObjectInputStream(socket.getInputStream());          
        	       out = new ObjectOutputStream(socket.getOutputStream());
        	      // EngineService serv = new EngineService();
        	       
        	       while (!bExit) {
        	    	   try {
        	    	           MessageObject req = (MessageObject) (in.readObject());
        	    	           if (req == null) {
        	    		            System.out.println("ERROR!! NULL message object passed in");
        	    		            continue;
        	    		            
        	    	           } else {
        	    	                result = ProcessRequest(req);
        	    	           }
        	    	           
        	    	           if (null != result)
        	    	           {
        	    	        	   out.writeObject((MessageObject) result);
            		               out.flush();
            		               out.reset();
        	    	           }
        		              
        	    	   } catch (ClassNotFoundException classNot) {
        	    		   System.out.println("Ouch!");
        	    	   }
        	       }
        	       
        	}  catch (IOException e) {
        		
        		try {socket.close();
        		     out.close();
        		     in.close();
        		     result = null;
        		 } catch (IOException ee) {}
        		
        	}
        	finally {
        		try {socket.close();
        		     out.close();
        		     in.close();
        		     result = null;
        		} catch (IOException e) {}
        	}
        	
       } // run
        
        private MessageObject ProcessRequest (MessageObject req) {
        	// We use the passed in request object and fill in the return results.
        	// That will allow us to play with just one object per client and do not use lot of memory.
        	// But be careful not to overwrite the fields by mistake.
        	

        	System.out.println("Processing "+ req.gettype() + " request from client " + req.getclientid());
        	// req.setrettype(RetID.INVALID);
        	
        	switch (req.gettype()) {
        	    
        	    case GET_PICID:
        	    	              ArrayList<Long> list = serv.GetPicId(req.getclassid());
        	    	              req.setlist(list);
        	    	              req.setrettype(RetID.LONG_LIST);
        	    	              break;
        	    	              
        	    case GET_IMAGE:
        	    	              byte[] gi = serv.RetrieveImg(req.getimageid());
        	    	              req.setbytes(gi);
        	    	              req.setrettype(RetID.BYTES);
        	    	              break;
        	    	              
        	    case DEL_IMAGE:
        	    	              boolean di = serv.DeleteImg(req.getclassid(), req.getimageid());
        	    	              req.setboolval(di);
        	    	              req.setrettype(RetID.BOOL);
        	    	              break;
        	    	              
        	    case ADD_IMAGE:
        	    	              boolean ai = serv.AddImg(req.getclassid(), req.getimageid(), req.getbytes());
        	    	              req.setboolval(ai);
        	    	              req.setrettype(RetID.BOOL);
        	    	              break;
        	    	              
        	    case GET_DOMAIN:
        	    	              ArrayList<Domain> gd = serv.GetDomain();
        	    	              req.setdomlist(gd);
        	    	              req.setrettype(RetID.DOMAIN_LIST);
        	    	              break;
        	    	              
        	    case GET_CLASS:
        	    	              ArrayList<SecondLevelClass> gc = serv.GetClasses(req.getdomid());
        	    	              req.setslclist(gc);
        	    	              req.setrettype(RetID.CLASS_LIST);
        	    	              break;
        	    	              
        	    case SET_RBFKP:
        	    	              boolean sr = serv.SetRBFKernelParam(req.getdomid(), req.getc(), req.getg(), req.getmaxsample());
        	    	              req.setboolval(sr);
        	    	              req.setrettype(RetID.BOOL);
        	    	              break;
        	    	              
        	    case SET_LKP:
        	    	              boolean sl = serv.SetLinearKernelParam(req.getdomid(), req.getc(), req.getmaxsample());
        	    	              req.setboolval(sl);
        	    	              req.setrettype(RetID.BOOL);
        	    	              break;
        	    	              
        	    case GET_ATFN:
        	    	              int ga = serv.GetAutoTuningFoldNum(req.getdomid());
        	    	              req.setintval(ga);
        	    	              req.setrettype(RetID.INT);
        	    	              break;
        	    	              
        	    case SET_ATFN:
        	    	              boolean sa = serv.SetAutoTuningFoldNum(req.getdomid(), req.getintval());
        	    	              req.setboolval(sa);
        	    	              req.setrettype(RetID.BOOL);
        	    	              break;
        	
        	    case START_TUNE:
			        	    	try 
								  {
									imgService.StartTuningRequest(req.getdomid());
							      } 
								  catch (IOException e) 
								  {
									e.printStackTrace();
								  }
			        	    	
			        	    	  req.bval = true;
   				              	  req.setrettype(RetID.BOOL);
					              break;
					              
        	    case START_TRAIN:
    	                          try 
								  {
									imgService.StartTrainingRequest(req.getdomid());
							      } 
								  catch (IOException e) 
								  {
									e.printStackTrace();
								  }
    	                          
    				              req.bval = true;
    				              req.setrettype(RetID.BOOL);
    				              break;
    			
        	    case ENABLE_RBF:
        	                   	  boolean er = serv.enableRBFTuning(req.getdomid());
    	                          req.setboolval(er);
    	                          req.setrettype(RetID.BOOL);
    	                          break;
    	                          
        	    case DISABLE_RBF:
                 	              boolean dr = serv.disableRBFTuning(req.getdomid());
                                  req.setboolval(dr);
                                  req.setrettype(RetID.BOOL);
                                  break;
                                  
        	    case CHECK_RBF:
        	    	              boolean cr = serv.isRBFTuningEnabled(req.getdomid());
                                  req.setboolval(cr);
                                  req.setrettype(RetID.BOOL);
                                  break;
                                  
        	    case GET_IMGSERV:
								 try 
								 {
									 req.imgServInfo = imgService.getImgServerInfo();
								 } 
								 catch (IOException e1) 
								 {
									e1.printStackTrace();
								 }
								 
				  	             req.setrettype(RetID.IMGSERV_LIST);
				                 break;
				                  
        	    case GET_MODEL_ACCURACY:
			        	    	  try 
								  {
									imgService.getModelAccuracyRequest
									(req.getintval(), req.getdomid(), out, socket);
							      } 
								  catch (IOException e) 
								  {
									e.printStackTrace();
								  }
			        	    	  
					              req = null;
					              break;
                                  
        	    case GET_MODEL_TUNINGINFO:
			        	    	  try 
								  {
									imgService.getTuningInfoRequest
									(req.getintval(), req.getdomid(), out, socket);
							      } 
								  catch (IOException e) 
								  {
									e.printStackTrace();
								  }
					              req = null;
					              break;
  				  				  
        	    case GET_MODEL_TRAININGINFO:
			        	    	  try 
								  {
									imgService.getTrainingInfoRequest
									(req.getintval(), req.getdomid(), out, socket);
							      } 
								  catch (IOException e) 
								  {
									e.printStackTrace();
								  }
					              req = null;
					              break;
        	    				  
        	    case SEARCH_SIM:
								  try 
								  {
									imgService.SimilaritySearchRequest(req.getbytes(), req.getintval(), out, socket);
							      } 
								  catch (IOException e) 
								  {
									e.printStackTrace();
								  }
    				              req = null;
    				              break;
    				              
        	    case GET_CLEST:
			        	    	  try 
								  {
									imgService.ClassificationRequest(req.getbytes(), req.getdomid(), out, socket);
							      } 
								  catch (IOException e) 
								  {
									 e.printStackTrace();
								  }
					              req = null;
					              break;
        	    	              
        	    /* ***********************************   */
        	    /* Add rest of the misc. API's if needed */
        	    default:
        	    	              req.setrettype(RetID.INVALID);
        	    	              break;
        	    	                 	
        	}
        	
        	return req;
        	
        }
        
    } // Handler

    private static ImgRetrievalService imgService = new ImgRetrievalService();
} // MainServer

