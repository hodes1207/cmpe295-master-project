package servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import database.MedicalParameter;

import util.SessionHashMap;
import MessageLayer.CommunicationAPI;


/**
 * Servlet implementation class ShowPhoto
 */
@WebServlet(name = "showphoto", urlPatterns = { "/showphoto" })
public class ShowPhoto extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ShowPhoto() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession(); 
		String sessionId = session.getId();
		SessionHashMap sessionMap = SessionHashMap.getInstance();
		CommunicationAPI comAPI = sessionMap.getSession(sessionId);
		
		System.out.println(" CommunicationAPI in ShowPhoto.java: "+comAPI);
		
		String id = request.getParameter("imgId");
		System.out.println("img id: "+id);
		Long imgId = Long.valueOf(id);

		//ArrayList<Long> ids = server.GetPicId(imgId);
		//Long testId = ids.get(0);
		/*
		int ndomainId = (Integer) session.getAttribute("domainId"); 
		MedicalParameter info = comAPI.GetCurrentModelInfo(ndomainId);
		System.out.println("info object: "+ info);
		int foldN = info.getnFold();
		System.out.println("fold number: "+ foldN); */
				
		
		byte[] content = comAPI.RetrieveImg(imgId);

		if(content == null){
			int i = 0;
			System.out.println("i: "+i);
		}
		
		System.out.println(" content[] : "+content);
		
		response.setContentType("image/png"); 
		ServletOutputStream  outs = response.getOutputStream(); 
		outs.write(content);
		outs.flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
