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
import com.Pf_Artis.models.Commande;
import com.Pf_Artis.models.User;
import com.Pf_Artis.service.facade.CommandeServiceInterface;
import com.Pf_Artis.service.facade.UserServiceInterface;

public class CommandeServiceImpl implements CommandeServiceInterface {

	private DaoFactory daoFactory;
	
	public CommandeServiceImpl(DaoFactory daoFactory) {
		super();
		this.daoFactory = daoFactory;
	}

	private static Commande map( ResultSet resultSet ) throws SQLException {
		Commande commande = new Commande();
		
		commande.setId( resultSet.getLong( "id" ) );
		commande.setDateCommande( resultSet.getDate( "date_commande" ) );
		
		UserServiceInterface serviceInterface = new UserServiceImpl(DaoFactory.getInstance());
		User user = serviceInterface.readUser(resultSet.getLong("client_id"));
		
		commande.setClient(user);
		return commande;
	}
	
	@Override
	public Commande createCommande(Commande commande) {
		
		final String SQL_INSERT = "INSERT INTO commande ( date_commande , client_id ) VALUES (  ? , ? ) ";
		final String SQL_SELECT_MAX = " SELECT max(id) as max_id from commande ";
		
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    try {
	    	connexion = daoFactory.getConnection();
	    	
	    	preparedStatement = RequestPrepare.initRequestPrepare( connexion , SQL_INSERT , commande.getDateCommande() , commande.getClient().getId() );
	        preparedStatement.executeUpdate();
	        
	        PreparedStatement ps2 = RequestPrepare.initRequestPrepare( connexion , SQL_SELECT_MAX );
	        resultSet = ps2.executeQuery();
	        
	        if(resultSet.next()) {
				
				commande.setId(resultSet.getLong("max_id"));
				
			}
	    	
		} catch (SQLException e) {
			throw new DaoException( e );
		}
	    
		return commande	;
	}

	@Override
	public Commande readCommande(Long id) {
		
		final String SQL_SELECT_PAR_ID = "SELECT id , date_commande , client_id FROM commande WHERE id = ? ";
		
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    Commande commande = new Commande();
	    
	    try {
	    	
	    	connexion = daoFactory.getConnection();
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_PAR_ID, id );
	        resultSet = preparedStatement.executeQuery();
	        
	        if ( resultSet.next() ) {
	        	
	        	commande = map( resultSet );
	            
	        }
		} catch (SQLException e) {

			throw new DaoException( e );
			
		}
		
		return commande;
	}

	@Override
	public Commande updateCommande(Commande commande) {
		
		final String SQL_UPDATE = "UPDATE commande SET date_commande = ? where id = ? ";
		
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		
		try {
			
			connexion = daoFactory.getConnection();
			
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_UPDATE  , commande.getDateCommande() , commande.getId()  );
	        preparedStatement.executeUpdate();
			
		} catch (SQLException e) {
			
			throw new DaoException( e );
			
		}
		
		return commande;
	}

	@Override
	public void deleteCommande(Long id) {
		
		final String SQL_DESTROY = " Delete from commande where id=? ";
		
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
	public List<Commande> getAllCommandes() {
		
		final String SQL_SELECT_ALL = " SELECT id , date_commande , client_id FROM commande";
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
		
	    Commande commande = null;
	    List<Commande> commandes = new ArrayList<Commande>();
	    
	    try {
	    	
	    	connexion = daoFactory.getConnection();
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_ALL);
	        resultSet = preparedStatement.executeQuery();
	        
	        while ( resultSet.next() ) {
	            commande = map( resultSet );
	            commandes.add(commande);
	        }
		} catch (SQLException e) {
			throw new DaoException( e );	
		}
		return commandes;
	}

}
