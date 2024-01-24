package com.Pf_Atis.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.Pf_Artis.dao.DaoFactory;
import com.Pf_Artis.dto.CategoryDto;
import com.Pf_Artis.dto.ImageDto;
import com.Pf_Artis.dto.LigneCommandeDto;
import com.Pf_Artis.dto.ProduitDto;
import com.Pf_Artis.dto.StoreDto;
import com.Pf_Artis.service.facade.CategoryServiceInterface;
import com.Pf_Artis.service.facade.ImageServiceInterface;
import com.Pf_Artis.service.facade.LigneCommandeServiceInterface;
import com.Pf_Artis.service.facade.ProduitServiceInterface;
import com.Pf_Artis.service.facade.StoreServiceInterface;
import com.Pf_Artis.service.impl.CategoryServiceImpl;
import com.Pf_Artis.service.impl.ImageServiceImpl;
import com.Pf_Artis.service.impl.LigneCommandeServiceImpl;
import com.Pf_Artis.service.impl.ProduitServiceImpl;
import com.Pf_Artis.service.impl.StoreServiceImpl;
import com.Pf_Artis.shared.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class ProduitController
 */
@WebServlet(name="ProduitController",urlPatterns = {"/api/produits/*"})
@MultipartConfig
public class ProduitController extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static final String SAVE_DIRECTORY = "assets\\images\\produits";
	private ProduitServiceInterface produitService;
	private StoreServiceInterface storeService;
	private ImageServiceInterface imageService;
	private LigneCommandeServiceInterface ligneCommandeService ;
	private CategoryServiceInterface categoryService;
	private ObjectMapper objectMapper = new ObjectMapper();
	
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
    	imageService = new ImageServiceImpl(daoFactory);
    	ligneCommandeService = new LigneCommandeServiceImpl(daoFactory);
    	categoryService = new CategoryServiceImpl(daoFactory);
    	
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String path=request.getPathInfo();
		
		if( path == null || path.split("/")[1].equals("*")) {
			
			List<ProduitDto> produits = produitService.getAllProduits();
			
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

				ErrorMessage message = new ErrorMessage("Aucun produit trouvé.", new Date(), 400);

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
				ProduitDto produitDto = produitService.readProduit(Id);
				
				if( produitDto.getProduitId() == null ) {

					ErrorMessage message = new ErrorMessage("Aucun produit trouvé.", new Date(), 400);

					String json = this.objectMapper.writeValueAsString(message);
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write(json);
					
				}else {
					
					try {
						
						String json = objectMapper.writeValueAsString(produitDto);
						
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

		// Définit le jeu de caractères pour l'encodage des données de la requête
	    request.setCharacterEncoding("UTF-8");
	    Integer storeId = Integer.parseInt(request.getParameter("storeId"));
	    StoreDto storeDto = storeService.readStore(storeId);
	    
	    String categoresId = request.getParameter("categoryId");
	    boolean categoryExiste = true;
	    List<CategoryDto> categories = new ArrayList<CategoryDto>();
	    String[] numbers = categoresId.split(",");
	    
	    // Parcourir le tableau pour obtenir chaque nombre
        for (String number : numbers) {
        	
    	    Integer categoryId = Integer.parseInt(number);
    	    if(!categoryService.categoryExiste(categoryId)) {
    	    	categoryExiste = false;
    	    	break;
    	    }
    	    
    	    CategoryDto categoryDto = categoryService.readCategory(categoryId);
    	    categories.add(categoryDto);
    	    
        }
		System.out.println(categories);
		if( storeDto.getStoreId() == null || !categoryExiste ) {

			ErrorMessage message = new ErrorMessage("cette store ou category n'existe pas", new Date(), 400);

			String json = this.objectMapper.writeValueAsString(message);
			
			response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        
	        response.getWriter().write(json);
            
		}else {
		    // Récupère la valeur du champ 'description' à partir de la requête
		    String description = request.getParameter("description");
		    
		    String dateFabricationStr = request.getParameter("date_fabrication");
		    String datePeremptionStr = request.getParameter("date_peremption");
		    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    Date dateFabrication = null;
		    Date datePeremption = null;
		    
		    try {
		    	dateFabrication = dateFormat.parse(dateFabricationStr);
		    	datePeremption = dateFormat.parse(datePeremptionStr);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		    String nom = request.getParameter("nom");
		    double poids = Double.parseDouble(request.getParameter("poids"));
		    double prix = Double.parseDouble(request.getParameter("prix"));
		    Integer stock = Integer.parseInt(request.getParameter("stock"));
			
			System.out.println("description : "+ description);
			
	        
	        
			// Gère le téléchargement de fichiers images
	        Collection<Part> parts = request.getParts();
	        
		    
	        // Initialise un objet ImageDto pour stocker les informations de l'image
	        ImageDto imageDto = new ImageDto();
	        ProduitDto produitDto = new ProduitDto(null, nom, description, prix, stock, dateFabrication, datePeremption, poids, null, null, null);
	        
	        produitDto.setStore(storeDto);
	        produitDto.setCategorys(categories);
			ProduitDto saved = produitService.createProduit(produitDto);
	        
	        System.out.println("produitDto :"+produitDto);
	        
	        for (Part part : parts) {
	        	System.out.println("name :"+part.getName());
	        	if (part.getName().equals("image")) {
	                // Cette partie est le fichier image
	                InputStream inputStream = part.getInputStream();
	                // Traite les données de l'image selon les besoins
	                // ...
	
	                // Obtient le nom du fichier téléchargé
	                // Génère un nom de fichier unique
	                String fileName = generateUniqueFileName(part);
	
	                // Obtient le chemin de sauvegarde
	                String savePath = getUploadPath(request);
	                System.out.println("savePath: " + savePath);
	
	                // S'assure que le répertoire existe
	                File directory = new File(savePath);
	                if (!directory.exists()) {
	                    directory.mkdirs();
	                }
	
	                // Sauvegarde le fichier image
	                Path imagePath = Path.of(savePath, fileName);
	                System.out.println("imagePath: " +imagePath);
	                try {
	                    Files.copy(inputStream, imagePath, StandardCopyOption.REPLACE_EXISTING);
	                    System.out.println("Image copied successfully to: " + imagePath);
	                } catch (IOException e) {
	                    e.printStackTrace();
	                    System.out.println("Error copying image to: " + imagePath);
	                }
	                
	                // Définit le chemin de l'image dans l'objet ImageDto
	                imageDto.setPath(SAVE_DIRECTORY + '\\' + fileName);
	                try {
	                	
	                	imageDto.setProduit(saved);
	                	imageService.createImage(imageDto);
	                	
					} catch (Exception e) {
						e.getStackTrace();
					}
	                
	                System.out.println("imageDto :"+imageDto);
	                
		        }
	//        	else if (part.getName().equals("date_fabrication")) {
	//	            // Assuming you have a 'produit_id' field in your form
	//	            // Retrieve the 'produit_id' value
	//	            BufferedReader reader = new BufferedReader(new InputStreamReader(part.getInputStream()));
	//	            String produitId = reader.readLine();
	//	            imageDto.set(Long.parseLong(produitId));
	//	            System.out.println(produitId);
	//	        }
	        }
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

        Integer Id = Integer.parseInt(request.getPathInfo().split("/")[2]);
        
        if( produitService.readProduit(Id).getProduitId() == null ) {

        	ErrorMessage message = new ErrorMessage("cette produit n'existe pas", new Date(), 400);

			String json = this.objectMapper.writeValueAsString(message);
			
			response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        
	        response.getWriter().write(json);
	        
        }else {
        	
        	Integer storeId = Integer.parseInt(request.getParameter("storeId"));
    	    StoreDto storeDto = storeService.readStore(storeId);
            
            if(storeDto.getStoreId() == null ) {
            	ErrorMessage message = new ErrorMessage("cette store n'existe pas", new Date(), 400);

    			String json = this.objectMapper.writeValueAsString(message);
    			
    			response.setContentType("application/json");
    	        response.setCharacterEncoding("UTF-8");
    	        
    	        response.getWriter().write(json);
    		}else {
    			
    			List<ImageDto> oldImages = imageService.getImagesByProduit(Id);
    			System.out.println("oldImages :" +oldImages);
    			for (ImageDto image : oldImages) {
    				
					String oldImagePath = image.getPath();
					try {
					    // Supprimez l'ancien fichier image associé au produit (si existant)
					    if (oldImagePath != null) {
					    	
					    	// Convertir le chemin en objet Path
					    	Path path = Paths.get(oldImagePath);
					    	// Extraire le nom du fichier
					        String fileName = path.getFileName().toString();
					        
					        Path oldImagePathToDelete = Path.of(getUploadPath(request), fileName);
					        Files.deleteIfExists(oldImagePathToDelete);
					        System.out.println("Old image deleted successfully from: " + oldImagePathToDelete);
					        imageService.deleteImage(image.getImageId());
					    }
					} catch (IOException e) {
					    e.printStackTrace();
					    System.out.println("Error deleting old image");
					}
				}
    			
    			// Récupère la valeur du champ 'description' à partir de la requête
    		    String description = request.getParameter("description");
    		    
    		    String dateFabricationStr = request.getParameter("date_fabrication");
    		    String datePeremptionStr = request.getParameter("date_peremption");
    		    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		    Date dateFabrication = null;
    		    Date datePeremption = null;
    		    
    		    try {
    		    	dateFabrication = dateFormat.parse(dateFabricationStr);
    		    	datePeremption = dateFormat.parse(datePeremptionStr);
    			} catch (ParseException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		    
    		    String nom = request.getParameter("nom");
    		    double poids = Double.parseDouble(request.getParameter("poids"));
    		    double prix = Double.parseDouble(request.getParameter("prix"));
    		    Integer stock = Integer.parseInt(request.getParameter("stock"));
    			
    			System.out.println("description : "+ description);
    			
    	        
    	        
    			// Gère le téléchargement de fichiers images
    	        Collection<Part> parts = request.getParts();
    	        
    	     // Initialise un objet ImageDto pour stocker les informations de l'image
    	        ImageDto imageDto = new ImageDto();
    	        ProduitDto produitDto = new ProduitDto(Id, nom, description, prix, stock, dateFabrication, datePeremption, poids, null, null, null);
    	        produitDto.setStore(storeDto);
    			ProduitDto updated = produitService.updateProduit(produitDto);
    	        
    	        System.out.println("produitDto :"+produitDto);
    			
    	        for (Part part : parts) {
    	        	System.out.println("name :"+part.getName());
    	        	if (part.getName().equals("image")) {
    	                // Cette partie est le fichier image
    	                InputStream inputStream = part.getInputStream();
    	                // Traite les données de l'image selon les besoins
    	                // ...
    	
    	                // Obtient le nom du fichier téléchargé
    	                // Génère un nom de fichier unique
    	                String fileName = generateUniqueFileName(part);
    	
    	                // Obtient le chemin de sauvegarde
    	                String savePath = getUploadPath(request);
    	                System.out.println("savePath: " + savePath);
    	
    	                // S'assure que le répertoire existe
    	                File directory = new File(savePath);
    	                if (!directory.exists()) {
    	                    directory.mkdirs();
    	                }
    	
    	                // Sauvegarde le fichier image
    	                Path imagePath = Path.of(savePath, fileName);
    	                System.out.println("imagePath: " +imagePath);
    	                try {
    	                    Files.copy(inputStream, imagePath, StandardCopyOption.REPLACE_EXISTING);
    	                    System.out.println("Image copied successfully to: " + imagePath);
    	                } catch (IOException e) {
    	                    e.printStackTrace();
    	                    System.out.println("Error copying image to: " + imagePath);
    	                }
    	                
    	                // Définit le chemin de l'image dans l'objet ImageDto
    	                imageDto.setPath(SAVE_DIRECTORY + '\\' + fileName);
    	                try {
    	                	
    	                	imageDto.setProduit(updated);
    	                	imageService.createImage(imageDto);
    	                	
    					} catch (Exception e) {
    						e.getStackTrace();
    					}
    	                
    	                System.out.println("imageDto :"+imageDto);
    	                
    		        }
    	        }
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
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
        Integer Id = Integer.parseInt(request.getPathInfo().split("/")[2]);
        if(produitService.readProduit(Id).getProduitId()== null) {
        	ErrorMessage message = new ErrorMessage("cette produit n'existe pas.", new Date(), 200);

			String json = this.objectMapper.writeValueAsString(message);
			
			response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        
	        response.getWriter().write(json);
        }else {
        	
        	List<ImageDto> images = imageService.getImagesByProduit(Id);
        	for(ImageDto imageDto :images) {
        		String oldImagePath = imageDto.getPath();
				try {
				    // Supprimez l'ancien fichier image associé au produit (si existant)
				    if (oldImagePath != null) {
				    	
				    	// Convertir le chemin en objet Path
				    	Path path = Paths.get(oldImagePath);
				    	// Extraire le nom du fichier
				        String fileName = path.getFileName().toString();
				        
				        Path oldImagePathToDelete = Path.of(getUploadPath(request), fileName);
				        Files.deleteIfExists(oldImagePathToDelete);
				        System.out.println("Old image deleted successfully from: " + oldImagePathToDelete);
				        imageService.deleteImage(imageDto.getImageId());
				    }
				} catch (IOException e) {
				    e.printStackTrace();
				    System.out.println("Error deleting old image");
				}
        	}
        	
        	List<LigneCommandeDto> ligneCommandes = ligneCommandeService.getLigneCommandesByProduit(Id);
        	for( LigneCommandeDto ligneCommande : ligneCommandes ) {
        		ligneCommandeService.deleteLigneCommande(ligneCommande.getId());
        	}
        	
        	produitService.deleteProduit(Id);
            
            if(produitService.readProduit(Id).getProduitId()== null) {

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
        System.out.println(applicationPath);
        System.out.println(File.separator);
        return applicationPath + SAVE_DIRECTORY;
    }
	
}
