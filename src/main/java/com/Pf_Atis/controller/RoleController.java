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
import com.Pf_Artis.service.facade.RoleService;
import com.Pf_Artis.service.impl.RoleServiceImpl;
import com.Pf_Artis.shared.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class RoleController
 */
@WebServlet(name="RoleController",urlPatterns = {"/api/roles/*"})
public class RoleController extends HttpServlet {
	
	
	private static final long serialVersionUID = 1L;
	private RoleService roleService;
	private ObjectMapper objectMapper = new ObjectMapper();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RoleController() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public void init() throws ServletException {
    	
    	DaoFactory daoFactory = DaoFactory.getInstance();
    	roleService = new RoleServiceImpl(daoFactory);
    	
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		System.out.println("2-test");

		String path=request.getPathInfo();
		
		if( path == null || path.split("/")[1].equals("*")) {
			
			List<RoleDto> roles = roleService.findAll();
			
			if(!roles.isEmpty()) {
					
				try {
				
					String json = objectMapper.writeValueAsString(roles);
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write(json);
			        
				} catch (Exception e) {
					
					e.printStackTrace();
					
				}
				
			}
			else {

				ErrorMessage message = new ErrorMessage("Aucun role trouvé.", new Date(), 400);

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
				RoleDto roleDto = roleService.findById(Id);
				
				if( roleDto.getRoleId() == null ) {

					ErrorMessage message = new ErrorMessage("Aucun role trouvé.", new Date(), 400);

					String json = this.objectMapper.writeValueAsString(message);
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write(json);
					
				}else {
					
					try {
					
						String json = objectMapper.writeValueAsString( roleDto );
						
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

		RoleDto roleDto = objectMapper.readValue(request.getReader(), RoleDto.class);
		RoleDto saved = roleService.save(roleDto);
		
		try {
        	
        	String json = objectMapper.writeValueAsString(saved);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            response.getWriter().write(json);
            
		} catch (Exception e) {

			e.printStackTrace();
			
		}

	}
	
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		RoleDto roleDto = objectMapper.readValue(request.getReader(), RoleDto.class);
		Integer Id = Integer.parseInt(request.getPathInfo().split("/")[2]);
		RoleDto updated = roleService.update(roleDto, Id);
		
		try {
        	
        	String json = objectMapper.writeValueAsString(updated);
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
		roleService.delete(Id);
		
		if(roleService.findById(Id).getRoleId()==null) {

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
