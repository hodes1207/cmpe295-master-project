package servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class PhotoFrameLet
 */
@WebServlet(name = "case_search_photo_frame", urlPatterns = {"/case_search_photo_frame"})
public class CaseSearchPhotoFrameLet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CaseSearchPhotoFrameLet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		RequestDispatcher dispatcher = request.getRequestDispatcher("/View/case_search_display_photo.jsp");
		dispatcher.forward(request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String pageFunctioncall = request.getParameter("pageFunctionCall");
		
		int currentPageStartId = 0;
		
		if(pageFunctioncall.equals("first_post"))
			currentPageStartId = Integer.parseInt(request.getParameter("demo_thumbs_offset_first_post"));
		else if (pageFunctioncall.equals("last_post"))
			currentPageStartId = Integer.parseInt(request.getParameter("demo_thumbs_offset_last_post"));
		else if(pageFunctioncall.equals("prev_post"))
			currentPageStartId = Integer.parseInt(request.getParameter("demo_thumbs_offset_prev_post"));
		else if(pageFunctioncall.equals("next_post"))
			currentPageStartId = Integer.parseInt(request.getParameter("demo_thumbs_offset_next_post"));
		else if(pageFunctioncall.equals("fast_back_post"))
			currentPageStartId = Integer.parseInt(request.getParameter("demo_thumbs_offset_fast_back_post"));
		else if(pageFunctioncall.equals("fast_next_post"))
			currentPageStartId = Integer.parseInt(request.getParameter("demo_thumbs_offset_fast_next_post"));
		
		System.out.println(pageFunctioncall+" "+currentPageStartId);		
		HttpSession session = request.getSession();
		session.setAttribute("page_start_id", currentPageStartId);
		//response.sendRedirect("/AnalyticEngineUI/View/display_photo.jsp");
		doGet(request, response);
		return;
	}

}
