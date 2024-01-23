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
import com.Pf_Artis.dto.StoreDto;
import com.Pf_Artis.dto.UserDto;
import com.Pf_Artis.service.facade.StoreServiceInterface;
import com.Pf_Artis.service.facade.UserServiceInterface;
import com.Pf_Artis.service.impl.StoreServiceImpl;
import com.Pf_Artis.service.impl.UserServiceImpl;
import com.Pf_Artis.shared.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class StoreController
 */
@WebServlet(name="StoreController",urlPatterns = {"/api/stores/*"})
public class StoreController extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private StoreServiceInterface storeService ;
	private UserServiceInterface userService;
	private ObjectMapper objectMapper = new ObjectMapper();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StoreController() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void init() throws ServletException {
    	
    	DaoFactory daoFactory= DaoFactory.getInstance();
    	storeService = new StoreServiceImpl(daoFactory);
    	userService = new UserServiceImpl(daoFactory);
    	
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String path=request.getPathInfo();
		System.out.println(path);
		
		if( path == null || path.split("/")[1].equals("*")) {
			
			// User user = (User) request.getSession().getAttribute("user");
			// System.out.println(user);
			
			List<StoreDto> stores = storeService.getAllStores();
			
			if(!stores.isEmpty()) {
					
				try {
				
					String jsonStore = objectMapper.writeValueAsString(stores);
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write(jsonStore);
			        
				} catch (Exception e) {
					
					e.printStackTrace();
					
				}
				
			}
			else {

				ErrorMessage message = new ErrorMessage("Aucun Store trouvé.", new Date(), 400);

				String json = this.objectMapper.writeValueAsString(message);
				
				response.setContentType("application/json");
		        response.setCharacterEncoding("UTF-8");
		        
		        response.getWriter().write(json);
				
			}
		}else if(path.split("/")[1].equals("last")) {
			
			StoreDto storeDto = storeService.getLastStores();
			
			if( storeDto.getStoreId() == null ) {

				ErrorMessage message = new ErrorMessage("Aucun Store trouvé.", new Date(), 400);

				String json = this.objectMapper.writeValueAsString(message);
				
				response.setContentType("application/json");
		        response.setCharacterEncoding("UTF-8");
		        
		        response.getWriter().write(json);
				
			}else {
				
				try {
					
					String jsonStore = objectMapper.writeValueAsString(storeDto);
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write(jsonStore);
			        
				} catch (Exception e) {
					
					e.printStackTrace();
					
				}
			}
		}
		else {
			
			String[] pathParts = path.split("/");
			
			if(pathParts[1].equals("id")) {
				
				Integer storeId = Integer.parseInt(pathParts[2]);
				StoreDto storeDto = storeService.readStore(storeId);
				
				if( storeDto.getStoreId() == null ) {

					ErrorMessage message = new ErrorMessage("Aucun Store trouvé.", new Date(), 400);

					String json = this.objectMapper.writeValueAsString(message);
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write(json);
					
				}else {
					
					try {
						
						String jsonStore = objectMapper.writeValueAsString(storeDto);
						
						response.setContentType("application/json");
				        response.setCharacterEncoding("UTF-8");
				        
				        response.getWriter().write(jsonStore);
				        
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
			
		StoreDto storeDto = objectMapper.readValue(request.getReader(), StoreDto.class);
		
		UserDto userDto = userService.readUser(storeDto.getArtisant().getUserId());
		
		if( userDto.getUserId() == null || userDto.getRole().getName().equals("ROLE_CLIENT")) {

			ErrorMessage message = new ErrorMessage("cette artisan n'existe pas.", new Date(), 400);

			String json = this.objectMapper.writeValueAsString(message);
			
			response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        
	        response.getWriter().write(json);
            
		}else {

			storeDto.setArtisant(userDto);
			
			StoreDto saved =  storeService.createStore(storeDto);
			try {
				
				String jsonStore = objectMapper.writeValueAsString(saved);
	            response.setContentType("application/json");
	            response.setCharacterEncoding("UTF-8");
	            
	            response.getWriter().write(jsonStore);
				
			} catch (Exception e) {

				e.printStackTrace();

			}
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		
		Integer storeId = Integer.parseInt(req.getPathInfo().split("/")[2]);
		storeService.deleteStore(storeId);
		
		if(storeService.readStore(storeId).getStoreId()==null) {

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
	
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		StoreDto storeDto = objectMapper.readValue(request.getReader(), StoreDto.class);
		
		Integer storeId = Integer.parseInt(request.getPathInfo().split("/")[2]);

		UserDto userDto = userService.readUser(storeDto.getArtisant().getUserId());
		
		if( userDto.getUserId() == null || userDto.getRole().getName().equals("ROLE_CLIENT")) {

			ErrorMessage message = new ErrorMessage("cette artisan n'existe pas.", new Date(), 400);

			String json = this.objectMapper.writeValueAsString(message);
			
			response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        
	        response.getWriter().write(json);
            
		}else {

			storeDto.setArtisant(userDto);
			storeDto.setStoreId(storeId);
			
			StoreDto updated = storeService.updateStore(storeDto);
			
			try {
				
				String jsonStore = objectMapper.writeValueAsString(updated);
	            response.setContentType("application/json");
	            response.setCharacterEncoding("UTF-8");
	            
	            response.getWriter().write(jsonStore);
				
			} catch (Exception e) {

				e.printStackTrace();

			}
		}
	}

}
