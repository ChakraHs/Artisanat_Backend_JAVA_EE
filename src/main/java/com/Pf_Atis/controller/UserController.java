package com.Pf_Atis.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

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
 * Servlet implementation class UserController
 */
@WebServlet(name="UserController",urlPatterns = {"/api/users/*"})
public class UserController extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private UserServiceInterface userService ;
	private RoleService roleService;
	private ObjectMapper objectMapper = new ObjectMapper();
       
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
    	roleService = new RoleServiceImpl(daoFactory);
    	
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String path=request.getPathInfo();
		
		if( path == null || path.split("/")[1].equals("*")) {
			
			List<UserDto> users = userService.getAllUsers();
			
			if(!users.isEmpty()) {
					
				try {
				
					String json = objectMapper.writeValueAsString(users);
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write(json);
			        
				} catch (Exception e) {
					
					e.printStackTrace();
					
				}
				
			}
			else {

				ErrorMessage message = new ErrorMessage("Aucun utilisateur trouvé.", new Date(), 400);

				String json = this.objectMapper.writeValueAsString(message);
				
				response.setContentType("application/json");
		        response.setCharacterEncoding("UTF-8");
		        
		        response.getWriter().write(json);
				
			}
		}
		else {
			
			String[] pathParts = path.split("/");
			
			if(pathParts[1].equals("id")) {
				
				Integer Id = Integer.parseInt(pathParts[2]);
				UserDto userDto = userService.readUser(Id);
				userDto.setToken("");
				
				if( userDto.getUserId() == null ) {

					ErrorMessage message = new ErrorMessage("Aucun utilisateur trouvé.", new Date(), 400);

					String json = this.objectMapper.writeValueAsString(message);
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write(json);
					
				}else {
					
					try {
					
						String json = objectMapper.writeValueAsString(userDto);
						
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
		
        UserDto newUser = objectMapper.readValue(request.getReader(), UserDto.class);
        newUser.setToken("");
        
        RoleDto roleDto  = roleService.findByName(newUser.getRole().getName());
        
        if(roleDto.getRoleId()!=null) {
        	
        	newUser.setRole(roleDto);
        	UserDto userDto = userService.createUser(newUser);
        	
        	try {
            	
            	String json = objectMapper.writeValueAsString(userDto);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                
                response.getWriter().write(json);
                
    		} catch (Exception e) {

    			e.printStackTrace();
    			
    		}
        }else {

			ErrorMessage message = new ErrorMessage("cette role n'existe pas.", new Date(), 400);

			String json = this.objectMapper.writeValueAsString(message);
			
			response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        
	        response.getWriter().write(json);
            
        }
	}
	
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
        UserDto newUser = objectMapper.readValue(request.getReader(), UserDto.class);
        Integer Id = Integer.parseInt(request.getPathInfo().split("/")[2]);
        newUser.setUserId(Id);
        UserDto userDto = userService.updateUser(newUser);
        
        try {
        	
        	String json = objectMapper.writeValueAsString(userDto);
        	response.setContentType("application/json");
        	response.setCharacterEncoding("UTF-8");
            
        	response.getWriter().write(json);
            
		} catch (Exception e) {

			e.printStackTrace();
			
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
		Integer Id = Integer.parseInt(request.getPathInfo().split("/")[2]);
        userService.deleteUser(Id);
        
        if(userService.readUser(Id).getUserId()== null) {

        	ErrorMessage message = new ErrorMessage("delete avec success.", new Date(), 200);

			String json = this.objectMapper.writeValueAsString(message);
			
			response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        
	        response.getWriter().write(json);
	        
        }else {
        	
        	ErrorMessage message = new ErrorMessage("delete failed.", new Date(), 200);

			String json = this.objectMapper.writeValueAsString(message);
			
			response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        
	        response.getWriter().write(json);
	        
        }
		
	}

}
