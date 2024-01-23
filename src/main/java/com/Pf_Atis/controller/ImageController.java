package com.Pf_Atis.controller;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.Pf_Artis.dao.DaoFactory;
import com.Pf_Artis.dto.ImageDto;
import com.Pf_Artis.dto.ProduitDto;
import com.Pf_Artis.service.facade.ImageServiceInterface;
import com.Pf_Artis.service.facade.ProduitServiceInterface;
import com.Pf_Artis.service.impl.ImageServiceImpl;
import com.Pf_Artis.service.impl.ProduitServiceImpl;
import com.Pf_Artis.shared.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class ImageController
 */
@WebServlet(name="ImageController",urlPatterns = {"/api/images/*"})
public class ImageController extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private ImageServiceInterface imageService;
	private ProduitServiceInterface produitService;
	private ObjectMapper objectMapper = new ObjectMapper();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ImageController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		
		DaoFactory daoFactory = DaoFactory.getInstance();
		imageService = new ImageServiceImpl(daoFactory);
		produitService = new ProduitServiceImpl(daoFactory);
		
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String path=request.getPathInfo();
		
		if( path == null || path.split("/")[1].equals("*")) {
			
			List<ImageDto> images = imageService.getAllImages();
			
			if(!images.isEmpty()) {
					
				try {
				
					ObjectMapper objectMapper = new ObjectMapper();
					String jsonStore = objectMapper.writeValueAsString(images);
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write(jsonStore);
			        
				} catch (Exception e) {
					
					e.printStackTrace();
					
				}
				
			}
			else {

				ErrorMessage message = new ErrorMessage("Aucun image trouvé.", new Date(), 400);

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
				ImageDto imageDto = imageService.readImage(Id);
				
				if( imageDto.getImageId() == null ) {

					ErrorMessage message = new ErrorMessage("Aucun image trouvé.", new Date(), 400);

					String json = this.objectMapper.writeValueAsString(message);
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write(json);
					
				}else {
					
					try {
						
						String jsonStore = objectMapper.writeValueAsString(imageDto);
						
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
		
		ImageDto imageDto = objectMapper.readValue(request.getReader(), ImageDto.class);
		
		ProduitDto produitdto = produitService.readProduit(imageDto.getProduit().getProduitId());
		
		if(produitdto.getProduitId()==null) {

			ErrorMessage message = new ErrorMessage("cette produit n'existe pas.", new Date(), 400);

			String json = this.objectMapper.writeValueAsString(message);
			
			response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        
	        response.getWriter().write(json);
			
		}else {
			
			imageDto.setProduit(produitdto);
			
			ImageDto saved = imageService.createImage(imageDto);
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

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		ImageDto imageDto = objectMapper.readValue(request.getReader(), ImageDto.class);
        Integer id = Integer.parseInt(request.getPathInfo().split("/")[2]);
        
        if( imageService.readImage(id).getImageId() == null ) {

        	ErrorMessage message = new ErrorMessage("cette image n'existe pas", new Date(), 400);

			String json = this.objectMapper.writeValueAsString(message);
			
			response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        
	        response.getWriter().write(json);
	        
        }else {
        	
            ProduitDto produitDto = produitService.readProduit(imageDto.getProduit().getProduitId());
            
            if(produitDto.getProduitId() == null ) {
            	ErrorMessage message = new ErrorMessage("cette produit n'existe pas", new Date(), 400);

    			String json = this.objectMapper.writeValueAsString(message);
    			
    			response.setContentType("application/json");
    	        response.setCharacterEncoding("UTF-8");
    	        
    	        response.getWriter().write(json);
    		}else {
    			imageDto.setImageId(id);
    			imageDto.setProduit(produitDto);
        		
        		ImageDto updated = imageService.updateImage(imageDto);
        		
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
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Integer Id = Integer.parseInt(request.getPathInfo().split("/")[2]);
		imageService.deleteImage(Id);
		
		if(imageService.readImage(Id).getImageId()==null) {

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
