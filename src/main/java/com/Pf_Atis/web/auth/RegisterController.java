package com.Pf_Atis.web.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.mindrot.jbcrypt.BCrypt;

import com.Pf_Artis.dao.DaoFactory;
import com.Pf_Artis.models.User;
import com.Pf_Artis.service.facade.UserServiceInterface;
import com.Pf_Artis.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class RegisterController
 */
@WebServlet(name="RegisterController",urlPatterns = {"/api/register"} )
public class RegisterController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UserServiceInterface userService ;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterController() {
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
        User newUser = objectMapper.readValue(request.getReader(), User.class);
        
        //String jwtToken = GenerateJwtToken.generateJwtToken(newUser.getEmail(), newUser.getRole());
        
        //newUser.setToken(jwtToken);
        
        String hashedPassword = BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt());
        
        newUser.setPassword(hashedPassword);
        
        User user = userService.createUser(newUser);
        request.getSession().setAttribute("user", user);
        try {
        	
        	String json = objectMapper.writeValueAsString(user);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            response.getWriter().write(json);
            
		} catch (Exception e) {

			e.printStackTrace();
			
		}
		
	}

}
