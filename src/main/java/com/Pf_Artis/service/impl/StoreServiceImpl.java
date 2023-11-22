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
import com.Pf_Artis.models.Store;
import com.Pf_Artis.models.User;
import com.Pf_Artis.service.facade.StoreServiceInterface;
import com.Pf_Artis.service.facade.UserServiceInterface;

public class StoreServiceImpl implements StoreServiceInterface {
	
	DaoFactory daoFactory ;
	
	public StoreServiceImpl( DaoFactory daoFactory ) {

		super();
		this.daoFactory = daoFactory;
		
	}

	private static Store map( ResultSet resultSet ) throws SQLException {
		Store store = new Store();
		store.setId( resultSet.getLong( "id" ) );
		store.setNom( resultSet.getString( "nom" ) );
		store.setAdress( resultSet.getString("adress"));
		store.setTelephone( resultSet.getString("telephone") );
		store.setProfile( resultSet.getString("profile") );
		
		UserServiceInterface userService = new UserServiceImpl(DaoFactory.getInstance());
		User user = userService.readUser( resultSet.getLong("artisant_id") );
		
		store.setArtisant(user);
		
		return store;
	}
	
	
	@Override
	public Store createStore(Store store) {
		
		final String SQL_INSERT = "INSERT INTO store ( adress , nom , telephone , artisant_id , profile ) VALUES (  ? , ? , ? , ? , ? ) ";
		final String SQL_SELECT_MAX = " SELECT max(id) as max_id from store ";
		
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    if(store.getArtisant().getRole().equals("artisan")) {
	    	try {
				
		    	connexion = daoFactory.getConnection();
		    	
		    	preparedStatement = RequestPrepare.initRequestPrepare( connexion , SQL_INSERT , store.getAdress() , store.getNom() , store.getTelephone() , store.getArtisant().getId() , store.getProfile() );
		    	preparedStatement.executeUpdate();
		    	
		    	PreparedStatement ps2 = RequestPrepare.initRequestPrepare( connexion , SQL_SELECT_MAX );
		        resultSet = ps2.executeQuery();
		    	
		        if(resultSet.next()) {
					
					store.setId(resultSet.getLong("max_id"));
					
				}
		        
			} catch (SQLException e) {
				throw new DaoException( e );
			}
			
			return store;
	    }
	    else {
	    	return null;
	    }
	    
	}

	@Override
	public Store readStore(Long id) {
		
		final String SQL_SELECT_PAR_ID = "SELECT id , adress , nom , telephone , artisant_id , profile FROM store WHERE id = ?";
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    Store store = new Store();
	    
	    try {
			
	    	connexion = daoFactory.getConnection();
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_PAR_ID, id );
	        resultSet = preparedStatement.executeQuery();
	        
	        if ( resultSet.next() ) {
	        	
	        	store = map( resultSet );
	            
	        }
	        
		} catch (SQLException e) {

			throw new DaoException( e );
			
		}
	    
		return store;
	}

	@Override
	public Store updateStore(Store store) {
		
		final String SQL_UPDATE = "UPDATE store SET adress = ? , nom = ? , telephone = ? , profile = ? where id = ? ";
		
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		
		try {
			
			connexion = daoFactory.getConnection();
			
			preparedStatement = RequestPrepare.initRequestPrepare(connexion, SQL_UPDATE, store.getAdress(),store.getNom(),store.getTelephone() , store.getProfile() , store.getId());
			preparedStatement.executeUpdate();
			
			
			
		} catch (SQLException e) {
			
			throw new DaoException( e );
			
		}
		
		return store;
	}

	@Override
	public void deleteStore(Long id) {
		final String SQL_DESTROY = " Delete from store where id=? ";
		
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
	public List<Store> getAllStores() {
		final String SQL_SELECT_ALL = "SELECT id , adress , nom , telephone , artisant_id , profile FROM store";
		
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    Store store = new Store();
	    List<Store> stores = new ArrayList<Store>();
	    
	    try {
			
	    	connexion = daoFactory.getConnection();
	    	preparedStatement = RequestPrepare.initRequestPrepare(connexion, SQL_SELECT_ALL );
	    	resultSet = preparedStatement.executeQuery();
	    	
	    	while ( resultSet.next() ) {
	            store = map( resultSet );
	            stores.add(store);
	        }
	    	
		} catch (SQLException e) {
			
			throw new DaoException( e );
			
		}
		
		return stores;
	}

}
