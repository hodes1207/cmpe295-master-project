package testScript;

import java.io.*;
import java.net.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import CommunicationInterface.*;
import ServiceInterface.*;
import database.*;
import datamining.*;

public class MainServer implements Serializable {

	private static final int PORT = 3456; 
	private static EngineService serv = new EngineService();
	   
	private static HashSet<String> names = new HashSet<String>();  
    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();  
    public static void main(String[] args) throws Exception {  
    	
      // Service Engine BootStrap
	  serv.startService();
		
	  while (serv.getInitProgress() < 1.0)
	  {
			System.out.println(serv.getInitProgress());
			Thread.sleep(3000);
	  }
		
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
    
    private static class Handler extends Thread {  
    	private String name;       
    	private Socket socket;       
    	private ObjectInputStream in;       
    	private ObjectOutputStream out;
    	
        public Handler(Socket socket) {      
        	this.socket = socket;   
        }
        
        public void run() {   
        	try {                // Create character streams for the socket.   
        		   System.out.println("Received client connection");
        		   
        	       in = new ObjectInputStream(socket.getInputStream());          
        	       out = new ObjectOutputStream(socket.getOutputStream());
        	      // EngineService serv = new EngineService();
        	       
        	       while (true) {
        	    	   try {
        	    	   MessageObject req = (MessageObject) (in.readObject());
        	    	      if (req == null) {
        	    		      System.out.println("ERROR!! NULL message object passed in");
        	    		      return;
        	    	      }
        	    	   
        	    	      if (req.gettype() == MsgId.GET_CMI) {
        	    	    	  System.out.println("Received get model info for domain : "+ req.getdomid());
        	    	    	  String res = serv.GetCurrentModelInfo(req.getdomid());
        	    	    	  MessageObject result = new MessageObject(req.gettype(), RetID.STRING);
        	    	    	  result.setStrval(res);
        	    	    	  
        	    	    	  out.writeObject(result);
        	    		      out.flush();
        	    	    	  
        	    	      }
        	    	      /* Handle request API should be in here.
        	    	      if (req.gettype() == MsgId.GET_DOMAIN) {
        	    		      System.out.println("Received get domain query request");
        	    		      ArrayList<Domain> res = serv.GetDomain();
        	    		      
        	    		    	Iterator itr = res.iterator();
        	    		    	
        	    		    	while (itr.hasNext()) {
        	    		    		Domain d = (Domain) itr.next();
        	    		    		System.out.println("Domain Id : " + d.getDomainId() + "Domain Name : " + d.getDomainName());
        	    		    	}
        	    		      MessageObject result = new MessageObject(req.gettype(), RetID.DOMAIN_LIST );
        	    		      result.setdomlist(res);
        	    		   
        	    		      out.writeObject(result);
        	    		      out.flush();
        	    	      } */
        	    	
        	    	   } catch (ClassNotFoundException classNot) {
        	    		   System.out.println("Ouch!");
        	    	   }
        	       }
        	       
        	}  catch (IOException e) {
        		System.out.println("Screwed : " + e);
        	}
        	finally {
        		try {socket.close();} catch (IOException e) {}
        	}
        	
       } // run
        
    } // Handler
} // MainServer

