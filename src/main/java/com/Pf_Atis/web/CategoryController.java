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
import com.Pf_Artis.models.Category;
import com.Pf_Artis.service.facade.CategoryServiceInterface;
import com.Pf_Artis.service.impl.CategoryServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class CategoryController
 */
@WebServlet(name="CategoryController",urlPatterns = {"/api/categories/*"})
public class CategoryController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private CategoryServiceInterface categoryService;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CategoryController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		
		DaoFactory daoFactory = DaoFactory.getInstance();
		categoryService = new CategoryServiceImpl(daoFactory);
		
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String path=request.getPathInfo();
		
		if( path == null || path.split("/")[1].equals("*")) {
			
			List<Category> categories = categoryService.getAllCategories();
			
			if(!categories.isEmpty()) {
					
				try {
				
					ObjectMapper objectMapper = new ObjectMapper();
					String json = objectMapper.writeValueAsString(categories);
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write(json);
			        
				} catch (Exception e) {
					
					e.printStackTrace();
					
				}
				
			}
			else {
				
				response.getWriter().write("Aucun category trouvé.");
				
			}
		}
		else {
			
			String[] pathParts = path.split("/");
			
			if(pathParts[1].equals("id")) {
				
				Long Id = Long.parseLong(pathParts[2]);
				Category category = categoryService.readCategory(Id);
				
				if( category.getId() == null ) {
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write("Aucun category trouvé.");
			        
				}else {
					
					try {
						
						ObjectMapper objectMapper = new ObjectMapper();
						String json = objectMapper.writeValueAsString(category);
						
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
        
		Category newCategory = objectMapper.readValue(request.getReader(), Category.class);
        Category category = categoryService.createCategory(newCategory);
        
        try {
        	
        	String json = objectMapper.writeValueAsString(category);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            response.getWriter().write(json);
            
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		ObjectMapper objectMapper = new ObjectMapper();
        
		Category newcategory = objectMapper.readValue(request.getReader(), Category.class);
        Long Id = Long.parseLong(request.getPathInfo().split("/")[2]);
        
        newcategory.setId(Id);
        
        Category category = categoryService.updateCategory(newcategory);
        
        try {
        	
        	String json = objectMapper.writeValueAsString(category);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            response.getWriter().write(json);
            
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		

        Long Id = Long.parseLong(request.getPathInfo().split("/")[2]);
        categoryService.deleteCategory(Id);
        
        if(categoryService.readCategory(Id).getId() == null) {
        	
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
