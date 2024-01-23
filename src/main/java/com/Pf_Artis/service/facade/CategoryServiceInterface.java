package com.Pf_Artis.service.facade;

import java.util.List;

import javax.ws.rs.NotFoundException;

import com.Pf_Artis.dto.CategoryDto;

public interface CategoryServiceInterface {

	public CategoryDto createCategory( CategoryDto categoryDto );
	
    public CategoryDto readCategory( Integer id );
    
    public CategoryDto updateCategory( CategoryDto categoryDto )throws NotFoundException;
    
    public void deleteCategory( Integer id );
    
    public List<CategoryDto> getAllCategories();
	
}
