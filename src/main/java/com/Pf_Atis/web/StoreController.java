package com.Pf_Atis.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import com.Pf_Artis.dao.DaoFactory;
import com.Pf_Artis.dto.StoreDto;
import com.Pf_Artis.models.Store;
import com.Pf_Artis.models.User;
import com.Pf_Artis.service.facade.StoreServiceInterface;
import com.Pf_Artis.service.facade.UserServiceInterface;
import com.Pf_Artis.service.impl.StoreServiceImpl;
import com.Pf_Artis.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class StoreController
 */
@WebServlet(name="StoreController",urlPatterns = {"/api/stores/*"})
public class StoreController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private StoreServiceInterface storeService ;
	private UserServiceInterface userService;
       
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
		
		if( path == null || path.split("/")[1].equals("*")) {
			
			List<Store> stores = storeService.getAllStores();
			
			if(!stores.isEmpty()) {
					
				try {
				
					ObjectMapper objectMapper = new ObjectMapper();
					String jsonStore = objectMapper.writeValueAsString(stores);
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write(jsonStore);
			        
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
				
				Long storeId = Long.parseLong(pathParts[2]);
				Store store = storeService.readStore(storeId);
				
				if( store.getId() == null ) {
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write("Aucun store trouvé.");
			        
				}else {
					
					try {
						
						ObjectMapper objectMapper = new ObjectMapper();
						String jsonStore = objectMapper.writeValueAsString(store);
						
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
			
		ObjectMapper objectMapper = new ObjectMapper();
		StoreDto storeDto = objectMapper.readValue(request.getReader(), StoreDto.class);
		
		Store store = new Store();
		User user = userService.readUser(storeDto.getArtisant_id());
		
		if( user.getId() == null || user.getRole().equals("client")) {
			
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            response.getWriter().write("cette artisan n'existe pas");
            
		}else {

			store.setAdress(storeDto.getAdress());
			store.setNom(storeDto.getNom());
			store.setTelephone(storeDto.getTelephone());
			store.setArtisant(user);
			
			Store newstore =  storeService.createStore(store);
			if(newstore == null ) {
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
		        
				response.getWriter().write("cette user n'est pas artisan.");
			}else {
				try {
					
					String jsonStore = objectMapper.writeValueAsString(newstore);
		            response.setContentType("application/json");
		            response.setCharacterEncoding("UTF-8");
		            
		            response.getWriter().write(jsonStore);
					
				} catch (Exception e) {

					e.printStackTrace();

				}
			}
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		Long storeId = Long.parseLong(req.getPathInfo().split("/")[2]);
		storeService.deleteStore(storeId);
		
		if(storeService.readStore(storeId).getId()==null) {

        	resp.setContentType("application/json");
	        resp.setCharacterEncoding("UTF-8");
	        
	        resp.getWriter().write("delete avec success.");
	        
		}else {

        	resp.setContentType("application/json");
	        resp.setCharacterEncoding("UTF-8");
	        
	        resp.getWriter().write("delete failed.");
	        
		}
		
	}
	
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("test");
		ObjectMapper objectMapper = new ObjectMapper();
		StoreDto storeDto = objectMapper.readValue(request.getReader(), StoreDto.class);
		
		Long storeId = Long.parseLong(request.getPathInfo().split("/")[2]);
		
		Store store = new Store();
		User user = userService.readUser(storeDto.getArtisant_id());
		
		if( user.getId() == null || user.getRole().equals("client")) {
			
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            response.getWriter().write("cette artisan n'existe pas");
            
		}else {
			
			store.setAdress(storeDto.getAdress());
			store.setNom(storeDto.getNom());
			store.setTelephone(storeDto.getTelephone());
			store.setArtisant(user);
			store.setId(storeId);
			
			Store newstore = storeService.updateStore(store);
			
			try {
				
				String jsonStore = objectMapper.writeValueAsString(newstore);
	            response.setContentType("application/json");
	            response.setCharacterEncoding("UTF-8");
	            
	            response.getWriter().write(jsonStore);
				
			} catch (Exception e) {

				e.printStackTrace();

			}
		}
	}

}
