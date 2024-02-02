package com.Pf_Atis.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.mindrot.jbcrypt.BCrypt;

import com.Pf_Artis.config.GenerateJwtToken;
import com.Pf_Artis.dao.DaoFactory;
import com.Pf_Artis.dto.AuthDto;
import com.Pf_Artis.dto.UserDto;
import com.Pf_Artis.dto.UserLoginDto;
import com.Pf_Artis.service.facade.UserServiceInterface;
import com.Pf_Artis.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class LoginController
 */
@WebServlet(name="LoginController",urlPatterns = {"/api/login"})
public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UserServiceInterface userService ;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginController() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public void init() throws ServletException {
    	
    	DaoFactory daoFactory = DaoFactory.getInstance();
    	userService = new UserServiceImpl(daoFactory);
    	
    }


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		ObjectMapper objectMapper = new ObjectMapper();
        UserLoginDto userDto = objectMapper.readValue( request.getReader() , UserLoginDto.class );
        
        UserDto user = userService.getUserByEmail(userDto.getEmail());
        System.out.println(user);
        
        if ( BCrypt.checkpw( userDto.getPassword() , user.getPassword() ) ) {
        	
        	System.out.println("test");
        	
        	String jwt = null;
			try {
				
				jwt = GenerateJwtToken.generateJwtToken(user.getEmail());
				
			} catch (NoSuchAlgorithmException e) {
				
				e.printStackTrace();
				
			}
        	
        	System.out.println(jwt);
        	
        	user.setToken(jwt);
        	userService.updateUser(user);
        	
        	AuthDto authDto = new AuthDto( user.getUserId() , jwt , user.getRole().getName() , "Success" );
        	
        	HttpSession session = request.getSession();
        	session.setAttribute("userId", user.getUserId());
        	
        	System.out.println(session.getAttribute("userId"));
        	
        	try {
    			
    			String json = objectMapper.writeValueAsString(authDto);
    			
    			response.setContentType("application/json");
    	        response.setCharacterEncoding("UTF-8");
    	        
    	        response.getWriter().write(json);
    	        
    		} catch (Exception e) {
    			
    			e.printStackTrace();
    			
    		}
        }else {
        	
        	try {
    			
    			response.setContentType("application/json");
    	        response.setCharacterEncoding("UTF-8");
    	        
    	        response.getWriter().write("Authentification échouée");
    	        
    		} catch (Exception e) {
    			
    			e.printStackTrace();
    			
    		}
        	
        }
	}

}
