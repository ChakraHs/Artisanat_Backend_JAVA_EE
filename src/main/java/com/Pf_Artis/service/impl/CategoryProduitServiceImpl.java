package com.Pf_Artis.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.Pf_Artis.dao.DaoException;
import com.Pf_Artis.dao.DaoFactory;
import com.Pf_Artis.dao.RequestPrepare;
import com.Pf_Artis.dto.CategoryDto;
import com.Pf_Artis.dto.CategoryProduitDto;
import com.Pf_Artis.dto.CategoryProduitKey;
import com.Pf_Artis.dto.ProduitDto;
import com.Pf_Artis.service.facade.CategoryProduitService;
import com.Pf_Artis.service.facade.CategoryServiceInterface;
import com.Pf_Artis.service.facade.ProduitServiceInterface;

import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CategoryProduitServiceImpl implements CategoryProduitService {

	private DaoFactory daoFactory;
	
	private static CategoryProduitDto map( ResultSet resultSet ) throws SQLException {
		
		CategoryProduitDto categoryProduitDto = new CategoryProduitDto();
		CategoryProduitKey categoryProduitKey = new CategoryProduitKey();
		
		categoryProduitKey.setCategoryId( resultSet.getInt( "categoryId" ) );
		categoryProduitKey.setProduitId( resultSet.getInt( "produitId" ) );
	
		categoryProduitDto.setId(categoryProduitKey);
		
		return categoryProduitDto;
	}
	
	@Override
	public List<CategoryProduitDto> findAll() {
		
		final String SQL_SELECT_ALL = " SELECT categoryId , produitId FROM produit_category";
		
		CategoryProduitDto categoryProduitDto = new CategoryProduitDto();
		List<CategoryProduitDto> categoryProduits = new ArrayList<CategoryProduitDto>();
		
		try (
				Connection connexion = daoFactory.getConnection();
	    		PreparedStatement preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_ALL);
	    	    ResultSet resultSet = preparedStatement.executeQuery();
			)
		{
			while( resultSet.next() ) {
				categoryProduitDto = map(resultSet);
				categoryProduits.add(categoryProduitDto);
			}
		} catch (SQLException e) {
			
			throw new DaoException( e );
			
		}
		
		return categoryProduits;
	}

	@Override
	public CategoryProduitDto save(CategoryProduitDto categoryProduitDto) {
		
		ProduitServiceInterface produitService = new ProduitServiceImpl(DaoFactory.getInstance());
		ProduitDto produitDto = produitService.readProduit( categoryProduitDto.getProduit().getProduitId() );
		
		CategoryServiceInterface categoryService = new CategoryServiceImpl(DaoFactory.getInstance());
		CategoryDto categoryDto = categoryService.readCategory( categoryProduitDto.getCategory().getCategoryId());
		
		if( categoryDto.getCategoryId() !=null && produitDto.getProduitId()!=null ) {
			
			final String SQL_INSERT = "INSERT INTO produit_category ( produitId , categoryId ) VALUES ( ? , ? ) ";
			
			try (
					Connection connexion = daoFactory.getConnection();
		    		PreparedStatement preparedStatement = RequestPrepare.initRequestPrepare( 
	    				connexion, 
	    				SQL_INSERT,
	    				categoryProduitDto.getProduit().getProduitId(),
	    				categoryProduitDto.getCategory().getCategoryId()
	    			);
				){
				
				preparedStatement.executeUpdate();
				
			} catch (SQLException e) {
				throw new DaoException( e );
			}
			
		}
		
		return categoryProduitDto;
	}

	@Override
	public CategoryProduitDto update(CategoryProduitDto categoryProduitDto, CategoryProduitKey key)
			throws NotFoundException {
		
		return null;
	}

	@Override
	public CategoryProduitDto findById(CategoryProduitKey key) {
		
		final String SQL_SELECT_PAR_ID = " SELECT categoryId , produitId FROM produit_category where produitId = ? and categoryId = ? ";
		
		CategoryProduitDto categoryProduitDto = new CategoryProduitDto();
		
		try (
				Connection connexion = daoFactory.getConnection();
	    		PreparedStatement preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_PAR_ID , key.getProduitId() , key.getCategoryId());
	    	    ResultSet resultSet = preparedStatement.executeQuery();
			){
			
			if ( resultSet.next() ) {
	        	
				categoryProduitDto = map( resultSet );
	            
	        }
		} catch (SQLException e) {

			throw new DaoException( e );
		}
		
		return categoryProduitDto;
	}

	@Override
	public List<CategoryProduitDto> findByProduit(Integer produitId) {
		
		final String SQL_SELECT_PAR_PRODUIT = " SELECT categoryId , produitId FROM produit_category where produitId = ? ";
		
		CategoryProduitDto categoryProduitDto = new CategoryProduitDto();
		List<CategoryProduitDto> categoryProduits = new ArrayList<CategoryProduitDto>();
		
		try (
				Connection connexion = daoFactory.getConnection();
	    		PreparedStatement preparedStatement = RequestPrepare.initRequestPrepare( 
    				connexion, 
    				SQL_SELECT_PAR_PRODUIT , 
    				produitId
    			);
	    	    ResultSet resultSet = preparedStatement.executeQuery();
			){
			
			if ( resultSet.next() ) {
	        	
				categoryProduitDto = map( resultSet );
				categoryProduits.add(categoryProduitDto);
	            
	        }
		} catch (SQLException e) {

			throw new DaoException( e );
		}
		
		
		return categoryProduits;
	}

	@Override
	public List<CategoryProduitDto> findByCategory(Integer categoryId) {

		final String SQL_SELECT_PAR_CATEGORY = " SELECT categoryId , produitId FROM produit_category where categoryId = ? ";
		
		CategoryProduitDto categoryProduitDto = new CategoryProduitDto();
		List<CategoryProduitDto> categoryProduits = new ArrayList<CategoryProduitDto>();
		
		try (
				Connection connexion = daoFactory.getConnection();
	    		PreparedStatement preparedStatement = RequestPrepare.initRequestPrepare( 
    				connexion, 
    				SQL_SELECT_PAR_CATEGORY , 
    				categoryId
    			);
	    	    ResultSet resultSet = preparedStatement.executeQuery();
			){
			
			if ( resultSet.next() ) {
	        	
				categoryProduitDto = map( resultSet );
				categoryProduits.add(categoryProduitDto);
	            
	        }
		} catch (SQLException e) {

			throw new DaoException( e );
		}
		
		
		return categoryProduits;
	}

	@Override
	public void delete(CategoryProduitKey key) {
		
		final String SQL_DESTROY = " Delete from produit_category where produitId = ? and categoryId = ? ";
		
		try (
				Connection connexion = daoFactory.getConnection();
	    		PreparedStatement preparedStatement = RequestPrepare.initRequestPrepare( 
    				connexion, 
    				SQL_DESTROY
    			);
			)
		{
			
			preparedStatement.execute();
			
		} catch (SQLException e) {
			throw new DaoException( e );	
		}
	}

}
