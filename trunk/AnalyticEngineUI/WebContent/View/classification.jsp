
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>Template &middot; Bootstrap</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <!-- Le styles -->
    <link href="css/bootstrap.css" rel="stylesheet">
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
    
    <link href="css/bootstrap-responsive.css" rel="stylesheet">

     <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->

    <script type="text/javascript" src="js/jquery.js"></script>
<script type="text/javascript" src="js/core_function.js" ></script>

    <script src="js/bootstrap-transition.js"></script>
    <script src="js/bootstrap-alert.js"></script>
    <script src="js/bootstrap-modal.js"></script>
    <script src="js/bootstrap-dropdown.js"></script>
    <script src="js/bootstrap-scrollspy.js"></script>
    <script src="js/bootstrap-tab.js"></script>
    <script src="js/bootstrap-tooltip.js"></script>
    <script src="js/bootstrap-popover.js"></script>
    <script src="js/bootstrap-button.js"></script>
    <script src="js/bootstrap-collapse.js"></script>
    <script src="js/bootstrap-carousel.js"></script>
    <script src="js/bootstrap-typeahead.js"></script>  
    
    <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
      <script src="../assets/js/html5shiv.js"></script>
    <![endif]-->

    <!-- Fav and touch icons -->
    <link rel="apple-touch-icon-precomposed" sizes="144x144" href="../assets/ico/apple-touch-icon-144-precomposed.png">
    <link rel="apple-touch-icon-precomposed" sizes="114x114" href="../assets/ico/apple-touch-icon-114-precomposed.png">
      <link rel="apple-touch-icon-precomposed" sizes="72x72" href="../assets/ico/apple-touch-icon-72-precomposed.png">
                    <link rel="apple-touch-icon-precomposed" href="../assets/ico/apple-touch-icon-57-precomposed.png">
                                   <link rel="shortcut icon" href="../assets/ico/favicon.png">
  </head>

  <body>

    <div class="navbar navbar-inverse navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container">
          <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="brand" href="./home.jsp">Analytic Engine</a>
          <div class="nav-collapse collapse">
            <ul class="nav">
              <li class="active">
                <a href="./browse.jsp?page_start_id=-1">Browse</a>
              </li>
              <li class="">
                <a href="./model-tuning.jsp?domainId=-1">Model Tuning</a>
              </li>
              <li class="">
                <a href="./case-search.jsp?page_start_id=-1">Case Search</a>
              </li>
              <li class="">
                <a href="./classification.jsp?ClassificationResult=0">Classification</a>
              </li>
              <li class="">
                <a href="./contact-us.html">Contact Us</a>
              </li>
              <li class="">
                <a href="./about-us.html">About Us</a>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>



<div class="container-fluid">
  <div class="row-fluid">
    <div class="span3">
    
    <div class="well sidebar-nav">
<form id="caseSearchForm" action="../classification" method="post" enctype="multipart/form-data" target="show_classification_frame" >


				Domain: <select class="required" id="domain" name="domain">
					<option value="">Please Select</option>
					<%@ page import="database.Domain"%>
					<%@ page import="java.util.ArrayList"%>
    <%@ page import="MessageLayer.CommunicationAPI"%>
    <%@ page import="util.SessionHashMap"%>
					
					<%
					String id = session.getId(); 
					SessionHashMap sessionMap = SessionHashMap.getInstance();
					CommunicationAPI comAPI = sessionMap.getSession(id);

					ArrayList<Domain> domainList = comAPI.GetDomain();
						for (Domain domain : domainList) {
							String name = domain.domainName;
							out.println("<option  value=\"" + name
								+ "\">" + name + "</option>");
						}
						
 					%>
				</select> <br/>


				Select Input Image:<input type="file" name="file" size="50" />
				<br />
				<input type="submit" value="Start Classification" />
</form>
              </div><!--/.well -->
      <!--Sidebar content-->
    </div>
    
    
    <div class="span8">
    <%
    if(request.getParameter("ClassificationResult")!=null)
	session.setAttribute("ClassificationResult", "");
	%>
<iframe name="show_classification_frame" src="display_classification.jsp" id="show_classification_frame" width="900" height="400">
</iframe>  
      <!--Body content-->
    </div>
  </div>
</div>

      <hr>

      <hr>

      <div class="footer">
        <p>&copy; G38 Haixiao Yang, Jing Zhang, Paramodh Pallapothu 2013</p>
      </div>


  </body>
</html>
