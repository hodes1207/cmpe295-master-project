<!doctype html public "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Image Test</title>



</head>

<body>

<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="ServiceInterface.EngineService"%>
    <%@ page import="database.Domain"%>
    <%@ page import="database.SecondLevelClass"%>
    <%@ page import="java.util.ArrayList"%>
    <%@ page import="java.io.OutputStream;"%>
    
<%
EngineService server = (EngineService)session.getAttribute("server");
String id = request.getParameter("imgId");
int imgId = Integer.parseInt(id);
System.out.println(imgId);
ArrayList<Long> ids = server.GetPicId(imgId);
Long testId = ids.get(0);
byte[] content = server.RetrieveImg(testId);

response.setContentType("image/png"); 
OutputStream outs = response.getOutputStream(); 
outs.write(content);
outs.flush();
outs.close();
%>



  </body>
</html>
 
