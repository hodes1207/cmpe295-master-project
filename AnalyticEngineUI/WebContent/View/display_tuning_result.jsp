<!DOCTYPE html>
<html lang="en">
  <head>
<meta http-equiv="refresh" content="3" >
</head>
<body>
    <%@ page import="MessageLayer.CommunicationAPI"%>
    <%@ page import="util.SessionHashMap"%>
<%

if(session.getAttribute("isStarted") == null) 
	out.println("Model Tuning Start Process Not Started...."+"<br/>");
else
{
	boolean isStarted = (Boolean)session.getAttribute("isStarted");
if(isStarted == true)
{
	
	String id = session.getId();
	SessionHashMap sessionMap = SessionHashMap.getInstance();
	CommunicationAPI comAPI = sessionMap.getSession(id);

	int domainId = (Integer)session.getAttribute("domainId");
    double tuningProcess = comAPI.getAutoTuningProgress(domainId);


	System.out.println(tuningProcess+", isStarted: "+isStarted);
	tuningProcess = tuningProcess*100;
    out.println("Tuning Status: "+tuningProcess+"% <br/>");
    
    String result = comAPI.getAutoTuningInfo(domainId);
    String[] strs = result.split("\n");


    for(int i = 0; i < strs.length; i++)
    {
    	System.out.println(strs[i]);
        out.println(strs[i]+"<br/>");

    }
    
    if(tuningProcess == 1.0)
    {
    	out.println("Tuning Status: "+tuningProcess+"% <br/>");
	    out.println("Tuning Complete <br/>");
    	//session.setAttribute("isStarted", null);
    }

}
else
	out.println("Model Tuning Start Process Failed...."+"<br/>");
}

	
%>
</body>