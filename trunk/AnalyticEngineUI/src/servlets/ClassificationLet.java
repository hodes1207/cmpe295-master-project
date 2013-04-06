package servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import database.Domain;

import util.FileUpLoad;
import util.SessionHashMap;
import MessageLayer.CommunicationAPI;


/**
 * Servlet implementation class ClassificationLet
 */
@WebServlet(name = "classification", urlPatterns = {"/classification"})
public class ClassificationLet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ClassificationLet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		RequestDispatcher dispatcher = request.getRequestDispatcher("/View/display_classification.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession(); 
		String id = session.getId();
		SessionHashMap sessionMap = SessionHashMap.getInstance();
		CommunicationAPI comAPI = sessionMap.getSession(id);
		

		String domainN = "";
		byte[] image = null;
		
        response.setContentType("text/html");
        boolean isMultiPart = ServletFileUpload.isMultipartContent(request);
        
        if(isMultiPart)
        {
        	ServletFileUpload upload = new ServletFileUpload();
        	try{ 
        		FileItemIterator itr = upload.getItemIterator(request);
        		while(itr.hasNext())
        		{
        			FileItemStream itemStr = itr.next();
        			if(itemStr.isFormField())
        			{ // do field specific process
        				String fieldN = itemStr.getFieldName();
        				InputStream in = itemStr.openStream();
                        byte[] b = new byte[in.available()];
                        in.read(b);
                        String value = new String(b);
                        System.out.println(value);
                        response.getWriter().println(fieldN + ": "+value+"<br/>");
                        
                        if(fieldN.equals("domain"))
                        	domainN = value;
        			}
        			else
        			{ // do file upload process
        				String filePath = getServletContext().getRealPath("/");
        				
        				
        				if(FileUpLoad.processFile(filePath, itemStr))
        				{
        					image = FileUpLoad.getImageByteAry();
        					response.getWriter().println("file uploaded successfully: "+filePath);
        				}
        				else
        					response.getWriter().println("file uploading failed");

        			}
        		}
        		
                int nDomainId=0;
        		ArrayList<Domain> domains = comAPI.GetDomain();
        		for(Domain d: domains)
        		{
        			if(d.getDomainName().equals(domainN))
        			{
        				nDomainId = d.getDomainId();
        			}
        		}

        		
        		String result = comAPI.classificationEstimation(image, nDomainId);
        		session.setAttribute("ClassificationResult", result);
        		session.setAttribute("uploadedImg", image);
        		System.out.println(result);
        		doGet(request, response);
        		//response.sendRedirect("/View/classification.jsp");
        	}catch(FileUploadException ex){
        		ex.printStackTrace();
        	}
        }
	}

}
