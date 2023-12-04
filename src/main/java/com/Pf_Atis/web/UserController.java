package com.Pf_Atis.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import com.Pf_Artis.dao.DaoFactory;
import com.Pf_Artis.models.User;
import com.Pf_Artis.service.facade.UserServiceInterface;
import com.Pf_Artis.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class UserController
 */
@WebServlet(name="UserController",urlPatterns = {"/api/users/*"})
public class UserController extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private UserServiceInterface userService ;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserController() {
    
    	super();
        
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
		
		String path=request.getPathInfo();
		
		if( path == null || path.split("/")[1].equals("*")) {
			
			List<User> users = userService.getAllUsers();
			
			if(!users.isEmpty()) {
					
				try {
				
					ObjectMapper objectMapper = new ObjectMapper();
					String json = objectMapper.writeValueAsString(users);
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write(json);
			        
				} catch (Exception e) {
					
					e.printStackTrace();
					
				}
				
			}
			else {
				
				response.getWriter().write("Aucun utilisateur trouvé.");
				
			}
		}
		else {
			
			String[] pathParts = path.split("/");
			
			if(pathParts[1].equals("id")) {
				
				Long Id = Long.parseLong(pathParts[2]);
				User user = userService.readUser(Id);
				user.setToken("");
				
				if( user.getId() == null ) {
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write("Aucun utilisateur trouvé.");
			        
				}else {
					
					try {
						
						ObjectMapper objectMapper = new ObjectMapper();
						String json = objectMapper.writeValueAsString(user);
						
						response.setContentType("application/json");
				        response.setCharacterEncoding("UTF-8");
				        
				        response.getWriter().write(json);
				        
					} catch (Exception e) {
						
						e.printStackTrace();
						
					}
				}
			}
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		ObjectMapper objectMapper = new ObjectMapper();
        User newUser = objectMapper.readValue(request.getReader(), User.class);
        newUser.setToken("");
        User user = userService.createUser(newUser);
        
        try {
        	
        	String json = objectMapper.writeValueAsString(user);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            response.getWriter().write(json);
            
		} catch (Exception e) {

			e.printStackTrace();
			
		}
	}
	
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		ObjectMapper objectMapper = new ObjectMapper();
        User newUser = objectMapper.readValue(request.getReader(), User.class);
        Long Id = Long.parseLong(request.getPathInfo().split("/")[2]);
        newUser.setId(Id);
        newUser.setToken("");
        User user = userService.updateUser(newUser);
        
        try {
        	
        	String json = objectMapper.writeValueAsString(user);
        	response.setContentType("application/json");
        	response.setCharacterEncoding("UTF-8");
            
        	response.getWriter().write(json);
            
		} catch (Exception e) {

			e.printStackTrace();
			
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        Long Id = Long.parseLong(request.getPathInfo().split("/")[2]);
        userService.deleteUser(Id);
        
        if(userService.readUser(Id).getId()== null) {
        	
        	response.setContentType("application/json");
        	response.setCharacterEncoding("UTF-8");
	        
        	response.getWriter().write("delete avec success.");
	        
        }else {
        	
        	response.setContentType("application/json");
        	response.setCharacterEncoding("UTF-8");
	        
        	response.getWriter().write("delete failed.");
	        
        }
		
	}

}
