package com.Pf_Atis.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Date;

import com.Pf_Artis.dao.DaoFactory;
import com.Pf_Artis.dto.AuthDto;
import com.Pf_Artis.dto.UserDto;
import com.Pf_Artis.service.facade.UserServiceInterface;
import com.Pf_Artis.service.impl.UserServiceImpl;
import com.Pf_Artis.shared.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class AuthController
 */
@WebServlet(name="AuthController",urlPatterns = {"/api/authUser"})
public class AuthController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ObjectMapper objectMapper = new ObjectMapper();
	private UserServiceInterface userService;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AuthController() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public void init() throws ServletException {
    	
    	userService = new UserServiceImpl(DaoFactory.getInstance());
    	
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		System.out.println(session.getAttribute("userId"));
		Integer userId = (Integer) session.getAttribute("userId");
		
		
		if( userId !=null ) {
			UserDto user = userService.readUser(userId);
			AuthDto authDto = new AuthDto( user.getUserId() , user.getToken() , user.getRole().getName() , "Success" );
			
			try {
    			
    			String json = objectMapper.writeValueAsString(authDto);
    			
    			response.setContentType("application/json");
    	        response.setCharacterEncoding("UTF-8");
    	        
    	        response.getWriter().write(json);
    	        
    		} catch (Exception e) {
    			
    			e.printStackTrace();
    			
    		}
		}else {
			
			ErrorMessage message = new ErrorMessage("aucune utilisateur connecter.", new Date(), 400);

			String json = objectMapper.writeValueAsString(message);
			
			response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        
	        response.getWriter().write(json);
			
		}
		
	}

}
