
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>Analytic &middot; Engine</title>
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
    <link rel="stylesheet" type="text/css" href="css/tree.css">
    
    
     <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->

    <script type="text/javascript" src="js/jquery.js"></script>
<script type="text/javascript" src="js/yahoo.js" ></script>
<script type="text/javascript" src="js/event.js"></script>
<script type="text/javascript" src="js/dom.js" ></script>
<script type="text/javascript" src="js/treeview.js" ></script>
<script type="text/javascript" src="js/jktreeview.js" ></script>
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
                <a href="./model-tuning.jsp">Model Tuning</a>
              </li>
              <li class="">
                <a href="./case-search.jsp?page_start_id=-1">Case Search</a>
              </li>
              <li class="">
                <a href="./classification.jsp">Classification</a>
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
    <div class="span2">
    <div class="well sidebar-nav"> 
    		
    <%@ page import="ServiceInterface.EngineService"%>
    <%@ page import="database.Domain"%>
    <%@ page import="database.SecondLevelClass"%>
    <%@ page import="java.util.ArrayList"%>

<form id="tree_form" action="../browse" method="post" target="show_photo_frame">   
<input type="hidden" id="domain_name" name="domain_name"/>         
<div id="domainTree"></div>
<script type="text/javascript">
var pinetree=new jktreeview("domainTree");

<%
EngineService server = (EngineService)session.getAttribute("server");
ArrayList<Domain> domains = server.GetDomain(); 
%>


<%
for(int  i = 0; i < domains.size(); i++)
{
	ArrayList<SecondLevelClass> classAry = server.GetClasses(domains.get(i).domainId);
    String name = domains.get(i).domainName;
	%>
	pinetree.addItem("<%=name%>");  //Add this item to root node 
	<%
}

%>

pinetree.treetop.draw(); //REQUIRED LINE: Initalize tree


	
</script>


	</form>

	</div>
      <!--Sidebar content-->

    </div>
    <div class="span5">
      <!--Body content-->
<iframe name="model_training_frame" src="model_training.jsp" id="model_training_frame" width="900" height="400">
</iframe>  

    </div>
    <div class="span5">
      <!--Body content-->
<iframe name="parameter_tuning_frame" src="parameter_tuning.jsp" id="parameter_tuning_frame" width="900" height="400">
</iframe>  

    </div>
  </div>
</div>

<script type="text/javascript">
pinetree.treetop.subscribe("dblClickEvent", function(oArgs){
	alert(oArgs.node.label + " label was double clicked, belong to" +oArgs.node.parent.label+" "+document.getElementById("page_start_id").getAttribute("value"));
	
	document.getElementById("domain_name").setAttribute("value", oArgs.node.parent.label);
	document.getElementById("tree_form").submit();
	});
</script>

      <hr>

      <hr>

      <div class="footer">
        <p>&copy; G38 Haixiao Yang, Jing Zhang, Paramodh Pallapothu 2013</p>
      </div>

    <!-- /container -->



  </body>
</html>
