package com.Pf_Artis.service.facade;

import java.util.List;

import com.Pf_Artis.dto.CategoryProduitDto;
import com.Pf_Artis.dto.CategoryProduitKey;

import jakarta.ws.rs.NotFoundException;

public interface CategoryProduitService {

	List<CategoryProduitDto> findAll();
	
	CategoryProduitDto save( CategoryProduitDto categoryProduitDto );
	
	CategoryProduitDto update( CategoryProduitDto categoryProduitDto , CategoryProduitKey key ) throws NotFoundException;
	
	CategoryProduitDto findById( CategoryProduitKey key );
	
	List<CategoryProduitDto> findByProduit( Integer produitId );
	
	List<CategoryProduitDto> findByCategory( Integer categoryId );
	
	void delete( CategoryProduitKey key );
	
}
