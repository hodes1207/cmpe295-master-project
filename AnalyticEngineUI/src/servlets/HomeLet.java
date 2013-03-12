package servlets;

import java.io.File;
import java.io.IOException; 
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import util.ServiceHelper;

import datamining.ClassifyModel;




import ServiceInterface.EngineService;

//import ServiceInterface.EngineService;
//import database.Domain;
//import database.SecondLevelClass;

@WebServlet(name = "home", urlPatterns = { "/home" })
public class HomeLet extends HttpServlet {
		private static final long serialVersionUID = 1L;
 
	    /**
	     * @see HttpServlet#HttpServlet()
	     */
	    public HomeLet() {
	        super();
	        // TODO Auto-generated constructor stub
	    }

		/**
		 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
		 */
		protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				
			HttpSession session = request.getSession();
			EngineService serv = ServiceHelper.getServer(); 
			serv.startService();

			while (serv.getInitProgress() < 1.0)
				{
					System.out.println(serv.getInitProgress());
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				//session.setAttribute("server", serv);
			    session.setAttribute("page_start_id", -1);
	            //session.setMaxInactiveInterval(arg0)
        		File dir = new File(".");
        		System.out.println("path: "+dir.getCanonicalPath() );

				
			
			
			/*request.setAttribute("ActivePage", "ACCOUNT");
			RequestDispatcher dispatcher = request.getRequestDispatcher("/View/account.jsp");
			dispatcher.forward(request, response);*/
			    
			    
    			response.sendRedirect("/AnalyticEngineUI/View/browse.jsp");

		}
		/**
		 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
		 */
		/*
		protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
         		 	*/

}
