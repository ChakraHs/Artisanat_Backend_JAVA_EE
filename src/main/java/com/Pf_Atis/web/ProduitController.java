package com.Pf_Atis.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import com.Pf_Artis.dao.DaoFactory;
import com.Pf_Artis.dto.ProduitDto;
import com.Pf_Artis.models.Produit;
import com.Pf_Artis.models.Store;
import com.Pf_Artis.service.facade.ProduitServiceInterface;
import com.Pf_Artis.service.facade.StoreServiceInterface;
import com.Pf_Artis.service.impl.ProduitServiceImpl;
import com.Pf_Artis.service.impl.StoreServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class ProduitController
 */
@WebServlet(name="ProduitController",urlPatterns = {"/api/produits/*"})
public class ProduitController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ProduitServiceInterface produitService;
	private StoreServiceInterface storeService;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProduitController() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void init() throws ServletException {
    	
    	DaoFactory daoFactory = DaoFactory.getInstance();
    	produitService = new ProduitServiceImpl(daoFactory);
    	storeService = new StoreServiceImpl(daoFactory);
    	
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		ObjectMapper objectMapper = new ObjectMapper();
		String path=request.getPathInfo();
		
		if( path == null || path.split("/")[1].equals("*")) {
			
			List<Produit> produits = produitService.getAllProduits();
			
			if(!produits.isEmpty()) {
					
				try {
				
					String json = objectMapper.writeValueAsString(produits);
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write(json);
			        
				} catch (Exception e) {
					
					e.printStackTrace();
					
				}
				
			}
			else {
				
				response.getWriter().write("Aucun produit trouvé.");
				
			}
		}
		else {
			
			String[] pathParts = path.split("/");
			
			if(pathParts[1].equals("id")) {
				
				Long Id = Long.parseLong(pathParts[2]);
				Produit produit = produitService.readProduit(Id);
				
				if( produit.getId() == null ) {
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write("Aucun produit trouvé.");
			        
				}else {
					
					try {
						
						String json = objectMapper.writeValueAsString(produit);
						
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
		ProduitDto produitDto = objectMapper.readValue(request.getReader(), ProduitDto.class);
		
		Produit newProduit = new Produit();
		Store store = storeService.readStore(produitDto.getStore_id());
		
		if(store.getId() == null ) {
			
			response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            response.getWriter().write("cette store n'existe pas");
            
		}else {
			newProduit.setStore(store);
			newProduit.setDateFabrication(produitDto.getDate_fabrication());
			newProduit.setDatePeremption(produitDto.getDate_peremption());
			newProduit.setDescription( produitDto.getDescription() );
			newProduit.setNom( produitDto.getNom() );
			newProduit.setPoids(produitDto.getPoids());
			newProduit.setPrix(produitDto.getPrix());
			newProduit.setStock(produitDto.getStock());
			
			
			Produit produit = produitService.createProduit(newProduit);
			
			try {
	        	
	        	String json = objectMapper.writeValueAsString(produit);
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

		ObjectMapper objectMapper = new ObjectMapper();
        ProduitDto produitDto = objectMapper.readValue(request.getReader(), ProduitDto.class);
        Long Id = Long.parseLong(request.getPathInfo().split("/")[2]);
        if( produitService.readProduit(Id).getId() == null ) {

        	response.setContentType("application/json");
        	response.setCharacterEncoding("UTF-8");
	        
        	response.getWriter().write("cette produit n'existe pas.");
	        
        }else {
        	Produit newProduit = new Produit();
            Store store = storeService.readStore(produitDto.getStore_id());
            
            if(store.getId() == null ) {
    			
    			response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                
                response.getWriter().write("cette store n'existe pas");
                
    		}else {
    			newProduit.setId(Id);
                newProduit.setStore(store);
        		newProduit.setDateFabrication(produitDto.getDate_fabrication());
        		newProduit.setDatePeremption(produitDto.getDate_peremption());
        		newProduit.setDescription( produitDto.getDescription() );
        		newProduit.setNom( produitDto.getNom() );
        		newProduit.setPoids(produitDto.getPoids());
        		newProduit.setPrix(produitDto.getPrix());
        		newProduit.setStock(produitDto.getStock());
        		
        		Produit produit = produitService.updateProduit(newProduit);
        		
                try {
                	
                	String json = objectMapper.writeValueAsString(produit);
                	response.setContentType("application/json");
                	response.setCharacterEncoding("UTF-8");
                    
                	response.getWriter().write(json);
                    
        		} catch (Exception e) {
        			e.printStackTrace();
        		}
    		}
        }		
	}
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
        Long Id = Long.parseLong(request.getPathInfo().split("/")[2]);
        produitService.deleteProduit(Id);
        
        if(produitService.readProduit(Id).getId()== null) {
        	
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
