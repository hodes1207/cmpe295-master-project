package servlets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import util.FileUpLoad;
import util.ServiceHelper;

import database.Domain;

import ServiceInterface.EngineService;

/**
 * Servlet implementation class CaseSearchLet
 */
@WebServlet(name = "casesearch", urlPatterns = { "/casesearch" })
public class CaseSearchLet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	//protected String imageFilePath = null;

	  public void init(ServletConfig servletConfig) throws ServletException{
		  super.init(servletConfig);
	    //this.imageFilePath = servletConfig.getInitParameter("image-upload");
	  }   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CaseSearchLet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub		
		//HttpSession session = request.getSession();
		//session.setAttribute("page_start_id", 0);
		//System.out.println("page_start_id" + session.getAttribute("page_start_id"));
		RequestDispatcher dispatcher = request.getRequestDispatcher("/View/case_search_display_photo.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		EngineService server = ServiceHelper.getServer();//(EngineService) session.getAttribute("server");
		

		String domainN = "";
		byte[] image = null;
		
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
        		if(image==null)
        		{
        			System.out.println("image is null");
        		}
        		ArrayList<Long> resultList = server.SimilaritySearch(image, 10);
        		Long[] resultAry = new Long[resultList.size()];
        		resultAry = resultList.toArray(resultAry);
        		response.setContentType("text/html");

        		session.setAttribute("imgIdAry", resultAry);
        		session.setAttribute("page_start_id", 0);
        		System.out.println(resultList.get(0)+", "+resultList.get(1)+", "+resultList.get(2));
        	    doGet(request, response);
                return;
        		//response.sendRedirect("/View/display_photo.jsp");
        	}catch(FileUploadException ex){
        		ex.printStackTrace();
        	}
        }
        //else do nothing
		
	}

}
