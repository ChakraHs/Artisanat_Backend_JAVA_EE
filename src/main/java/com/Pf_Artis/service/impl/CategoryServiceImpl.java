package com.Pf_Artis.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.NotFoundException;

import com.Pf_Artis.dao.DaoException;
import com.Pf_Artis.dao.DaoFactory;
import com.Pf_Artis.dao.RequestPrepare;
import com.Pf_Artis.dto.CategoryDto;
import com.Pf_Artis.exception.EntityNotFoundException;
import com.Pf_Artis.service.facade.CategoryServiceInterface;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CategoryServiceImpl implements CategoryServiceInterface {

	private DaoFactory daoFactory;

	private static CategoryDto map( ResultSet resultSet ) throws SQLException {
		
		CategoryDto categoryDto = new CategoryDto();
		categoryDto.setCategoryId( resultSet.getInt( "category_id" ) );
		categoryDto.setNom( resultSet.getString( "nom" ) );
		categoryDto.setDescription( resultSet.getString("description") );
		
		return categoryDto;
	}
	
	@Override
	public CategoryDto createCategory(CategoryDto categoryDto) {
		
		final String SQL_INSERT = "INSERT INTO category ( nom , description ) VALUES (  ? , ? ) ";
		final String SQL_SELECT_MAX = " SELECT max(category_id) as max_id from category ";
		
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    try {
	    	connexion = daoFactory.getConnection();
	    	
	    	preparedStatement = RequestPrepare.initRequestPrepare( connexion , SQL_INSERT , categoryDto.getNom() , categoryDto.getDescription() );
	        preparedStatement.executeUpdate();
	        
	        PreparedStatement ps2 = RequestPrepare.initRequestPrepare( connexion , SQL_SELECT_MAX );
	        resultSet = ps2.executeQuery();
	        
	        if(resultSet.next()) {
				
	        	categoryDto.setCategoryId(resultSet.getInt("max_id"));
				
			}
	    	
		} catch (SQLException e) {
			throw new DaoException( e );
		}
		
		return categoryDto;
	}

	@Override
	public CategoryDto readCategory(Integer id) {
		
		final String SQL_SELECT_PAR_ID = "SELECT category_id , description , nom FROM category WHERE category_id = ? ";
		
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    CategoryDto categoryDto = new CategoryDto();
	    
	    try {
			
	    	connexion = daoFactory.getConnection();
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_PAR_ID, id );
	        resultSet = preparedStatement.executeQuery();
	        
	        if ( resultSet.next() ) {
	        	
	        	categoryDto = map( resultSet );
	            
	        }
		} catch (SQLException e) {

			throw new DaoException( e );
			
		}
		return categoryDto;
	}

	@Override
	public CategoryDto updateCategory(CategoryDto categoryDto) throws NotFoundException {
		
		CategoryDto dto = this.readCategory(categoryDto.getCategoryId());
		
		if(dto.getCategoryId() != null) {
			final String SQL_UPDATE = "UPDATE category SET  nom = ? , description = ? where category_id = ? ";
			
			Connection connexion = null;
			PreparedStatement preparedStatement = null;
			
			try {
				
				connexion = daoFactory.getConnection();
				
		        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_UPDATE  , categoryDto.getNom() , categoryDto.getDescription() , categoryDto.getCategoryId()  );
		        preparedStatement.executeUpdate();
				
			} catch (SQLException e) {
				
				throw new DaoException( e );
				
			}
			
			return categoryDto;
		}else {
			throw new EntityNotFoundException("Category Not found");
		}
	}

	@Override
	public void deleteCategory(Integer id) {
		final String SQL_DESTROY = " Delete from category where category_id=? ";
		
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		
		try {
			
			connexion = daoFactory.getConnection();
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_DESTROY , id );
	        preparedStatement.execute();
			
		} catch (SQLException e) {
			
			throw new DaoException( e );
			
		}
	}

	@Override
	public List<CategoryDto> getAllCategories() {
		final String SQL_SELECT_ALL = " SELECT category_id , description , nom FROM category ";
		
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    CategoryDto categoryDto = new CategoryDto();
	    List<CategoryDto> categories = new ArrayList<CategoryDto>();
	    
	    try {
	    	connexion = daoFactory.getConnection();
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_ALL);
	        resultSet = preparedStatement.executeQuery();
	        
	        while ( resultSet.next() ) {
	        	categoryDto = map( resultSet );
	            categories.add(categoryDto);
	        }
		} catch (SQLException e) {
			
			throw new DaoException( e );
			
		}
	    
		return categories;
	}

	@Override
	public boolean categoryExiste(Integer id) {
		
		return readCategory(id).getCategoryId() != null ;
	}
	
}
