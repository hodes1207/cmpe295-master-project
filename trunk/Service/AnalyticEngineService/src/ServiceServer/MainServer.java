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
    
    public static void main(String[] args) throws Exception {  
    	
      initServer("ServiceServ_ini.xml");

      ServerSocket listener = new ServerSocket(PORT, 10);    
      System.out.println("The image processing server is listening on port " + PORT);    
      try {           
    	      while (true) {       
    	    	 new Handler(listener.accept()).start();      
    	      }      
      } finally {         
    	      listener.close();     
      }   
    } // main
    
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
		
		// Service Engine BootStrap
		serv.startService();
			
		while (serv.getInitProgress() < 1.0)
		{
			System.out.println(serv.getInitProgress());
			Thread.sleep(3000);
		}
    }
    
    private static class Handler extends Thread {  
    	private Socket socket;       
    	private ObjectInputStream in;       
    	private ObjectOutputStream out;
    	private MessageObject result;
    	
        public Handler(Socket socket) {      
        	this.socket = socket;   
        }
        
        public void run() {   
        	try {  
        		// Create character streams for the socket.   
        		   System.out.println("Received client connection");
        		   
        	       in = new ObjectInputStream(socket.getInputStream());          
        	       out = new ObjectOutputStream(socket.getOutputStream());
        	      // EngineService serv = new EngineService();
        	       
        	       while (true) {
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
        	

        	//System.out.println("Processing "+ req.gettype() + " request from client " + req.getclientid());
        	// req.setrettype(RetID.INVALID);
        	
        	switch (req.gettype()) {
        	    case GET_PROG :
        	    	              double prog = serv.getInitProgress();
        	    	              req.setdval(prog);
        	    	              req.setrettype(RetID.DOUBLE);
        	    	              break;
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
        	    case GET_ATP:
        	    	              double gap = serv.getAutoTuningProgress(req.getdomid());
        	    	              req.setdval(gap);
        	    	              req.setrettype(RetID.DOUBLE);
        	    	              break;
        	    case GET_ATI:
        	    	              String gati = serv.getAutoTuningInfo(req.getdomid());
        	    	              req.setStrval(gati);
        	    	              req.setrettype(RetID.STRING);
        	    	              break;
        	    case GET_CMI:
        	    	              String gcmi = serv.GetCurrentModelInfo(req.getdomid());
        	    	              req.setStrval(gcmi);
        	    	              req.setrettype(RetID.STRING);
        	    	              break;
        	    case START_TUNE:
        	    	              boolean st = serv.StartAutoTuning(req.getdomid());
        	    	              req.setboolval(st);
        	    	              req.setrettype(RetID.BOOL);
        	    	              break;
        	    case START_TRAIN:
        	    	              boolean stt = serv.startTraining(req.getdomid());
    	                          req.setboolval(stt);
    	                          req.setrettype(RetID.BOOL);
    	                          break;
        	    case TRAIN_FINISH:
        	    				  boolean tf = serv.isTrainingFinished(req.getdomid());
        	    				  req.setboolval(tf);
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
        	    /*case SEARCH_SIM:
        	    	              ArrayList<Long> ss = serv.SimilaritySearch(req.getbytes(), req.getintval());
        	    	              req.setlist(ss);
        	    	              req.setrettype(RetID.LONG_LIST);
        	    	              break;*/
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
        	    	              String gct = serv.classificationEstimation(req.getbytes(), req.getintval());
        	    	              req.setStrval(gct);
        	    	              req.setrettype(RetID.STRING);
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

