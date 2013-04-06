package servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import util.SessionHashMap;
import MessageLayer.CommunicationAPI;


/**
 * Servlet implementation class TuningLet
 */
@WebServlet(name = "modelconfig", urlPatterns = {"/TuningLet"})
public class TuningLet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TuningLet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		RequestDispatcher dispatcher = request.getRequestDispatcher("/View/display_tuning_result.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String folderNum = request.getParameter("fold_selection"); 
		HttpSession session = request.getSession(); 
		String id = session.getId();
		SessionHashMap sessionMap = SessionHashMap.getInstance();
		CommunicationAPI comAPI = sessionMap.getSession(id);

		int domainId = (Integer)session.getAttribute("domainId"); 
		comAPI.SetAutoTuningFoldNum(domainId, Integer.parseInt(folderNum));
		boolean isStarted = comAPI.StartAutoTuning(domainId);
		session.setAttribute("isStarted", isStarted);
			doGet(request, response);
			return;

	}

}
