package com.Pf_Atis.web;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import com.Pf_Artis.dao.DaoFactory;
import com.Pf_Artis.dto.ImageDto;
import com.Pf_Artis.models.Image;
import com.Pf_Artis.models.Produit;
import com.Pf_Artis.service.facade.ImageServiceInterface;
import com.Pf_Artis.service.facade.ProduitServiceInterface;
import com.Pf_Artis.service.impl.ImageServiceImpl;
import com.Pf_Artis.service.impl.ProduitServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class ImageController
 */
@WebServlet(name="ImageController",urlPatterns = {"/api/images/*"})
public class ImageController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ImageServiceInterface imageService;
	private ProduitServiceInterface produitService;
       
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
			
			List<Image> images = imageService.getAllImages();
			
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
				
				response.getWriter().write("Aucun image trouvé.");
				
			}
		}
		else {
			
			String[] pathParts = path.split("/");
			
			if(pathParts[1].equals("id")) {
				
				Long Id = Long.parseLong(pathParts[2]);
				Image image = imageService.readImage(Id);
				
				if( image.getId() == null ) {
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write("Aucun image trouvé.");
			        
				}else {
					
					try {
						
						ObjectMapper objectMapper = new ObjectMapper();
						String jsonStore = objectMapper.writeValueAsString(image);
						
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
		ImageDto imageDto = objectMapper.readValue(request.getReader(), ImageDto.class);
		
		Image image = new Image();
		
		Produit produit = produitService.readProduit(imageDto.getProduit_id());
		
		if(produit.getId()==null) {
			
			response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            response.getWriter().write("cette produit n'existe pas");
			
		}else {
			
			image.setPath( imageDto.getPath() );
			image.setProduit(produit);
			
			Image newImage = imageService.createImage(image);
			try {
				
				String jsonStore = objectMapper.writeValueAsString(newImage);
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
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Long Id = Long.parseLong(request.getPathInfo().split("/")[2]);
		imageService.deleteImage(Id);
		
		if(imageService.readImage(Id).getId()==null) {

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
