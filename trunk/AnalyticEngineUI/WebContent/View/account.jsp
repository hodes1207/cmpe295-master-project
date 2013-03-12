

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>Template &middot; Bootstrap</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Analytic Medical Engine">
    <meta name="author" content="SJSU G38">
    <!-- Let Page Auto Refresh Every 3 Second -->

    
    <!-- Le styles -->
    <link href="css/bootstrap.css" rel="stylesheet">
    <link href="css/account.css" rel="stylesheet">
    <link href="css/bootstrap-responsive.css" rel="stylesheet">
    <style type="text/css">
    </style>
    

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
                <a href="./browse.jsp">Browse</a>
              </li>
              <li class="">
                <a href="./model-tuning.jsp">Model Tuning</a>
              </li>
              <li class="">
                <a href="./case-search.jsp">Case Search</a>
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

      <div class="jumbotron">
        <h1>Server Start in Progress</h1>
        <p class="lead">Please wait until the progress bar reach 100% before selecting any service from the panel.</p>
        
        <div class="container">
            <div class="progress progress-striped active">
                <div class="bar" style="width: 0%;">
                <%@ page import="ServiceInterface.EngineService"%>
<% 
EngineService server = (EngineService)session.getAttribute("server");
//EngineService server = new EngineService();
			if (server.getInitProgress() < 1.0)
			{  
				
			%>
				<script type="text/javascript" charset="utf-8">
				var progress = setInterval(function() {
				    var $bar = $('.bar'); 
				    /*
				    if ($bar.width()==400) {
				        clearInterval(progress);
				        $('.progress').removeClass('active');
				    } else {
				        $bar.width($bar.width()+20);
				    } */ 
				    $bar.text( <%=server.getInitProgress()%>*100*4 + "%");
				}, 800); 
				</script>
				<% 
				out.println(server.getInitProgress());
					
			} 
%>
</div>
                </div>
            </div>
      </div>



      <hr>

      <hr>

      <div class="footer">
        <p>&copy; G38 Haixiao Yang, Jing Zhang, Paramodh Pallapothu 2013</p>
      </div>


    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    
    <script src="js/jquery.js"></script>
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

  </body>
</html>
