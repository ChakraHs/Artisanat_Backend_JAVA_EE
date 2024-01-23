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
import com.Pf_Artis.dto.FactureDto;
import com.Pf_Artis.service.facade.CommandeServiceInterface;
import com.Pf_Artis.service.facade.FactureServiceInterface;
import com.Pf_Artis.service.impl.CommandeServiceImpl;
import com.Pf_Artis.service.impl.FactureServiceImpl;
import com.Pf_Artis.shared.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class FactureController
 */
@WebServlet(name="FactureController",urlPatterns = {"/api/factures/*"})
public class FactureController extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private FactureServiceInterface factureService;
	private CommandeServiceInterface commandeService;
	private ObjectMapper objectMapper = new ObjectMapper();
       
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
			
			List<FactureDto> factures = factureService.getAllFactures();
			
			if(!factures.isEmpty()) {
					
				try {
					
					String json = objectMapper.writeValueAsString(factures);
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write(json);
			        
				} catch (Exception e) {
					
					e.printStackTrace();
					
				}
			}
			else {

				ErrorMessage message = new ErrorMessage("Aucun facture trouvé.", new Date(), 400);

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
				FactureDto factureDto = factureService.readFacture(Id);
				
				if( factureDto.getFactureId() == null ) {

					ErrorMessage message = new ErrorMessage("Aucun facture trouvé.", new Date(), 400);

					String json = this.objectMapper.writeValueAsString(message);
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write(json);
					
				}else {
					
					try {
						
						String json = objectMapper.writeValueAsString(factureDto);
						
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

        FactureDto factureDto = objectMapper.readValue(request.getReader(), FactureDto.class);
        
        CommandeDto commandeDto = commandeService.readCommande( factureDto.getCommande().getCommandeId() );
        
        if( commandeDto.getCommandeId() == null ) {

			ErrorMessage message = new ErrorMessage("cette commande n'existe pas", new Date(), 400);

			String json = this.objectMapper.writeValueAsString(message);
			
			response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        
	        response.getWriter().write(json);
	        
        }else {
        	factureDto.setCommande(commandeDto);
            
            FactureDto saved = factureService.createFacture(factureDto);
            
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
		
        FactureDto factureDto = objectMapper.readValue(request.getReader(), FactureDto.class);
        Integer Id = Integer.parseInt(request.getPathInfo().split("/")[2]);
        
        CommandeDto commandeDto = commandeService.readCommande( factureDto.getCommande().getCommandeId() );
        
        if( commandeDto.getCommandeId() == null ) {

			ErrorMessage message = new ErrorMessage("cette commande n'existe pas", new Date(), 400);

			String json = this.objectMapper.writeValueAsString(message);
			
			response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        
	        response.getWriter().write(json);
	        
        }else {
        	factureDto.setFactureId(Id);
        	factureDto.setCommande(commandeDto);
    		
    		FactureDto updated = factureService.updateFacture(factureDto);
    		
            try {
            	
            	String json = objectMapper.writeValueAsString(updated);
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
		
		Integer Id = Integer.parseInt(request.getPathInfo().split("/")[2]);
        factureService.deleteFacture(Id);
        
        if(factureService.readFacture(Id).getFactureId()== null) {

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
