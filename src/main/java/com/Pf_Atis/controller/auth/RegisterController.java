package com.Pf_Atis.controller.auth;

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
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

import com.Pf_Artis.config.GenerateJwtToken;
import com.Pf_Artis.dao.DaoFactory;
import com.Pf_Artis.dto.RoleDto;
import com.Pf_Artis.dto.UserDto;
import com.Pf_Artis.service.facade.RoleService;
import com.Pf_Artis.service.facade.UserServiceInterface;
import com.Pf_Artis.service.impl.RoleServiceImpl;
import com.Pf_Artis.service.impl.UserServiceImpl;
import com.Pf_Artis.shared.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class RegisterController
 */
@WebServlet(name="RegisterController",urlPatterns = {"/api/register"} )
@MultipartConfig
public class RegisterController extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static final String SAVE_DIRECTORY = "assets\\images\\profiles";
	private UserServiceInterface userService ;
	private RoleService roleService;
	private ObjectMapper objectMapper = new ObjectMapper();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterController() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public void init() throws ServletException {
    	
    	DaoFactory daoFactory = DaoFactory.getInstance();
    	userService = new UserServiceImpl(daoFactory);
    	roleService = new RoleServiceImpl(daoFactory);
    	
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// Définit le jeu de caractères pour l'encodage des données de la requête
	    request.setCharacterEncoding("UTF-8");
	    
	    String roleStr = request.getParameter("role");
	    RoleDto roleDto  = roleService.findByName(roleStr);
	    if(roleDto.getRoleId()!=null) {
	    
	        String nom = request.getParameter("nom");
	        String prenom = request.getParameter("prenom");
	        String telephone = request.getParameter("telephone");
	        String email = request.getParameter("email");
	        String password = request.getParameter("password");
	        String rue = request.getParameter("rue");
	        String ville = request.getParameter("ville");
	        String numero = request.getParameter("numero");
	        
		    UserDto userDto = new UserDto(null, nom, prenom, ville, rue, numero, telephone, null, email, password, null, null);
	        
	        String hashedPassword = BCrypt.hashpw(userDto.getPassword(), BCrypt.gensalt());
	        
	        userDto.setPassword(hashedPassword);
	        
	        String jwt = null;
			try {
				
				jwt = GenerateJwtToken.generateJwtToken(userDto.getEmail());
				
			} catch (NoSuchAlgorithmException e) {
				
				e.printStackTrace();
				
			}
	    	
			userDto.setToken(jwt);
			userDto.setRole(roleDto);
			
			// Gère le téléchargement de fichiers images
	        Collection<Part> parts = request.getParts();
	        for (Part part : parts) {
	        	System.out.println("name :"+part.getName());
	        	if (part.getName().equals("profile")) {
	        		System.out.println( "name :"+part.getName() );
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
	                userDto.setProfile(SAVE_DIRECTORY + '\\' + fileName);
	                
		        }
	        }
			
        	UserDto saved = userService.createUser(userDto);
        	
        	saved.setPassword(null);
            request.getSession().setAttribute("user", saved);
            try {
            	
            	String json = objectMapper.writeValueAsString(saved);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                
                response.getWriter().write(json);
                
    		} catch (Exception e) {

    			e.printStackTrace();
    			
    		}
        }else {

			ErrorMessage message = new ErrorMessage("cette role n'existe pas.", new Date(), 400);

			String json = objectMapper.writeValueAsString(message);
			
			response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        
	        response.getWriter().write(json);
            
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
