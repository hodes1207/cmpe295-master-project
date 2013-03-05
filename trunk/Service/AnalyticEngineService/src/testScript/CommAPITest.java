package testScript;

import java.io.*;
import java.net.*;
import java.util.*;
import CommunicationInterface.*;
import database.*;



public class CommAPITest {    
	public static void main(String[] args) throws IOException {    
		
		CommunicationAPI con = new CommunicationAPI();
		
		
		
		System.out.println("Testing getInitProgress API");
		System.out.println("===========================");
	
     	double prog = con.getInitProgress();
		System.out.println("Get Progress value is : " + prog);
		
		System.out.println("\nTesting GetDomain API");
		System.out.println("===========================");
		System.out.println("Output of GetDomain :");
		ArrayList<Domain> gd = con.GetDomain();
		Iterator itr = gd.iterator();
    	
    	while (itr.hasNext()) {
    		Domain d = (Domain) itr.next();
    		System.out.println("Domain Id : " + d.getDomainId() + "Domain Name : " + d.getDomainName());
    	}
    	
    	System.out.println("\nTesting RBFTuning  API's");
		System.out.println("================================");
    	
		System.out.println("Domainid 0 RBF Tuning enabled : " + con.isRBFTuningEnabled(0));
		System.out.println("Domainid 1 RBF Tuning enabled : " + con.isRBFTuningEnabled(1));
		System.out.println("Domainid -1 RBF Tuning enabled : " + con.isRBFTuningEnabled(-1));
		
		if (con.enableRBFTuning(1)) {
			System.out.println("Enabling RBF Tuning for domainid 1  PASSED");
		} else {
			System.out.println("Enabling RBF Tuning for domainid 1  FAILED");
		}
		System.out.println("Domainid 1 RBF Tuning enabled : " + con.isRBFTuningEnabled(1));
		
		if (con.disableRBFTuning(1)) {
			System.out.println("Disabling RBF Tuning for domainid 1  PASSED");
		} else {
			System.out.println("Disabling RBF Tuning for domainid 1  FAILED");
		}
		System.out.println("Domainid 1 RBF Tuning enabled : " + con.isRBFTuningEnabled(1));
		
		System.out.println("Testing Auto tuning API's");
		System.out.println("=========================");
		
		System.out.println("Auto Tuning Info for domain id 0" + con.getAutoTuningInfo(0));
		System.out.println("Current Model Info for domain id 0\n" + con.GetCurrentModelInfo(0));
		System.out.println("Current Model Info for domain id 1\n" + con.GetCurrentModelInfo(1));
        con.close();
	//	System.exit(0);   
	}
}

