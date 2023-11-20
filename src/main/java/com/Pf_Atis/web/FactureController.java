package com.Pf_Atis.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import com.Pf_Artis.dao.DaoFactory;
import com.Pf_Artis.dto.FactureDto;
import com.Pf_Artis.models.Commande;
import com.Pf_Artis.models.Facture;
import com.Pf_Artis.service.facade.CommandeServiceInterface;
import com.Pf_Artis.service.facade.FactureServiceInterface;
import com.Pf_Artis.service.impl.CommandeServiceImpl;
import com.Pf_Artis.service.impl.FactureServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class FactureController
 */
@WebServlet(name="FactureController",urlPatterns = {"/api/factures/*"})
public class FactureController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private FactureServiceInterface factureService;
	private CommandeServiceInterface commandeService;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FactureController() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public void init() throws ServletException {
    	
    	DaoFactory daoFactory = DaoFactory.getInstance();
    	factureService = new FactureServiceImpl(daoFactory);
    	commandeService = new CommandeServiceImpl(daoFactory);
    	
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String path=request.getPathInfo();
		
		if( path == null || path.split("/")[1].equals("*")) {
			
			List<Facture> factures = factureService.getAllFactures();
			
			if(!factures.isEmpty()) {
					
				try {
				
					ObjectMapper objectMapper = new ObjectMapper();
					String json = objectMapper.writeValueAsString(factures);
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write(json);
			        
				} catch (Exception e) {
					
					e.printStackTrace();
					
				}
				
			}
			else {
				
				response.getWriter().write("Aucun facture trouvé.");
				
			}
		}
		else {
			
			String[] pathParts = path.split("/");
			
			if(pathParts[1].equals("id")) {
				
				Long Id = Long.parseLong(pathParts[2]);
				Facture facture = factureService.readFacture(Id);
				
				if( facture.getId() == null ) {
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write("Aucun facture trouvé.");
			        
				}else {
					
					try {
						
						ObjectMapper objectMapper = new ObjectMapper();
						String json = objectMapper.writeValueAsString(facture);
						
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
        FactureDto factureDto = objectMapper.readValue(request.getReader(), FactureDto.class);
        
        Facture newFacture = new Facture();
        Commande commande = commandeService.readCommande( factureDto.getCommande_id() );
        
        if( commande.getId() == null ) {
        	
        	response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            response.getWriter().write("cette commande n'existe pas");
            
        }else {
        	newFacture.setCommande(commande);
            newFacture.setDateFacturation( factureDto.getDate_facturation() );
            newFacture.setMontantTotal( factureDto.getMontant_total() );
            
            Facture facture = factureService.createFacture(newFacture);
            
            try {
            	
            	String json = objectMapper.writeValueAsString(facture);
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
        FactureDto factureDto = objectMapper.readValue(request.getReader(), FactureDto.class);
        Long Id = Long.parseLong(request.getPathInfo().split("/")[2]);
        
        Facture newFacture = new Facture();
        Commande commande = commandeService.readCommande( factureDto.getCommande_id() );
		
        if( commande.getId() == null ) {
        	
        	response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            response.getWriter().write("cette commande n'existe pas");
            
        }else {
        	newFacture.setId(Id);
            newFacture.setCommande(commande);
            newFacture.setDateFacturation(factureDto.getDate_facturation());
            newFacture.setMontantTotal(factureDto.getMontant_total());
    		
    		Facture facture = factureService.updateFacture(newFacture);
    		
            try {
            	
            	String json = objectMapper.writeValueAsString(facture);
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
		
		Long Id = Long.parseLong(request.getPathInfo().split("/")[2]);
        factureService.deleteFacture(Id);
        
        if(factureService.readFacture(Id).getId()== null) {
        	
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
