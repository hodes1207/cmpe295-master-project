    <%@ page import="MessageLayer.CommunicationAPI"%>
    <%@ page import="util.SessionHashMap"%>

<% 
String id = session.getId(); 
SessionHashMap sessionMap = SessionHashMap.getInstance();
CommunicationAPI comAPI = sessionMap.getSession(id);

int nDomainId = (Integer)session.getAttribute("domainId");
double accuracy = comAPI.getModelAccuracy(nDomainId) * 100;
out.println("Current Model Accuracy: "+accuracy+"% <br/>");
%>