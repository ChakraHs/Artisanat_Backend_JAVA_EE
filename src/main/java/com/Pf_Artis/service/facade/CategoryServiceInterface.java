package com.Pf_Artis.service.facade;

import java.util.List;

import com.Pf_Artis.models.Category;

public interface CategoryServiceInterface {

	public Category createCategory( Category category );
    public Category readCategory( Long id );
    public Category updateCategory( Category category );
    public void deleteCategory( Long id );
    
    public List<Category> getAllCategories();
	
}
