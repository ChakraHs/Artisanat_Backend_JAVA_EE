package com.Pf_Atis.web;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.Part;
import jakarta.servlet.http.Part;

import java.io.BufferedReader;
import java.io.File;
//import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
//import java.nio.charset.MalformedInputException;
//import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import com.Pf_Artis.dao.DaoFactory;
import com.Pf_Artis.dto.ImageDto;
//import com.Pf_Artis.dto.ImageDto;
import com.Pf_Artis.models.Image;
import com.Pf_Artis.models.Produit;
//import com.Pf_Artis.models.Produit;
import com.Pf_Artis.service.facade.ImageServiceInterface;
import com.Pf_Artis.service.facade.ProduitServiceInterface;
import com.Pf_Artis.service.impl.ImageServiceImpl;
import com.Pf_Artis.service.impl.ProduitServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class ImageController
 */
@WebServlet("/api/ImageTest/*")
@MultipartConfig(
//		location = "http://localhost:8080/Pf_Artis/assets/images",
		fileSizeThreshold = 1024 * 1024, //1 MB
		maxFileSize = 1024 * 1024 * 10, //10 MB
		maxRequestSize = 1024 * 1024 * 11 //11 MB
		)
public class ImageTest extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String SAVE_DIRECTORY = "assets/images";
	private ImageServiceInterface imageService;
	private ProduitServiceInterface produitService;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ImageTest() {
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
		
		if( path == null || path.split("/").length == 0) {
			
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
		
		request.setCharacterEncoding("UTF-8");
		
		String description = request.getParameter("description");
		
		System.out.println("description : "+ description);
		
        
                                    
     // Handle image file upload
        Collection<Part> parts = request.getParts();

        ImageDto imageDto = new ImageDto();

        for (Part part : parts) {
            if (part.getName().equals("image")) {
            	// This part is the image file
                InputStream inputStream = part.getInputStream();
                // Process the image data as needed
                // ...

                // Obtain the name of the uploaded file
                // Generate a unique filename
                String fileName = generateUniqueFileName(part);
                
                
                String savePath = getUploadPath(request);
                System.out.println("savePath: "+savePath);
                
                // Ensure the directory exists
                File directory = new File(savePath);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Save the image file
                Path imagePath = Path.of(savePath, fileName);
                Files.copy(inputStream, imagePath, StandardCopyOption.REPLACE_EXISTING);
                
                imageDto.setPath(SAVE_DIRECTORY + '/'+ fileName);
                
                
                

            } else if (part.getName().equals("produit_id")) {
                // Assuming you have a 'produit_id' field in your form
                // Retrieve the 'produit_id' value
                BufferedReader reader = new BufferedReader(new InputStreamReader(part.getInputStream()));
                String produitId = reader.readLine();
                imageDto.setProduit_id(Long.parseLong(produitId));
                System.out.println(produitId);
            }
        }
        
        ObjectMapper objectMapper = new ObjectMapper();

	    Image image = new Image();
	    Produit produit = produitService.readProduit(imageDto.getProduit_id());
	    
	    image.setPath(imageDto.getPath());
	    image.setProduit(produit);
	    

	    if (produit.getId() == null) {
	        // Handle the case where the associated product doesn't exist
	        response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        response.getWriter().write("cette produit n'existe pas :"+imageDto.getProduit_id());
	    } else {
	        // Call the modified createImage method with the InputStream and original file name
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
	
	private String generateUniqueFileName(Part part) {
        String originalFileName = getFileName(part);
        String extension = "";
        int lastDotIndex = originalFileName.lastIndexOf(".");
        if (lastDotIndex > 0) {
            extension = originalFileName.substring(lastDotIndex);
        }
        // Generate a unique filename using UUID
        return UUID.randomUUID().toString() + extension;
    }
	
	
	private String getFileName(Part part) {
        // Get the content-disposition header to extract the file name
        String contentDisposition = part.getHeader("content-disposition");
        String[] elements = contentDisposition.split(";");
        for (String element : elements) {
            if (element.trim().startsWith("filename")) {
                // Extract and return the file name
                return element.substring(element.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return "";
    }
	
	
	
	private String getUploadPath(HttpServletRequest request) {
        String applicationPath = request.getServletContext().getRealPath("");
        return applicationPath + File.separator + SAVE_DIRECTORY;
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
