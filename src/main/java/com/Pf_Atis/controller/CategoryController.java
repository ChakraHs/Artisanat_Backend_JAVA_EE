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
import com.Pf_Artis.dto.CategoryDto;
import com.Pf_Artis.service.facade.CategoryServiceInterface;
import com.Pf_Artis.service.impl.CategoryServiceImpl;
import com.Pf_Artis.shared.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class CategoryController
 */
@WebServlet(name="CategoryController",urlPatterns = {"/api/categories/*"})
public class CategoryController extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private CategoryServiceInterface categoryService;
	private ObjectMapper objectMapper = new ObjectMapper();
       
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
			
			List<CategoryDto> categories = categoryService.getAllCategories();
			
			if(!categories.isEmpty()) {
					
				try {
					
					String json = this.objectMapper.writeValueAsString(categories);
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write(json);
			        
				} catch (Exception e) {
					
					e.printStackTrace();
					
				}
				
			}
			else {
				
				ErrorMessage message = new ErrorMessage("Aucun category trouvé.", new Date(), 400);

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
				System.out.println(Id);
				CategoryDto categoryDto = categoryService.readCategory(Id);
				System.out.println(categoryDto);
				if( categoryDto.getCategoryId() == null ) {
					
					ErrorMessage message = new ErrorMessage("Aucun category trouvé.", new Date(), 400);

					String json = this.objectMapper.writeValueAsString(message);
					
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        
			        response.getWriter().write(json);
			        
				}else {
					
					try {
						
						String json = this.objectMapper.writeValueAsString(categoryDto);
						
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
        
		CategoryDto newCategoryDto = this.objectMapper.readValue(request.getReader(), CategoryDto.class);
        CategoryDto categoryDto = categoryService.createCategory(newCategoryDto);
        
        try {
        	
        	String json = this.objectMapper.writeValueAsString(categoryDto);
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
        
		CategoryDto newcategoryDto = this.objectMapper.readValue(request.getReader(), CategoryDto.class);
        Integer Id = Integer.parseInt(request.getPathInfo().split("/")[2]);
        
        newcategoryDto.setCategoryId(Id);
        
        CategoryDto categoryDto = categoryService.updateCategory(newcategoryDto);
        
        try {
        	
        	String json = this.objectMapper.writeValueAsString(categoryDto);
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
		
		Integer Id = Integer.parseInt(request.getPathInfo().split("/")[2]);
        categoryService.deleteCategory(Id);
        
        if(categoryService.readCategory(Id).getCategoryId() == null) {
        	
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
