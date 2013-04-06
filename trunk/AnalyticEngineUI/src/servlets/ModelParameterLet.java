package servlets;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import database.Domain;


import MessageLayer.CommunicationAPI;

import util.SessionHashMap;


/**
 * Servlet implementation class ModelParameterLet
 */
@WebServlet(name = "modeltuning", urlPatterns = {"/ModelParameterLet"})
public class ModelParameterLet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ModelParameterLet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		RequestDispatcher dispatcher = request.getRequestDispatcher("/View/model_config.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String domainName =  request.getParameter("domain_name"); 	
		System.out.println("Model Parameter let: "+domainName);
		HttpSession session = request.getSession(); 
		String id = session.getId();
		SessionHashMap sessionMap = SessionHashMap.getInstance();
		CommunicationAPI comAPI = sessionMap.getSession(id);
				
        int domainId=0;
		ArrayList<database.Domain> domains = comAPI.GetDomain();
		for(Domain d: domains)
		{
			if(d.getDomainName().equals(domainName))
			{
				domainId = d.getDomainId();
			}
		}
		response.setContentType("text/html");
		session.setAttribute("domainId", domainId);
		System.out.println(domainId);
		doGet(request, response);
		return;
		
	}

}
