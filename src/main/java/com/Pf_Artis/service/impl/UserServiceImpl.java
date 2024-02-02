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
import com.Pf_Artis.dto.RoleDto;
import com.Pf_Artis.dto.UserDto;
import com.Pf_Artis.exception.EntityNotFoundException;
import com.Pf_Artis.service.facade.RoleService;
import com.Pf_Artis.service.facade.UserServiceInterface;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserServiceImpl implements UserServiceInterface {
	
	private DaoFactory daoFactory;
	
	private static UserDto map( ResultSet resultSet ) throws SQLException {
	
		UserDto userDto = new UserDto();
		
		userDto.setUserId( resultSet.getInt( "user_id" ) );
		userDto.setNom( resultSet.getString( "nom" ) );
		userDto.setPrenom( resultSet.getString( "prenom" ) );
		userDto.setNumero( resultSet.getString("numero") );
		userDto.setRue( resultSet.getString("rue") );
		userDto.setVille( resultSet.getString("ville") );
		userDto.setTelephone( resultSet.getString("telephone") );
		userDto.setEmail( resultSet.getString("email") );
		userDto.setProfile( resultSet.getString( "profile" ) );
		
		RoleService roleService = new RoleServiceImpl(DaoFactory.getInstance());
		RoleDto roleDto = roleService.findById( resultSet.getInt( "role_id" ) );
		
		userDto.setRole( roleDto );
		
		return userDto;
		
	}
	
	@Override
	public UserDto createUser(UserDto userDto) throws NotFoundException{
		
		RoleService roleService = new RoleServiceImpl(DaoFactory.getInstance());
		RoleDto roleDto = roleService.findById( userDto.getRole().getRoleId() );
		
		if(roleDto.getRoleId()!=null) {
			
			final String SQL_INSERT = "INSERT INTO user ( numero , rue , ville , email , nom , password , prenom , role_id , telephone , profile , token ) VALUES (  ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? ) ";
			final String SQL_SELECT_MAX = " SELECT max(user_id) as max_id from user ";
			
			Connection connexion = null;
			PreparedStatement preparedStatement = null;
		    ResultSet resultSet = null;
		    
		    try {
		    	connexion = daoFactory.getConnection();
		    	
		    	preparedStatement = RequestPrepare.initRequestPrepare( 
		    			connexion , 
		    			SQL_INSERT , 
		    			userDto.getNumero() , 
		    			userDto.getRue() , 
		    			userDto.getVille() , 
		    			userDto.getEmail() , 
		    			userDto.getNom() , 
		    			userDto.getPassword() , 
		    			userDto.getPrenom() , 
		    			userDto.getRole().getRoleId() , 
		    			userDto.getTelephone() , 
		    			userDto.getProfile() , 
		    			userDto.getToken() 
		    		);
		        preparedStatement.executeUpdate();
		        
		        PreparedStatement ps2 = RequestPrepare.initRequestPrepare( 
		        		connexion , 
		        		SQL_SELECT_MAX 
		        	);
		        resultSet = ps2.executeQuery();
		        
		        if(resultSet.next()) {
					
					userDto.setUserId(resultSet.getInt("max_id"));
					
				}
		    	
			} catch (SQLException e) {
				throw new DaoException( e );
			}
			userDto.setPassword(null);
			return userDto;
		}else {
			throw new EntityNotFoundException("Role Not found");
		}
	}

	@Override
	public UserDto readUser(Integer id) {
		
		final String SQL_SELECT_PAR_ID = "SELECT user_id , numero , ville , rue , email , nom , prenom , role_id , telephone , profile FROM user  WHERE user_id = ?";
		
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    UserDto userDto = new UserDto();
	    
	    try {
	    	
	    	connexion = daoFactory.getConnection();
	        preparedStatement = RequestPrepare.initRequestPrepare( 
	        		connexion, 
	        		SQL_SELECT_PAR_ID, 
	        		id 
	        	);
	        resultSet = preparedStatement.executeQuery();
	        
	        if ( resultSet.next() ) {
	        	
	        	userDto = map( resultSet );
	            
	        }
		} catch (SQLException e) {

			throw new DaoException( e );
			
		}
		return userDto;
	}

	@Override
	public UserDto updateUser( UserDto userDto ) throws NotFoundException {
		
		if( this.readUser( userDto.getUserId() ) != null ) {

			final String SQL_UPDATE = "UPDATE user SET numero = ? , rue = ? , ville = ? , email = ? , nom = ? , prenom = ? , telephone = ? , profile = ? ,token = ? where user_id = ? ";
			
			
			try (
					Connection connexion = daoFactory.getConnection();
					PreparedStatement preparedStatement = RequestPrepare.initRequestPrepare( 
		        		connexion, 
		        		SQL_UPDATE  , 
		        		userDto.getNumero() , 
		        		userDto.getRue() , 
		        		userDto.getVille() , 
		        		userDto.getEmail() , 
		        		userDto.getNom() , 
		        		userDto.getPrenom() , 
		        		userDto.getTelephone() , 
		        		userDto.getProfile() , 
		        		userDto.getToken(),
		        		userDto.getUserId()  
			        );
				)
			{
				preparedStatement.executeUpdate();
				
			} catch (SQLException e) {
				
				throw new DaoException( e );
				
			}
			
			return userDto;
		}else {
			throw new EntityNotFoundException("User Not found");
		}
	}

	@Override
	public void deleteUser(Integer id) {
		
		final String SQL_DESTROY = " Delete from user where user_id=? ";
		
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		
		try {
			
			connexion = daoFactory.getConnection();
	        preparedStatement = RequestPrepare.initRequestPrepare( 
	        		connexion, 
	        		SQL_DESTROY , 
	        		id 
	        	);
	        preparedStatement.execute();
			
		} catch (SQLException e) {
			
			throw new DaoException( e );
			
		}
		
	}

	@Override
	public List<UserDto> getAllUsers() {
		
		final String SQL_SELECT_ALL = " SELECT user_id , numero , ville , rue , email , nom , prenom , role_id , telephone , profile FROM user ";
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
		
	    UserDto userDto = null;
	    List<UserDto> users = new ArrayList<UserDto>();
	    
	    try {
	    	connexion = daoFactory.getConnection();
	        preparedStatement = RequestPrepare.initRequestPrepare( 
	        		connexion, 
	        		SQL_SELECT_ALL
	        	);
	        resultSet = preparedStatement.executeQuery();
	        
	        while ( resultSet.next() ) {
	            userDto = map( resultSet );
	            users.add(userDto);
	        }
		} catch (SQLException e) {
			
			throw new DaoException( e );
			
		}
	    
		return users;
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public UserDto getUserByEmail(String email) {
		
		final String SQL_SELECT_By_Email = " SELECT user_id , numero , ville , rue , email , nom , prenom , role_id , telephone , profile , password FROM user where email = ? ";
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    UserDto userDto = new UserDto();
	    
	    try {
	    	connexion = daoFactory.getConnection();
	        preparedStatement = RequestPrepare.initRequestPrepare( 
	        		connexion, 
	        		SQL_SELECT_By_Email , 
	        		email 
	        	);
	        resultSet = preparedStatement.executeQuery();
	        
	        while ( resultSet.next() ) {
	            
	        	userDto = map( resultSet );
	            userDto.setPassword( resultSet.getString( "password" ) );
	    		
	        }
		} catch (SQLException e) {
			
			throw new DaoException( e );
			
		}
	    
		
		return userDto;
	}
	

}
