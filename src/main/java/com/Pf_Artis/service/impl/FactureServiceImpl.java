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
import com.Pf_Artis.models.Facture;
import com.Pf_Artis.service.facade.FactureServiceInterface;

public class FactureServiceImpl implements FactureServiceInterface {

	private DaoFactory daoFactory;
	
	public FactureServiceImpl(DaoFactory daoFactory) {
		super();
		this.daoFactory = daoFactory;
	}

	private static Facture map( ResultSet resultSet ) throws SQLException {
		Facture facture = new Facture();
		facture.setId( resultSet.getLong( "id" ) );
		facture.setDateFacturation(resultSet.getDate("date_facturation"));
		facture.setMontantTotal(resultSet.getLong("montant_total"));
		
		CommandeServiceImpl serviceImpl = new CommandeServiceImpl(DaoFactory.getInstance());
		Commande commande = serviceImpl.readCommande(resultSet.getLong("commande_id"));
		
		facture.setCommande(commande);
		
		return facture;
	}
	
	@Override
	public Facture createFacture(Facture facture) {
		final String SQL_INSERT = "INSERT INTO facture ( date_facturation , montant_total , commande_id ) VALUES (  ? , ? , ? ) ";
		final String SQL_SELECT_MAX = " SELECT max(id) as max_id from facture ";
		
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    try {
	    	connexion = daoFactory.getConnection();
	    	
	    	preparedStatement = RequestPrepare.initRequestPrepare( connexion , SQL_INSERT , facture.getDateFacturation() , facture.getMontantTotal() , facture.getCommande().getId() );
	        preparedStatement.executeUpdate();
	        
	        PreparedStatement ps2 = RequestPrepare.initRequestPrepare( connexion , SQL_SELECT_MAX );
	        resultSet = ps2.executeQuery();
	        
	        if(resultSet.next()) {
				
				facture.setId(resultSet.getLong("max_id"));
				
			}
	    	
		} catch (SQLException e) {
			throw new DaoException( e );
		}
		
		return facture;
	}

	@Override
	public Facture readFacture(Long id) {
		final String SQL_SELECT_PAR_ID = " SELECT id , date_facturation , montant_total , commande_id FROM facture WHERE id = ? ";
		
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    Facture facture = new Facture();
	    
	    try {
	    	
	    	connexion = daoFactory.getConnection();
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_PAR_ID, id );
	        resultSet = preparedStatement.executeQuery();
	        
	        if ( resultSet.next() ) {
	        	
	        	facture = map( resultSet );
	            
	        }
		} catch (SQLException e) {

			throw new DaoException( e );
			
		}
	    
		return facture;
	}

	@Override
	public Facture updateFacture(Facture facture) {
		
		final String SQL_UPDATE = "UPDATE facture SET date_facturation = ? , montant_total = ? where id = ? ";
		
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		
		try {
			
			connexion = daoFactory.getConnection();
			
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_UPDATE  , facture.getDateFacturation() , facture.getMontantTotal() , facture.getId()  );
	        preparedStatement.executeUpdate();
			
		} catch (SQLException e) {
			
			throw new DaoException( e );
			
		}
		return facture;
	}

	@Override
	public void deleteFacture(Long id) {
		
		final String SQL_DESTROY = " Delete from facture where id=? ";
		
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
	public List<Facture> getAllFactures() {
		
		final String SQL_SELECT_ALL = "SELECT id , date_facturation , montant_total , commande_id FROM facture";
		
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
		
	    Facture facture = new Facture();
	    List<Facture> factures = new ArrayList<Facture>();
	    
	    try {
	    	
	    	connexion = daoFactory.getConnection();
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_ALL);
	        resultSet = preparedStatement.executeQuery();
	        
	        while ( resultSet.next() ) {
	        	facture = map( resultSet );
	        	factures.add( facture );
	        }
		} catch (SQLException e) {
			throw new DaoException( e );	
		}
		return factures;
	}

}
