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
import com.Pf_Artis.models.LigneCommande;
import com.Pf_Artis.models.LigneCommandeKey;
import com.Pf_Artis.models.Produit;
import com.Pf_Artis.service.facade.CommandeServiceInterface;
import com.Pf_Artis.service.facade.LigneCommandeServiceInterface;
import com.Pf_Artis.service.facade.ProduitServiceInterface;

public class LigneCommandeServiceImpl implements LigneCommandeServiceInterface {

	private DaoFactory daoFactory;
	
	public LigneCommandeServiceImpl(DaoFactory daoFactory) {
		super();
		this.daoFactory = daoFactory;
	}
	
	private static LigneCommande map( ResultSet resultSet ) throws SQLException {
		LigneCommande ligneCommande = new LigneCommande();
		
		LigneCommandeKey ligneCommandeKey = new LigneCommandeKey();
		
		ligneCommandeKey.setCommandeId( resultSet.getLong( "commande_id" ) );
		ligneCommandeKey.setProduitId( resultSet.getLong( "produit_id" ) );
		
		ProduitServiceInterface produitService = new ProduitServiceImpl(DaoFactory.getInstance());
		Produit produit = produitService.readProduit( resultSet.getLong( "produit_id" ) );
		
		CommandeServiceInterface commandeService = new CommandeServiceImpl( DaoFactory.getInstance() );
		Commande commande = commandeService.readCommande( resultSet.getLong( "commande_id" ) );
		
		ligneCommande.setCommande(commande);
		ligneCommande.setProduit(produit);
		ligneCommande.setId(ligneCommandeKey);
		ligneCommande.setPrixUnitaire( resultSet.getDouble( "prixUnitaire" ) );
		ligneCommande.setQuantite( resultSet.getDouble( "quantite" ) );
		
		return ligneCommande;
	}
	
	@Override
	public LigneCommande createLigneCommande(LigneCommande ligneCommande) {

		final String SQL_INSERT = "INSERT INTO line_commande ( commande_id , produit_id , prixUnitaire , quantite ) VALUES ( ? , ? , ? , ? ) ";
		
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
	    
	    try {
	    	connexion = daoFactory.getConnection();
	    	
	    	preparedStatement = RequestPrepare.initRequestPrepare( connexion , SQL_INSERT , ligneCommande.getId().getCommandeId() , ligneCommande.getId().getProduitId() , ligneCommande.getPrixUnitaire() , ligneCommande.getQuantite() );
	        preparedStatement.executeUpdate();
	    	
		} catch (SQLException e) {
			throw new DaoException( e );
		}

		return ligneCommande;
	}

	@Override
	public LigneCommande readLigneCommande( LigneCommandeKey ligneCommandeKey ) {
		
		final String SQL_SELECT_PAR_ID = "SELECT commande_id , produit_id , prixUnitaire , quantite FROM line_commande  WHERE commande_id = ? and produit_id = ? ";
		
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    LigneCommande ligneCommande = new LigneCommande();
	    
	    try {
	    	
	    	connexion = daoFactory.getConnection();
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_PAR_ID, ligneCommandeKey.getCommandeId() , ligneCommandeKey.getProduitId() );
	        resultSet = preparedStatement.executeQuery();
	        
	        if ( resultSet.next() ) {
	        	
	        	ligneCommande = map( resultSet );
	            
	        }
		} catch (SQLException e) {

			throw new DaoException( e );
			
		}
		return ligneCommande ;
	}

	@Override
	public LigneCommande updateLigneCommande(LigneCommande ligneCommande) {

		final String SQL_UPDATE = "UPDATE line_commande SET prixUnitaire = ? , quantite = ? where commande_id = ? and produit_id = ? ";
		
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		
		try {
			
			connexion = daoFactory.getConnection();
			
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_UPDATE  , ligneCommande.getPrixUnitaire() , ligneCommande.getQuantite() , ligneCommande.getId().getCommandeId() , ligneCommande.getId().getProduitId()  );
	        preparedStatement.executeUpdate();
			
		} catch (SQLException e) {
			
			throw new DaoException( e );
			
		}
		return ligneCommande;
	}

	@Override
	public void deleteLigneCommande( LigneCommandeKey ligneCommandeKey ) {

		final String SQL_DESTROY = " Delete from line_commande where commande_id = ? and produit_id = ? ";
		
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		
		try {
			
			connexion = daoFactory.getConnection();
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_DESTROY , ligneCommandeKey.getCommandeId() , ligneCommandeKey.getProduitId() );
	        preparedStatement.execute();
			
		} catch (SQLException e) {
			
			throw new DaoException( e );
			
		}
		
	}

	@Override
	public List<LigneCommande> getAllLigneCommandes() {

		final String SQL_SELECT_ALL = " SELECT commande_id , produit_id , prixUnitaire , quantite FROM line_commande";
		
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
		
	    LigneCommande ligneCommande = new LigneCommande();
	    List<LigneCommande> ligneCommandes = new ArrayList<LigneCommande>();
	    
	    try {
	    	connexion = daoFactory.getConnection();
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_ALL);
	        resultSet = preparedStatement.executeQuery();
	        
	        while ( resultSet.next() ) {
	        	ligneCommande = map( resultSet );
	        	ligneCommandes.add(ligneCommande);
	        }
		} catch (SQLException e) {
			
			throw new DaoException( e );
			
		}
		return ligneCommandes;
	}

}
