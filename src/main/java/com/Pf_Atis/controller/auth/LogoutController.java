package com.Pf_Atis.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import com.Pf_Artis.dao.DaoFactory;
import com.Pf_Artis.dto.MessageLogout;
import com.Pf_Artis.dto.UserDto;
import com.Pf_Artis.service.facade.UserServiceInterface;
import com.Pf_Artis.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class LogoutController
 */
@WebServlet(name="LogoutController",urlPatterns = {"/api/logout"})
public class LogoutController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UserServiceInterface userService ;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LogoutController() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void init() throws ServletException {
    	
    	DaoFactory daoFactory = DaoFactory.getInstance();
    	userService = new UserServiceImpl(daoFactory);
    	
    }
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		HttpSession session = request.getSession(false);
		System.out.println(session);
		UserDto user = (UserDto) session.getAttribute("user");
		
		if( session != null && user != null ) {
			
			System.out.println("bien deconnecter");
			
			System.out.println("test-all");
			
			try {
		        
				user.setToken("");
		        userService.updateUser(userService.readUser(user.getUserId()));
		        
		    } catch (Exception e) {
		        // Gérer l'exception ici, par exemple, en enregistrant les détails dans les logs
		        e.printStackTrace();
		        // Vous pouvez également envoyer un message d'erreur à l'utilisateur si nécessaire
		    }
			
			session.removeAttribute("user");
			session.invalidate();
			
			MessageLogout logout = new MessageLogout("bien deconnecter");
			
			try {
		        String json = objectMapper.writeValueAsString(logout);
		        response.setContentType("application/json");
		        response.setCharacterEncoding("UTF-8");
		        response.getWriter().write(json);
		    } catch (IOException e) {
		        // Gérer les exceptions liées à la réponse HTTP, par exemple, en enregistrant les détails dans les logs
		        e.printStackTrace();
		    }

		}else {
			
			System.out.println("test-log");
			
			MessageLogout logout = new MessageLogout("déjà deconnecter");
			
			String json = objectMapper.writeValueAsString(logout);
			response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        
	        response.getWriter().write(json);
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
