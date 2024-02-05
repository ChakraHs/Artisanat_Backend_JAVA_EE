package com.Pf_Atis.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.Pf_Artis.dao.DaoFactory;
import com.Pf_Artis.dto.CommandeByArtisanDto;
import com.Pf_Artis.dto.CommandeDto;
import com.Pf_Artis.dto.ProduitByDateDto;
import com.Pf_Artis.dto.UserDto;
import com.Pf_Artis.service.facade.CommandeServiceInterface;
import com.Pf_Artis.service.facade.UserServiceInterface;
import com.Pf_Artis.service.impl.CommandeServiceImpl;
import com.Pf_Artis.service.impl.UserServiceImpl;
import com.Pf_Artis.shared.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class CommandeController
 */
@WebServlet(name="CommandeController",urlPatterns = {"/api/commandes/*"})
public class CommandeController extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private CommandeServiceInterface commandeService;
	private UserServiceInterface userService;
	private ObjectMapper objectMapper = new ObjectMapper();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CommandeController() {
        super();
        // TODO Auto-generated constructor stub
    }
    @Override
    public void init() throws ServletException {
    	
    	DaoFactory daoFactory = DaoFactory.getInstance();
    	commandeService = new CommandeServiceImpl(daoFactory);
    	userService = new UserServiceImpl(daoFactory);
    	
    	
    }
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String authorizationHeader = request.getHeader("Authorization");
		
		this.objectMapper.setDateFormat(dateFormat);
		String path=request.getPathInfo();
		
		if( path == null || path.split("/")[1].equals("*")) {
			
			List<CommandeDto> commandes = commandeService.getAllCommandes();
			
			if(!commandes.isEmpty()) {
					
				try {
				
					String json = this.objectMapper.writeValueAsString(commandes);
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write(json);
			        
				} catch (Exception e) {
					
					e.printStackTrace();
					
				}
				
			}
			else {
				ErrorMessage message = new ErrorMessage("Aucun commande trouvé.", new Date(), 400);

				String json = this.objectMapper.writeValueAsString(message);
				
				response.setContentType("application/json");
		        response.setCharacterEncoding("UTF-8");
		        
		        response.getWriter().write(json);
				
			}
		}else if(path.split("/")[1].equals("produitByDate") ) {
			
			System.out.println("bien entréeeeeeeeeeeeeeeeeeeeeeeeeeeeeee produitByDate");
			
			if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
	            // Extraire le jeton d'autorisation (enlever le préfixe "Bearer ")
//		            String token = authorizationHeader.substring(7);

	            // Utiliser le jeton comme nécessaire
//		            System.out.println("Bearer Token: " + token);
	            
	            HttpSession session = request.getSession();
	            Integer userId = (Integer) session.getAttribute("userId");
//		            System.out.println(userId);
	            
	            if(userId!=null) {
	            	
	            	List<ProduitByDateDto> dtos = commandeService.getNbreProduitAchatByArtisan(userId);
	            	System.out.println(" tesssssssssssssssssssssssssssssssssssssssssssstttttttttttttttttt dtos :  "+dtos);
	            	String json = this.objectMapper.writeValueAsString( dtos );
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write(json);
	            	
	            	
	            }else {
	            	
	            	ErrorMessage message = new ErrorMessage("token is not valid.", new Date(), 400);
	            	
					String json = this.objectMapper.writeValueAsString(message);
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write(json);
	            }
			} else {
	            // Aucun en-tête d'autorisation ou format incorrect
	            response.getWriter().write("Aucun jeton d'autorisation trouvé");
	        }
		}else if(path.split("/")[1].equals("ByArtisan") ) {
			
			System.out.println("bien entréeeeeeeeeeeeeeeeeeeeeeeeeeeeeee produitByDate");
			
			if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
	            // Extraire le jeton d'autorisation (enlever le préfixe "Bearer ")
//		            String token = authorizationHeader.substring(7);

	            // Utiliser le jeton comme nécessaire
//		            System.out.println("Bearer Token: " + token);
	            
	            HttpSession session = request.getSession();
	            Integer userId = (Integer) session.getAttribute("userId");
//		            System.out.println(userId);
	            
	            if(userId!=null) {
	            	
	            	List<CommandeByArtisanDto> dtos = commandeService.getCommandeByArtisan(userId);
	            	System.out.println(" tesssssssssssssssssssssssssssssssssssssssssssstttttttttttttttttt dtos :  "+dtos);
	            	String json = this.objectMapper.writeValueAsString( dtos );
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write(json);
	            	
	            	
	            }else {
	            	
	            	ErrorMessage message = new ErrorMessage("token is not valid.", new Date(), 400);
	            	
					String json = this.objectMapper.writeValueAsString(message);
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write(json);
	            }
			} else {
	            // Aucun en-tête d'autorisation ou format incorrect
	            response.getWriter().write("Aucun jeton d'autorisation trouvé");
	        }
		}else {
			
			String[] pathParts = path.split("/");
			
			if(pathParts[1].equals("id")) {
				
				Integer Id = Integer.parseInt(pathParts[2]);
				CommandeDto commandeDto = commandeService.readCommande(Id);
				
				if( commandeDto.getCommandeId() == null ) {
					
					ErrorMessage message = new ErrorMessage("Aucun commande trouvé.", new Date(), 400);

					String json = this.objectMapper.writeValueAsString(message);
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write(json);
			        
				}else {
					
					try {
						
						String json = this.objectMapper.writeValueAsString(commandeDto);
						
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

		CommandeDto commandeDto = this.objectMapper.readValue(request.getReader(), CommandeDto.class);

		
		UserDto userDto = userService.readUser(commandeDto.getUser().getUserId());
		if( userDto.getUserId() == null || userDto.getRole().getName().equals("ROLE_ARTISAN")) {
			
			ErrorMessage message = new ErrorMessage("cette client n'existe pas.", new Date(), 400);

			String json = this.objectMapper.writeValueAsString(message);
			
			response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        
	        response.getWriter().write(json);
            
		}else {
			
			commandeDto.setUser(userDto);
			CommandeDto saved = commandeService.createCommande(commandeDto);
			
			try {
	        	
	        	String json = this.objectMapper.writeValueAsString(saved);
	            response.setContentType("application/json");
	            response.setCharacterEncoding("UTF-8");
	            
	            response.getWriter().write(json);
	            
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
		CommandeDto commandeDto = this.objectMapper.readValue(request.getReader(), CommandeDto.class);
        Integer Id = Integer.parseInt(request.getPathInfo().split("/")[2]);
        
		UserDto userDto = userService.readUser(commandeDto.getUser().getUserId());
		
		if( userDto.getUserId() == null || userDto.getRole().getName().equals("ROLE_ARTISAN")) {

			ErrorMessage message = new ErrorMessage("cette client n'existe pas.", new Date(), 404);

			String json = this.objectMapper.writeValueAsString(message);
			
			response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        
	        response.getWriter().write(json);
            
		}else {
			commandeDto.setCommandeId(Id);
			commandeDto.setUser(userDto);
			
			CommandeDto updated = commandeService.updateCommande(commandeDto);
			
	        try {
	        	
	        	String json = this.objectMapper.writeValueAsString(updated);
	        	response.setContentType("application/json");
	        	response.setCharacterEncoding("UTF-8");
	            
	        	response.getWriter().write(json);
	            
			} catch (Exception e) {

				e.printStackTrace();
				
			}
		}
		
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Integer Id = Integer.parseInt(request.getPathInfo().split("/")[2]);
        commandeService.deleteCommande(Id);
        
        if(commandeService.readCommande(Id).getCommandeId()== null) {

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
