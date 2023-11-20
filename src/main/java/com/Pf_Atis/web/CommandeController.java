package com.Pf_Atis.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import com.Pf_Artis.dao.DaoFactory;
import com.Pf_Artis.dto.CommandeDto;
import com.Pf_Artis.models.Commande;
import com.Pf_Artis.models.User;
import com.Pf_Artis.service.facade.CommandeServiceInterface;
import com.Pf_Artis.service.facade.UserServiceInterface;
import com.Pf_Artis.service.impl.CommandeServiceImpl;
import com.Pf_Artis.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class CommandeController
 */
@WebServlet(name="CommandeController",urlPatterns = {"/api/commandes/*"})
public class CommandeController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private CommandeServiceInterface commandeService;
	private UserServiceInterface userService;
       
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
		
		ObjectMapper objectMapper = new ObjectMapper();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		objectMapper.setDateFormat(dateFormat);
		String path=request.getPathInfo();
		
		if( path == null || path.split("/")[1].equals("*")) {
			
			List<Commande> commandes = commandeService.getAllCommandes();
			
			if(!commandes.isEmpty()) {
					
				try {
				
					String json = objectMapper.writeValueAsString(commandes);
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write(json);
			        
				} catch (Exception e) {
					
					e.printStackTrace();
					
				}
				
			}
			else {
				
				response.getWriter().write("Aucun commande trouvé.");
				
			}
		}
		else {
			
			String[] pathParts = path.split("/");
			
			if(pathParts[1].equals("id")) {
				
				Long Id = Long.parseLong(pathParts[2]);
				Commande commande = commandeService.readCommande(Id);
				
				if( commande.getId() == null ) {
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write("Aucun commande trouvé.");
			        
				}else {
					
					try {
						
						String json = objectMapper.writeValueAsString(commande);
						
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
		CommandeDto commandeDto = objectMapper.readValue(request.getReader(), CommandeDto.class);
		
		Commande newCommande = new Commande();
		User user = userService.readUser(commandeDto.getClient_id());
		if( user.getId() == null || user.getRole().equals("artisan")) {
			
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            response.getWriter().write("cette client n'existe pas");
            
		}else {
			newCommande.setClient(user);
			newCommande.setDateCommande(commandeDto.getDate_commande());
			
			Commande commande = commandeService.createCommande(newCommande);
			
			try {
	        	
	        	String json = objectMapper.writeValueAsString(commande);
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
		
		ObjectMapper objectMapper = new ObjectMapper();
        CommandeDto commandeDto = objectMapper.readValue(request.getReader(), CommandeDto.class);
        Long Id = Long.parseLong(request.getPathInfo().split("/")[2]);
        
        Commande newCommande = new Commande();
		User user = userService.readUser(commandeDto.getClient_id());
		
		if( user.getId() == null || user.getRole().equals("artisan")) {
			
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            response.getWriter().write("cette client n'existe pas");
            
		}else {
			newCommande.setId(Id);
			newCommande.setClient(user);
			newCommande.setDateCommande(commandeDto.getDate_commande());
			
			Commande commande = commandeService.updateCommande(newCommande);
			
	        try {
	        	
	        	String json = objectMapper.writeValueAsString(commande);
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
		
        Long Id = Long.parseLong(request.getPathInfo().split("/")[2]);
        commandeService.deleteCommande(Id);
        
        if(commandeService.readCommande(Id).getId()== null) {
        	
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
