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
import com.Pf_Artis.models.User;
import com.Pf_Artis.service.facade.UserServiceInterface;




public class UserServiceImpl implements UserServiceInterface {
	
	private DaoFactory daoFactory;
	
	public UserServiceImpl( DaoFactory daoFactory ) {
		
		super();
		this.daoFactory = daoFactory;
		
	}
	private static User map( ResultSet resultSet ) throws SQLException {
		User user = new User();
		user.setId( resultSet.getLong( "id" ) );
		user.setNom( resultSet.getString( "nom" ) );
		user.setPrenom( resultSet.getString( "prenom" ) );
		user.setAdresse( resultSet.getString("adresse"));
		user.setTelephone( resultSet.getString("telephone") );
		user.setRole( resultSet.getString("role") );
		user.setEmail( resultSet.getString("email") );
		user.setPassword( resultSet.getString("password") );
		user.setProfile( resultSet.getString( "profile" ) );
		return user;
	}
	
	@Override
	public User createUser(User user) {
		
		final String SQL_INSERT = "INSERT INTO user ( adresse , email , nom , password , prenom , role , telephone , profile ) VALUES (  ? , ? , ? , ? , ? , ? , ? , ? ) ";
		final String SQL_SELECT_MAX = " SELECT max(id) as max_id from user ";
		
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    try {
	    	connexion = daoFactory.getConnection();
	    	
	    	preparedStatement = RequestPrepare.initRequestPrepare( connexion , SQL_INSERT , user.getAdresse() , user.getEmail() , user.getNom() , user.getPassword() , user.getPrenom() , user.getRole() , user.getTelephone() , user.getProfile() );
	        preparedStatement.executeUpdate();
	        
	        PreparedStatement ps2 = RequestPrepare.initRequestPrepare( connexion , SQL_SELECT_MAX );
	        resultSet = ps2.executeQuery();
	        
	        if(resultSet.next()) {
				
				user.setId(resultSet.getLong("max_id"));
				
			}
	    	
		} catch (SQLException e) {
			throw new DaoException( e );
		}
		
		return user;
	}

	@Override
	public User readUser(Long id) {
		
		final String SQL_SELECT_PAR_ID = "SELECT id , adresse , email , nom , password , prenom , role , telephone , profile FROM user  WHERE id = ?";
		
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    User user = new User();
	    
	    try {
	    	
	    	connexion = daoFactory.getConnection();
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_PAR_ID, id );
	        resultSet = preparedStatement.executeQuery();
	        
	        if ( resultSet.next() ) {
	        	
	        	user = map( resultSet );
	            
	        }
		} catch (SQLException e) {

			throw new DaoException( e );
			
		}
		return user;
	}

	@Override
	public User updateUser(User user) {
		
		final String SQL_UPDATE = "UPDATE user SET adresse = ? , email = ? , nom = ? , password = ? , prenom = ? , role = ? , telephone = ? , profile = ? where id = ? ";
		
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		
		try {
			
			connexion = daoFactory.getConnection();
			
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_UPDATE  , user.getAdresse() , user.getEmail() , user.getNom() , user.getPassword() , user.getPrenom() , user.getRole() , user.getTelephone() , user.getProfile() ,user.getId()  );
	        preparedStatement.executeUpdate();
			
		} catch (SQLException e) {
			
			throw new DaoException( e );
			
		}
		
		return user;
	}

	@Override
	public void deleteUser(Long id) {
		
		final String SQL_DESTROY = " Delete from user where id=? ";
		
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
	public List<User> getAllUsers() {
		
		final String SQL_SELECT_ALL = " SELECT id , adresse , email , nom , password , prenom , role , telephone , profile FROM user ";
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
		
	    User user = null;
	    List<User> users = new ArrayList<User>();
	    
	    try {
	    	connexion = daoFactory.getConnection();
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_ALL);
	        resultSet = preparedStatement.executeQuery();
	        
	        while ( resultSet.next() ) {
	            user = map( resultSet );
	            users.add(user);
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
	

}
