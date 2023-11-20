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
import com.Pf_Artis.models.Produit;
import com.Pf_Artis.models.Store;
import com.Pf_Artis.service.facade.ProduitServiceInterface;
import com.Pf_Artis.service.facade.StoreServiceInterface;

public class ProduitServiceImpl implements ProduitServiceInterface {

	private DaoFactory daoFactory;
	
	public ProduitServiceImpl(DaoFactory daoFactory) {
		super();
		this.daoFactory = daoFactory;
	}

	private static Produit map( ResultSet resultSet ) throws SQLException {
		
		Produit produit = new Produit();
		produit.setId( resultSet.getLong( "id" ) );
		produit.setNom( resultSet.getString( "nom" ) );
		produit.setDateFabrication( resultSet.getDate( "date_fabrication" ) );
		produit.setDatePeremption( resultSet.getDate( "date_peremption" ) );
		produit.setDescription( resultSet.getString( "description" ) );
		produit.setPoids( resultSet.getDouble( "poids" ) );
		produit.setPrix( resultSet.getDouble( "prix" ) );
		produit.setStock( resultSet.getInt( "stock" ) );
		
		StoreServiceInterface serviceInterface = new StoreServiceImpl( DaoFactory.getInstance() );
		Store store = serviceInterface.readStore( resultSet.getLong( "store_id" ) );
		
		produit.setStore(store);
		
		return produit;
	}
	
	@Override
	public Produit createProduit(Produit produit) {

		final String SQL_INSERT = "INSERT INTO produit ( date_fabrication , date_peremption , description , nom , poids , prix , stock , store_id ) VALUES (  ? , ? , ? , ? , ? , ? , ? , ? ) ";
		final String SQL_SELECT_MAX = " SELECT max(id) as max_id from produit ";
		
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    try {
	    	connexion = daoFactory.getConnection();
	    	
	    	preparedStatement = RequestPrepare.initRequestPrepare( connexion , SQL_INSERT , produit.getDateFabrication() , produit.getDatePeremption() , produit.getDescription() , produit.getNom() , produit.getPoids() , produit.getPrix() , produit.getStock() , produit.getStore().getId()  );
	        preparedStatement.executeUpdate();
	        
	        PreparedStatement ps2 = RequestPrepare.initRequestPrepare( connexion , SQL_SELECT_MAX );
	        resultSet = ps2.executeQuery();
	        
	        if(resultSet.next()) {
				
				produit.setId(resultSet.getLong("max_id"));
				
			}
	    	
		} catch (SQLException e) {
			throw new DaoException( e );
		}

		return produit;
	}

	@Override
	public Produit readProduit(Long id) {
		
		final String SQL_SELECT_PAR_ID = " SELECT id , date_fabrication , date_peremption , description , nom , poids , prix , stock , store_id FROM produit WHERE id = ? ";
		
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    Produit produit = new Produit();
	    
	    try {
	    	
	    	connexion = daoFactory.getConnection();
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_PAR_ID, id );
	        resultSet = preparedStatement.executeQuery();
	        
	        if ( resultSet.next() ) {
	        	
	        	produit = map( resultSet );
	            
	        }
		} catch (SQLException e) {

			throw new DaoException( e );
			
		}
		
		return produit;
	}

	@Override
	public Produit updateProduit(Produit produit) {

		final String SQL_UPDATE = "UPDATE produit SET date_fabrication = ? , date_peremption = ? , description = ?  , nom = ?  , poids = ?  , prix = ?  , stock = ? where id = ? ";
		
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		
		try {
			
			connexion = daoFactory.getConnection();
			
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_UPDATE  , produit.getDateFabrication() , produit.getDatePeremption() , produit.getDescription() , produit.getNom() , produit.getPoids() , produit.getPrix() , produit.getStock() , produit.getId() );
	        preparedStatement.executeUpdate();
			
		} catch (SQLException e) {
			
			throw new DaoException( e );
			
		}
		return produit;
	}

	@Override
	public void deleteProduit(Long id) {
		

		final String SQL_DESTROY = " Delete from produit where id=? ";
		
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
	public List<Produit> getAllProduits() {

		final String SQL_SELECT_ALL = "SELECT id , date_fabrication , date_peremption , description , nom , poids , prix , stock , store_id FROM produit";
		
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
		
	    Produit produit = new Produit();
	    List<Produit> produits = new ArrayList<Produit>();
	    
	    try {
	    	
	    	connexion = daoFactory.getConnection();
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_ALL);
	        resultSet = preparedStatement.executeQuery();
	        
	        while ( resultSet.next() ) {
	        	produit = map( resultSet );
	        	produits.add( produit );
	        }
		} catch (SQLException e) {
			throw new DaoException( e );	
		}
		return produits;
	}

}
