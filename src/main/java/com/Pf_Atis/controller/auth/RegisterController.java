package com.Pf_Atis.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.mindrot.jbcrypt.BCrypt;

import com.Pf_Artis.config.GenerateJwtToken;
import com.Pf_Artis.dao.DaoFactory;
import com.Pf_Artis.dto.RoleDto;
import com.Pf_Artis.dto.UserDto;
import com.Pf_Artis.service.facade.RoleService;
import com.Pf_Artis.service.facade.UserServiceInterface;
import com.Pf_Artis.service.impl.RoleServiceImpl;
import com.Pf_Artis.service.impl.UserServiceImpl;
import com.Pf_Artis.shared.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class RegisterController
 */
@WebServlet(name="RegisterController",urlPatterns = {"/api/register"} )
public class RegisterController extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private UserServiceInterface userService ;
	private RoleService roleService;
       
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
    	roleService = new RoleServiceImpl(daoFactory);
    	
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
        UserDto userDto = objectMapper.readValue(request.getReader(), UserDto.class);
        
        String hashedPassword = BCrypt.hashpw(userDto.getPassword(), BCrypt.gensalt());
        
        userDto.setPassword(hashedPassword);
        
        String jwt = null;
		try {
			
			jwt = GenerateJwtToken.generateJwtToken(userDto.getEmail());
			
		} catch (NoSuchAlgorithmException e) {
			
			e.printStackTrace();
			
		}
    	
		userDto.setToken(jwt);
        
		RoleDto roleDto  = roleService.findByName(userDto.getRole().getName());
        
        if(roleDto.getRoleId()!=null) {
        	
        	userDto.setRole(roleDto);
        	UserDto saved = userService.createUser(userDto);
        	
        	saved.setPassword(null);
            request.getSession().setAttribute("user", saved);
            try {
            	
            	String json = objectMapper.writeValueAsString(saved);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                
                response.getWriter().write(json);
                
    		} catch (Exception e) {

    			e.printStackTrace();
    			
    		}
        }else {

			ErrorMessage message = new ErrorMessage("cette role n'existe pas.", new Date(), 400);

			String json = objectMapper.writeValueAsString(message);
			
			response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        
	        response.getWriter().write(json);
            
        }	
	}

}
