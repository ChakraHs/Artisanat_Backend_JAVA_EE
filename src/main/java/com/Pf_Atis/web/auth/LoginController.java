package com.Pf_Atis.web.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.mindrot.jbcrypt.BCrypt;

import com.Pf_Artis.dao.DaoFactory;
import com.Pf_Artis.dto.UserLoginDto;
import com.Pf_Artis.models.User;
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		ObjectMapper objectMapper = new ObjectMapper();
        UserLoginDto userDto = objectMapper.readValue( request.getReader() , UserLoginDto.class );
        
        User user = userService.getUserByEmail(userDto.getEmail());
        
        if (BCrypt.checkpw(userDto.getPassword(), user.getPassword())) {
        	
        	request.getSession().setAttribute("user", user);
        	user.setPassword(null);
        	
        	try {
    			
    			String json = objectMapper.writeValueAsString(user);
    			
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
