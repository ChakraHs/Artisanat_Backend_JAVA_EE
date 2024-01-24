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
import com.Pf_Artis.dto.StoreDto;
import com.Pf_Artis.dto.UserDto;
import com.Pf_Artis.exception.EntityNotFoundException;
import com.Pf_Artis.service.facade.StoreServiceInterface;
import com.Pf_Artis.service.facade.UserServiceInterface;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StoreServiceImpl implements StoreServiceInterface {
	
	DaoFactory daoFactory ;

	private static StoreDto map( ResultSet resultSet ) throws SQLException {
		
		StoreDto storeDto = new StoreDto();
		
		storeDto.setStoreId( resultSet.getInt( "store_id" ) );
		storeDto.setNom( resultSet.getString( "nom" ) );
		storeDto.setAdress( resultSet.getString("adress"));
		storeDto.setAvatar( resultSet.getString("avatar") );
		
		UserServiceInterface userService = new UserServiceImpl(DaoFactory.getInstance());
		UserDto userDto = userService.readUser( resultSet.getInt("artisant_id") );
		
		storeDto.setArtisant(userDto);
		
		return storeDto;
	}
	
	
	@Override
	public StoreDto createStore(StoreDto storeDto) throws NotFoundException{

		final String SQL_INSERT = "INSERT INTO store ( adress , nom , artisant_id , avatar ) VALUES (  ? , ? , ? , ? ) ";
		
		UserServiceInterface userService = new UserServiceImpl(DaoFactory.getInstance());
		UserDto artisan = userService.readUser( storeDto.getArtisant().getUserId() );
		
		if( artisan.getUserId() != null ) {
			if(storeDto.getArtisant().getRole().getName().equals("ROLE_ARTISAN")) {
				try (
				        Connection connexion = daoFactory.getConnection();
				        PreparedStatement preparedStatement = RequestPrepare.initRequestPrepare(
				                connexion,
				                SQL_INSERT,
				                storeDto.getAdress(),
				                storeDto.getNom(),
				                storeDto.getArtisant().getUserId(),
				                storeDto.getAvatar()
				        );
				    ) {
					preparedStatement.executeUpdate();
			        
			        
				} catch (SQLException e) {
					throw new DaoException( e );
				}
				
				return getLastStores();
		    }
		    else {
		    	throw new EntityNotFoundException("The user role should be ROLE_ARTISAN");
		    }
		}else {
			throw new EntityNotFoundException("User Not found");
		}
	    
	}

	@Override
	public StoreDto readStore(Integer id) {
		
		final String SQL_SELECT_PAR_ID = "SELECT store_id , adress , nom , artisant_id , avatar FROM store WHERE store_id = ?";
	    
	    StoreDto storeDto = new StoreDto();
	    
	    try (
    		Connection connexion = daoFactory.getConnection();
    		PreparedStatement preparedStatement = RequestPrepare.initRequestPrepare( 
	        		connexion, 
	        		SQL_SELECT_PAR_ID, 
	        		id 
	        	);
    		ResultSet resultSet = preparedStatement.executeQuery()
	    	)
	    {
	        if ( resultSet.next() ) {
	        	
	        	storeDto = map( resultSet );
	            
	        }
	        
		} catch (SQLException e) {

			throw new DaoException( e );
			
		}
	    
		return storeDto;
	}

	@Override
	public StoreDto updateStore(StoreDto storeDto) throws NotFoundException {
		
		final String SQL_UPDATE = "UPDATE store SET adress = ? , nom = ? , avatar = ? where store_id = ? ";
		
		if( this.readStore( storeDto.getStoreId() ) != null) {
			
			try (
					Connection connexion = daoFactory.getConnection();
					PreparedStatement preparedStatement = RequestPrepare.initRequestPrepare(
						connexion , 
						SQL_UPDATE, 
						storeDto.getAdress() , 
						storeDto.getNom() , 
						storeDto.getAvatar() , 
						storeDto.getStoreId() 
					);
				)
			{
				preparedStatement.executeUpdate();
				
			} catch (SQLException e) {
				
				throw new DaoException( e );
				
			}
			
			return storeDto;
		}else {
			throw new EntityNotFoundException("Store Not found");
		}
	}

	@Override
	public void deleteStore(Integer id) {
		final String SQL_DESTROY = " Delete from store where store_id = ? ";
		
		try(
				Connection connexion = daoFactory.getConnection();
				PreparedStatement preparedStatement = RequestPrepare.initRequestPrepare( 
	        		connexion, 
	        		SQL_DESTROY , 
	        		id 
	        	);
			) 
		{
	        preparedStatement.execute();
			
		} catch (SQLException e) {
			
			throw new DaoException( e );
			
		}
	}

	@Override
	public List<StoreDto> getAllStores() {
		final String SQL_SELECT_ALL = "SELECT store_id , adress , nom , artisant_id , avatar FROM store";
	    
	    StoreDto storeDto = new StoreDto();
	    List<StoreDto> stores = new ArrayList<StoreDto>();
	    
	    try (
	    		Connection connexion = daoFactory.getConnection();
	    		PreparedStatement preparedStatement = RequestPrepare.initRequestPrepare(
	    			connexion, 
	    			SQL_SELECT_ALL 
	    		);
	    	    ResultSet resultSet = preparedStatement.executeQuery();
	    	)
	    {
	    	
	    	while ( resultSet.next() ) {
	            storeDto = map( resultSet );
	            stores.add(storeDto);
	        }
	    	
		} catch (SQLException e) {
			
			throw new DaoException( e );
			
		}
		
		return stores;
	}
	
	@Override
	public StoreDto getLastStores() {
		
		final String SQL_SELECT_MAX = " SELECT max(store_id) as max_id from store ";
		System.out.println(" -5-1- test ");
	    
	    Integer storeId = null ;
	    System.out.println(" -5-2- test ");
	    try (
	    		Connection connexion = daoFactory.getConnection();
	    	    PreparedStatement preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_MAX );
	    	    ResultSet resultSet = preparedStatement.executeQuery();
	    	)
	    
	    {
	    	System.out.println(" -5-3- test ");
	        
	        if ( resultSet.next() ) {
	        	System.out.println(" -5-4- test ");
	        	storeId = resultSet.getInt("max_id");
	            
	        }
		} catch (SQLException e) {

			throw new DaoException( e );
			
		}
	    return readStore(storeId);
	}

}
