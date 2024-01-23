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
import com.Pf_Artis.dto.CommandeDto;
import com.Pf_Artis.dto.LigneCommandeDto;
import com.Pf_Artis.dto.ProduitDto;
import com.Pf_Artis.models.LigneCommandeKey;
import com.Pf_Artis.service.facade.CommandeServiceInterface;
import com.Pf_Artis.service.facade.LigneCommandeServiceInterface;
import com.Pf_Artis.service.facade.ProduitServiceInterface;
import com.Pf_Artis.service.impl.CommandeServiceImpl;
import com.Pf_Artis.service.impl.LigneCommandeServiceImpl;
import com.Pf_Artis.service.impl.ProduitServiceImpl;
import com.Pf_Artis.shared.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class LigneCommandeController
 */
@WebServlet(name="LigneCommandeController",urlPatterns = {"/api/lignecommandes/*"})
public class LigneCommandeController extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private LigneCommandeServiceInterface ligneCommandeService;
	private ProduitServiceInterface produitService ;
	private CommandeServiceInterface commandeService ;
	private ObjectMapper objectMapper = new ObjectMapper();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LigneCommandeController() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public void init() throws ServletException {
    	
    	DaoFactory daoFactory = DaoFactory.getInstance();
    	ligneCommandeService = new LigneCommandeServiceImpl(daoFactory);
    	produitService = new ProduitServiceImpl(daoFactory);
    	commandeService = new CommandeServiceImpl(daoFactory);
    	
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String path=request.getPathInfo();
		
		if( path == null || path.split("/")[1].equals("*")) {
			
			List<LigneCommandeDto> ligneCommandes = ligneCommandeService.getAllLigneCommandes();
			
			if(!ligneCommandes.isEmpty()) {
					
				try {
	
					String json = objectMapper.writeValueAsString(ligneCommandes);
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write(json);
			        
				} catch (Exception e) {
					
					e.printStackTrace();
					
				}
				
			}
			else {

				ErrorMessage message = new ErrorMessage("Aucun ligne Commande trouvé.", new Date(), 400);

				String json = this.objectMapper.writeValueAsString(message);
				
				response.setContentType("application/json");
		        response.setCharacterEncoding("UTF-8");
		        
		        response.getWriter().write(json);
				
			}
		}
		else {
			
			String[] pathParts = path.split("/");
			
			if(pathParts[1].equals("comid") && pathParts[3].equals("prodid") ) {
				
				Integer comId = Integer.parseInt(pathParts[2]);
				Integer prodid = Integer.parseInt(pathParts[4]);
		        
		        LigneCommandeKey key = new LigneCommandeKey();
		        
		        key.setCommandeId(comId);
	        	key.setProduitId(prodid);
		        
				LigneCommandeDto ligneCommandeDto = ligneCommandeService.readLigneCommande(key);
				
				if( ligneCommandeDto.getId() == null ) {

					ErrorMessage message = new ErrorMessage("Aucun ligne Commande trouvé.", new Date(), 400);

					String json = this.objectMapper.writeValueAsString(message);
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write(json);
					
				}else {
					
					try {
						
						String json = objectMapper.writeValueAsString(ligneCommandeDto);
						
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
		
        LigneCommandeDto ligneCommandeDto = objectMapper.readValue(request.getReader(), LigneCommandeDto.class);
        Integer produitId = ligneCommandeDto.getProduit().getProduitId();
        Integer commandeId = ligneCommandeDto.getCommande().getCommandeId();
        
        if( produitService.readProduit( produitId ).getProduitId() == null || 
        		commandeService.readCommande( commandeId ).getCommandeId() == null ) {

			ErrorMessage message = new ErrorMessage("cette commande ou bien produit n'existe pas.", new Date(), 400);

			String json = this.objectMapper.writeValueAsString(message);
			
			response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        
	        response.getWriter().write(json);
        	
        }else {
        	
        	LigneCommandeKey key = new LigneCommandeKey();
        	
        	key.setCommandeId( commandeId );
        	key.setProduitId( produitId );
        	
        	ProduitDto produitDto = produitService.readProduit( produitId );
        	CommandeDto commandeDto = commandeService.readCommande( commandeId );
        	
        	ligneCommandeDto.setCommande(commandeDto);
        	ligneCommandeDto.setProduit(produitDto);
        	ligneCommandeDto.setId(key);
        	
        	LigneCommandeDto saved = ligneCommandeService.createLigneCommande(ligneCommandeDto);
        	
            
            try {
            	
            	String json = objectMapper.writeValueAsString(saved);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                
                response.getWriter().write(json);
                
    		} catch (Exception e) {

    			e.printStackTrace();
    			
    		}
        }
	}
	
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
        LigneCommandeDto ligneCommandeDto = objectMapper.readValue(request.getReader(), LigneCommandeDto.class);
        
        String path = request.getPathInfo();
        
        Integer comId = Integer.parseInt(path.split("/")[2]);
        Integer prodid = Integer.parseInt(path.split("/")[4]);
        
        if( produitService.readProduit( prodid ).getProduitId() == null || commandeService.readCommande( comId ).getCommandeId() == null ) {

			ErrorMessage message = new ErrorMessage("cette commande ou bien produit n'existe pas.", new Date(), 400);

			String json = this.objectMapper.writeValueAsString(message);
			
			response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        
	        response.getWriter().write(json);
        	
        }else {
      
        	LigneCommandeKey key = new LigneCommandeKey();
        	
        	key.setCommandeId(comId);
        	key.setProduitId(prodid);
        	
        	ProduitDto produitDto = produitService.readProduit( prodid );
        	CommandeDto commandeDto = commandeService.readCommande( comId);
        	
        	ligneCommandeDto.setCommande(commandeDto);
        	ligneCommandeDto.setProduit(produitDto);
        	ligneCommandeDto.setId(key);
        	
        	LigneCommandeDto updateLigneCommande = ligneCommandeService.updateLigneCommande(ligneCommandeDto);
        	
        	try {
            	
            	String json = objectMapper.writeValueAsString(updateLigneCommande);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                
                response.getWriter().write(json);
                
    		} catch (Exception e) {

    			e.printStackTrace();
    			
    		}
        	
        }
		
	}
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String path = request.getPathInfo();
        
        Integer comId = Integer.parseInt(path.split("/")[2]);
        Integer prodid = Integer.parseInt(path.split("/")[4]);
        
        if( produitService.readProduit( prodid ).getProduitId() == null || commandeService.readCommande( comId ).getCommandeId() == null ) {

			ErrorMessage message = new ErrorMessage("cette commande ou bien produit n'existe pas.", new Date(), 400);

			String json = this.objectMapper.writeValueAsString(message);
			
			response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        
	        response.getWriter().write(json);
        	
        }else {
        	LigneCommandeKey key = new LigneCommandeKey();
        	
        	key.setCommandeId(comId);
        	key.setProduitId(prodid);
        	
        	ligneCommandeService.deleteLigneCommande(key);
        	
        	if(ligneCommandeService.readLigneCommande(key).getId() == null ) {

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

}
