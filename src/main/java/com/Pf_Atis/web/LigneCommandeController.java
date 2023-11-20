package com.Pf_Atis.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import com.Pf_Artis.dao.DaoFactory;
import com.Pf_Artis.dto.LigneCommandeDto;
import com.Pf_Artis.models.Commande;
import com.Pf_Artis.models.LigneCommande;
import com.Pf_Artis.models.LigneCommandeKey;
import com.Pf_Artis.models.Produit;
import com.Pf_Artis.service.facade.CommandeServiceInterface;
import com.Pf_Artis.service.facade.LigneCommandeServiceInterface;
import com.Pf_Artis.service.facade.ProduitServiceInterface;
import com.Pf_Artis.service.impl.CommandeServiceImpl;
import com.Pf_Artis.service.impl.LigneCommandeServiceImpl;
import com.Pf_Artis.service.impl.ProduitServiceImpl;
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
			
			List<LigneCommande> ligneCommandes = ligneCommandeService.getAllLigneCommandes();
			
			if(!ligneCommandes.isEmpty()) {
					
				try {
				
					ObjectMapper objectMapper = new ObjectMapper();
					String json = objectMapper.writeValueAsString(ligneCommandes);
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write(json);
			        
				} catch (Exception e) {
					
					e.printStackTrace();
					
				}
				
			}
			else {
				
				response.getWriter().write("Aucun ligne Commande trouvé.");
				
			}
		}
		else {
			
			String[] pathParts = path.split("/");
			
			if(pathParts[1].equals("comid") && pathParts[3].equals("prodid") ) {
				
				Long comId = Long.parseLong(pathParts[2]);
		        Long prodid = Long.parseLong(pathParts[4]);
		        
		        LigneCommandeKey key = new LigneCommandeKey();
		        
		        key.setCommandeId(comId);
	        	key.setProduitId(prodid);
		        
				LigneCommande ligneCommande = ligneCommandeService.readLigneCommande(key);
				
				if( ligneCommande.getId() == null ) {
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write("Aucun ligne Commande trouvé.");
			        
				}else {
					
					try {
						
						ObjectMapper objectMapper = new ObjectMapper();
						String json = objectMapper.writeValueAsString(ligneCommande);
						
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
		System.out.println("test");
		ObjectMapper objectMapper = new ObjectMapper();
        LigneCommandeDto ligneCommandeCreateDto = objectMapper.readValue(request.getReader(), LigneCommandeDto.class);
        System.out.println(produitService.readProduit( ligneCommandeCreateDto.getProduit_id() ));
        System.out.println(commandeService.readCommande( ligneCommandeCreateDto.getCommande_id() ));
        if( produitService.readProduit( ligneCommandeCreateDto.getProduit_id() ).getId() == null || commandeService.readCommande( ligneCommandeCreateDto.getCommande_id() ).getId() == null ) {
        	
        	response.setContentType("application/json");
        	response.setCharacterEncoding("UTF-8");
	        
        	response.getWriter().write("cette commande ou bien produit n'existe pas.");
	        
        	
        }else {
        	LigneCommande newligneCommande = new LigneCommande();
        	LigneCommandeKey key = new LigneCommandeKey();
        	
        	key.setCommandeId(ligneCommandeCreateDto.getCommande_id());
        	key.setProduitId( ligneCommandeCreateDto.getProduit_id() );
        	
        	Produit produit = produitService.readProduit( ligneCommandeCreateDto.getProduit_id() );
        	Commande commande = commandeService.readCommande( ligneCommandeCreateDto.getCommande_id());
        	
        	newligneCommande.setCommande(commande);
        	newligneCommande.setProduit(produit);
        	newligneCommande.setId(key);
        	newligneCommande.setPrixUnitaire(ligneCommandeCreateDto.getPrixUnitaire());
        	newligneCommande.setQuantite(ligneCommandeCreateDto.getQuantite());
        	
        	LigneCommande ligneCommande = ligneCommandeService.createLigneCommande(newligneCommande);
        	
            
            try {
            	
            	String json = objectMapper.writeValueAsString(ligneCommande);
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
        LigneCommandeDto ligneCommandeDto = objectMapper.readValue(request.getReader(), LigneCommandeDto.class);
        
        String path = request.getPathInfo();
        
        Long comId = Long.parseLong(path.split("/")[2]);
        Long prodid = Long.parseLong(path.split("/")[4]);
        
        if( produitService.readProduit( prodid ).getId() == null || commandeService.readCommande( comId ).getId() == null ) {
        	
        	response.setContentType("application/json");
        	response.setCharacterEncoding("UTF-8");
	        
        	response.getWriter().write("cette commande ou bien produit n'existe pas.");
	        
        	
        }else {
        	
        	LigneCommande ligneCommande = new LigneCommande();
        	LigneCommandeKey key = new LigneCommandeKey();
        	
        	key.setCommandeId(comId);
        	key.setProduitId(prodid);
        	
        	Produit produit = produitService.readProduit( prodid );
        	Commande commande = commandeService.readCommande( comId);
        	
        	ligneCommande.setCommande(commande);
        	ligneCommande.setProduit(produit);
        	ligneCommande.setId(key);
        	ligneCommande.setPrixUnitaire(ligneCommandeDto.getPrixUnitaire());
        	ligneCommande.setQuantite(ligneCommandeDto.getQuantite());
        	
        	LigneCommande updateLigneCommande = ligneCommandeService.updateLigneCommande(ligneCommande);
        	
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
        
        Long comId = Long.parseLong(path.split("/")[2]);
        Long prodid = Long.parseLong(path.split("/")[4]);
        
        if( produitService.readProduit( prodid ).getId() == null || commandeService.readCommande( comId ).getId() == null ) {
        	
        	response.setContentType("application/json");
        	response.setCharacterEncoding("UTF-8");
	        
        	response.getWriter().write("cette commande ou bien produit n'existe pas.");
        	
        }else {
        	LigneCommandeKey key = new LigneCommandeKey();
        	
        	key.setCommandeId(comId);
        	key.setProduitId(prodid);
        	
        	ligneCommandeService.deleteLigneCommande(key);
        	
        	System.out.println(ligneCommandeService.readLigneCommande(key));
        	if(ligneCommandeService.readLigneCommande(key).getId() == null ) {
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

}
