<!DOCTYPE html>
<html lang="en">
  <head>
    <!-- Le styles -->
    <link href="View/css/bootstrap.css" rel="stylesheet">
    <style type="text/css">
      body {
        padding-top: 60px;
        padding-bottom: 40px;
      }
      .sidebar-nav {
        padding: 9px 0;
      }

      @media (max-width: 980px) {
        /* Enable use of floated navbar text */
        .navbar-text.pull-right {
          float: none;
          padding-left: 5px;
          padding-right: 5px;
        }
      }
    </style>
    
    <link href="View/css/bootstrap-responsive.css" rel="stylesheet">
</head>

<body>
	<div class="container-fluid">
  <div class="row-fluid">

    <div class="span4" align="left">
    <%
    String id = session.getId(); 
    SessionHashMap sessionMap = SessionHashMap.getInstance();
    CommunicationAPI comAPI = sessionMap.getSession(id);

    int domainId = (Integer)session.getAttribute("domainId");

    if(domainId == -1)
    {}
    else{
    %>
        <div class="well sidebar-nav" > 
<form id="config_form" action="modeltraining" method="post" target="display_training_result_frame"> 
    <%@ page import="MessageLayer.CommunicationAPI"%>
    <%@ page import="util.SessionHashMap"%>
<%

//String tuningInfo = server.getAutoTuningInfo(domainId);
int foldNum = comAPI.GetAutoTuningFoldNum(domainId);
boolean isRBFEnabled = comAPI.isRBFTuningEnabled(domainId);

System.out.println(isRBFEnabled);
%> 
    Model Configuration <br/>
    <% if(isRBFEnabled == true) {%>
    <input type="radio" name="group1" value="RBF" checked="checked"> RBF<br>
    C: <input type="text" name="rbf_c"> <br>
    G: <input type="text" name="rbf_g"><br>
    <input type="radio" name="group1" value="Linear"> Linear<br>
    C: <input type="text" name="linear_c"><br>
    <%} else {%>
    <input type="radio" name="group1" value="RBF" > RBF<br>
    C: <input type="text" name="rbf_c"> <br>
    G: <input type="text" name="rbf_g"><br>
    <input type="radio" name="group1" value="Linear" checked="checked"> Linear<br>
    C: <input type="text" name="linear_c"><br>
    <%} %>
    <input type="submit" value="Start Training">
    </form>
      <!--Body content-->
<iframe name="display_training_result_frame" src="View/display_training_result.jsp" id="display_training_result_frame" width="200" height="100">
</iframe>  
       </div>
    </div>
    
    <div class="span4"  align="left">
           <div class="well sidebar-nav" > 
      <!--Body content-->
      <form id="model_accuracy_form" action="modelaccuracy" method="post" target="display_model_accuracy_frame">   
      Display current model accuracy  
      <input type="submit" value="Start">
      </form>
<iframe name="display_model_accuracy_frame" src="View/display_model_accuracy.jsp" id="display_model_accuracy_frame" width="160" height="50" >
</iframe>  
      
      <form id="tuning_form" action="modelconfig" method="post" target="display_tuning_result_frame">   
      Model Parameter Auto Tuning <br>
      <select name="fold_selection" >
      <% if (foldNum == 4)  {%>
  		<option value="4" selected="selected">Four Fold</option>
  		<option value="5" >Five Fold</option>
  		<option value="6" >Six Fold</option>
  		<option value="7" >Seven Fold</option>
  		<option value="8" >Eight Fold</option>
  		<option value="9" >Nine Fold</option>
  		<option value="10">Ten Fold</option>
  		<%} else if(foldNum == 5) {%>
  		<option value="4" >Four Fold</option>
  		<option value="5" selected="selected">Five Fold</option>
  		<option value="6" >Six Fold</option>
  		<option value="7" >Seven Fold</option>
  		<option value="8" >Eight Fold</option>
  		<option value="9" >Nine Fold</option>
  		<option value="10">Ten Fold</option>
  		<%} else if (foldNum == 6) {%>
  		<option value="4" >Four Fold</option>
  		<option value="5" >Five Fold</option>
  		<option value="6" selected="selected">Six Fold</option>
  		<option value="7" >Seven Fold</option>
  		<option value="8" >Eight Fold</option>
  		<option value="9" >Nine Fold</option>
  		<option value="10">Ten Fold</option>
  		<%} else if (foldNum == 7) {%>
  		<option value="4" >Four Fold</option>
  		<option value="5" >Five Fold</option>
  		<option value="6" >Six Fold</option>
  		<option value="7" selected="selected">Seven Fold</option>
  		<option value="8" >Eight Fold</option>
  		<option value="9" >Nine Fold</option>
  		<option value="10">Ten Fold</option>
  		<%} else if (foldNum == 8) {%>
  		<option value="4" >Four Fold</option>
  		<option value="5" >Five Fold</option>
  		<option value="6" >Six Fold</option>
  		<option value="7" >Seven Fold</option>
  		<option value="8" selected="selected">Eight Fold</option>
  		<option value="9" >Nine Fold</option>
  		<option value="10">Ten Fold</option>
  		<%} else if (foldNum == 9) {%>
  		<option value="4" >Four Fold</option>
  		<option value="5" >Five Fold</option>
  		<option value="6" >Six Fold</option>
  		<option value="7" >Seven Fold</option>
  		<option value="8" >Eight Fold</option>
  		<option value="9" selected="selected">Nine Fold</option>
  		<option value="10">Ten Fold</option>
  		<%} else if (foldNum == 10) {%>
  		<option value="4" >Four Fold</option>
  		<option value="5" >Five Fold</option>
  		<option value="6" >Six Fold</option>
  		<option value="7" >Seven Fold</option>
  		<option value="8" >Eight Fold</option>
  		<option value="9" >Nine Fold</option>
  		<option value="10" selected="selected">Ten Fold</option>
  		<%} else{ %>
  		<option value="4" >Four Fold</option>
  		<option value="5" >Five Fold</option>
  		<option value="6" >Six Fold</option>
  		<option value="7" >Seven Fold</option>
  		<option value="8" >Eight Fold</option>
  		<option value="9" >Nine Fold</option>
  		<option value="10">Ten Fold</option>
  		<%} %>
      </select>
      
      <input type="submit" value="Start Tuning">
      </form>

<!-- <iframe name="display_tuning_result_frame" src="display_tuning_result.jsp" id="display_tuning_result_frame" width="200" height="100">
</iframe>   -->
</div>
    </div>

    <div class="span2" align="left">
<iframe name="display_tuning_result_frame" src="View/display_tuning_result.jsp" id="display_tuning_result_frame" width="200" height="300">
</iframe>  
</div>
    
    </div>
    </div>
    <%} %>
    </body>
