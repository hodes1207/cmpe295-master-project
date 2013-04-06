package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import util.SessionHashMap;
import MessageLayer.CommunicationAPI;

import database.Domain;
import database.SecondLevelClass;


/**
 * Servlet implementation class BrowseLet
 */
@WebServlet(name = "browse", urlPatterns = { "/browse" })
public class BrowseLet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BrowseLet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		

	/*	response.setContentType("text/html; charset = gb2312");
		PrintWriter out = new PrintWriter(response.getOutputStream());
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Test</title>");
		out.println("</head>");
		out.println("<body>");


		int imgId = (1<<16)+1;
		
		out.println("<img src=\"show-photo?imgId="+imgId+"\">");
		out.println("</body>");
		out.println();
		out.flush();
		out.close(); */
		//HttpSession session = request.getSession();
		//session.setAttribute("page_start_id", 1);
		
		RequestDispatcher dispatcher = request.getRequestDispatcher("/View/display_photo.jsp");
		dispatcher.forward(request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String domainName =  request.getParameter("domain_name"); 					
		String className =  request.getParameter("class_name"); 					
		HttpSession session = request.getSession(); 
		String id = session.getId();
		SessionHashMap sessionMap = SessionHashMap.getInstance();
		CommunicationAPI comAPI = sessionMap.getSession(id);
		
        int domainId=0, classId=0;
		ArrayList<Domain> domains = comAPI.GetDomain();
		for(Domain d: domains)
		{
			if(d.getDomainName().equals(domainName))
			{
				domainId = d.getDomainId();
			}
		}
		
		ArrayList<SecondLevelClass> classList = comAPI.GetClasses(domainId);
		for(SecondLevelClass c :  classList)
		{
			if(c.getClassName().equals(className))
			{
				classId = c.getClassId();
			}
		}
		
		System.out.println(domainName+": "+className);
		
		ArrayList<Long> imgIdList = comAPI.GetPicId(classId);
		Long[] imgIdAry = new Long[imgIdList.size()];
		imgIdAry = imgIdList.toArray(imgIdAry);
		
		session.setAttribute("domainId", domainId);   //test
		
		response.setContentType("text/html");
		session.setAttribute("imgIdAry", imgIdAry);
		session.setAttribute("page_start_id", 0);
		System.out.println(imgIdAry[0]+", "+imgIdAry[1]+", "+imgIdAry[2]);
		//response.sendRedirect("/View/display_photo.jsp");
		doGet(request, response);
		return;

	}

}
