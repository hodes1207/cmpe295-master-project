package testScript;

import java.io.*;
import java.net.*;
import java.util.*;
import CommunicationInterface.*;
import database.*;



public class Client {    
	public static void main(String[] args) throws IOException {    
		
		
		ServerConnection scon = new ServerConnection();
		MessageObject query = new MessageObject(MsgId.GET_CMI);
		MessageObject result;
		
		// Set domain id
		query.setdomid(1);
		
		scon.sendmsg(query);
		
		result = scon.getmsg(query); 
			
		
	    if (result.getrettype() == RetID.STRING) {
	    	
	    	System.out.println("Yahoo !!!");
	    	System.out.println("CMI : "+  result.getStrVal());
	    	 
	    } else {
	    	System.out.println("ERROR !! fetching domain list");
	    }
        
        scon.close();
	//	System.exit(0);   
	}
}

