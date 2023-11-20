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
import com.Pf_Artis.models.Category;
import com.Pf_Artis.service.facade.CategoryServiceInterface;

public class CategoryServiceImpl implements CategoryServiceInterface {

	private DaoFactory daoFactory;
	
	public CategoryServiceImpl(DaoFactory daoFactory) {
		super();
		this.daoFactory = daoFactory;
	}

	private static Category map( ResultSet resultSet ) throws SQLException {
		
		Category category = new Category();
		category.setId( resultSet.getLong( "id" ) );
		category.setNom( resultSet.getString( "nom" ) );
		category.setDescription( resultSet.getString("description") );
		
		return category;
	}
	
	@Override
	public Category createCategory(Category category) {
		
		final String SQL_INSERT = "INSERT INTO category ( nom , description ) VALUES (  ? , ? ) ";
		final String SQL_SELECT_MAX = " SELECT max(id) as max_id from category ";
		
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    try {
	    	connexion = daoFactory.getConnection();
	    	
	    	preparedStatement = RequestPrepare.initRequestPrepare( connexion , SQL_INSERT , category.getNom() , category.getDescription() );
	        preparedStatement.executeUpdate();
	        
	        PreparedStatement ps2 = RequestPrepare.initRequestPrepare( connexion , SQL_SELECT_MAX );
	        resultSet = ps2.executeQuery();
	        
	        if(resultSet.next()) {
				
				category.setId(resultSet.getLong("max_id"));
				
			}
	    	
		} catch (SQLException e) {
			throw new DaoException( e );
		}
		
		return category;
	}

	@Override
	public Category readCategory(Long id) {
		
		final String SQL_SELECT_PAR_ID = "SELECT id , description , nom FROM category WHERE id = ? ";
		
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    Category category = new Category();
	    
	    try {
			
	    	connexion = daoFactory.getConnection();
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_PAR_ID, id );
	        resultSet = preparedStatement.executeQuery();
	        
	        if ( resultSet.next() ) {
	        	
	        	category = map( resultSet );
	            
	        }
		} catch (SQLException e) {

			throw new DaoException( e );
			
		}
		return category;
	}

	@Override
	public Category updateCategory(Category category) {
		
		final String SQL_UPDATE = "UPDATE category SET  nom = ? , description = ? where id = ? ";
		
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		
		try {
			
			connexion = daoFactory.getConnection();
			
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_UPDATE  , category.getNom() , category.getDescription() , category.getId()  );
	        preparedStatement.executeUpdate();
			
		} catch (SQLException e) {
			
			throw new DaoException( e );
			
		}
		
		return category;
	}

	@Override
	public void deleteCategory(Long id) {
		final String SQL_DESTROY = " Delete from category where id=? ";
		
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
	public List<Category> getAllCategories() {
		final String SQL_SELECT_ALL = " SELECT id , description , nom FROM category ";
		
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    Category category = new Category();
	    List<Category> categories = new ArrayList<Category>();
	    
	    try {
	    	connexion = daoFactory.getConnection();
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_ALL);
	        resultSet = preparedStatement.executeQuery();
	        
	        while ( resultSet.next() ) {
	            category = map( resultSet );
	            categories.add(category);
	        }
		} catch (SQLException e) {
			
			throw new DaoException( e );
			
		}
	    
		return categories;
	}

}
