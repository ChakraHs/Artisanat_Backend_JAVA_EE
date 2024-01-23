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
import com.Pf_Artis.dto.CommandeDto;
import com.Pf_Artis.dto.LigneCommandeDto;
import com.Pf_Artis.dto.ProduitDto;
import com.Pf_Artis.exception.EntityNotFoundException;
import com.Pf_Artis.models.LigneCommandeKey;
import com.Pf_Artis.service.facade.CommandeServiceInterface;
import com.Pf_Artis.service.facade.LigneCommandeServiceInterface;
import com.Pf_Artis.service.facade.ProduitServiceInterface;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LigneCommandeServiceImpl implements LigneCommandeServiceInterface {

	private DaoFactory daoFactory;
	
	private static LigneCommandeDto map( ResultSet resultSet ) throws SQLException {
		
		LigneCommandeDto ligneCommandeDto = new LigneCommandeDto();
		LigneCommandeKey ligneCommandeKey = new LigneCommandeKey();
		
		ligneCommandeKey.setCommandeId( resultSet.getInt( "commande_id" ) );
		ligneCommandeKey.setProduitId( resultSet.getInt( "produit_id" ) );
		
		ProduitServiceInterface produitService = new ProduitServiceImpl(DaoFactory.getInstance());
		ProduitDto produitDto = produitService.readProduit( resultSet.getInt( "produit_id" ) );
		
		CommandeServiceInterface commandeService = new CommandeServiceImpl( DaoFactory.getInstance() );
		CommandeDto commandeDto = commandeService.readCommande( resultSet.getInt( "commande_id" ) );
		
		ligneCommandeDto.setCommande(commandeDto);
		ligneCommandeDto.setProduit(produitDto);
		ligneCommandeDto.setId(ligneCommandeKey);
		ligneCommandeDto.setPrixUnitaire( resultSet.getDouble( "prixUnitaire" ) );
		ligneCommandeDto.setQuantite( resultSet.getDouble( "quantite" ) );
		
		return ligneCommandeDto;
	}
	
	@Override
	public LigneCommandeDto createLigneCommande( LigneCommandeDto ligneCommandeDto )throws NotFoundException {

		ProduitServiceInterface produitService = new ProduitServiceImpl(DaoFactory.getInstance());
		ProduitDto produitDto = produitService.readProduit( ligneCommandeDto.getProduit().getProduitId() );
		
		CommandeServiceInterface commandeService = new CommandeServiceImpl( DaoFactory.getInstance() );
		CommandeDto commandeDto = commandeService.readCommande( ligneCommandeDto.getCommande().getCommandeId() );
		
		if( commandeDto.getCommandeId() != null && produitDto.getProduitId()!=null ) {

			final String SQL_INSERT = "INSERT INTO line_commande ( commande_id , produit_id , prixUnitaire , quantite ) VALUES ( ? , ? , ? , ? ) ";
			
			Connection connexion = null;
			PreparedStatement preparedStatement = null;
		    
		    try {
		    	connexion = daoFactory.getConnection();
		    	
		    	preparedStatement = RequestPrepare.initRequestPrepare( connexion , SQL_INSERT , ligneCommandeDto.getId().getCommandeId() , ligneCommandeDto.getId().getProduitId() , ligneCommandeDto.getPrixUnitaire() , ligneCommandeDto.getQuantite() );
		        preparedStatement.executeUpdate();
		    	
			} catch (SQLException e) {
				throw new DaoException( e );
			}

			return ligneCommandeDto;
		}else {
			throw new EntityNotFoundException("Image Not found");
		}
	}

	@Override
	public LigneCommandeDto readLigneCommande( LigneCommandeKey ligneCommandeKey ) {
		
		final String SQL_SELECT_PAR_ID = "SELECT commande_id , produit_id , prixUnitaire , quantite FROM line_commande  WHERE commande_id = ? and produit_id = ? ";
		
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    LigneCommandeDto ligneCommandeDto = new LigneCommandeDto();
	    
	    try {
	    	
	    	connexion = daoFactory.getConnection();
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_PAR_ID, ligneCommandeKey.getCommandeId() , ligneCommandeKey.getProduitId() );
	        resultSet = preparedStatement.executeQuery();
	        
	        if ( resultSet.next() ) {
	        	
	        	ligneCommandeDto = map( resultSet );
	            
	        }
		} catch (SQLException e) {

			throw new DaoException( e );
			
		}
		return ligneCommandeDto ;
	}

	@Override
	public LigneCommandeDto updateLigneCommande(LigneCommandeDto ligneCommandeDto) throws NotFoundException {

		if(this.readLigneCommande(ligneCommandeDto.getId())!=null) {
			final String SQL_UPDATE = "UPDATE line_commande SET prixUnitaire = ? , quantite = ? where commande_id = ? and produit_id = ? ";
			
			Connection connexion = null;
			PreparedStatement preparedStatement = null;
			
			try {
				
				connexion = daoFactory.getConnection();
				
		        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_UPDATE  , ligneCommandeDto.getPrixUnitaire() , ligneCommandeDto.getQuantite() , ligneCommandeDto.getId().getCommandeId() , ligneCommandeDto.getId().getProduitId()  );
		        preparedStatement.executeUpdate();
				
			} catch (SQLException e) {
				
				throw new DaoException( e );
				
			}
			return ligneCommandeDto;
		}else {
			throw new EntityNotFoundException("Ligne Commande Not found");
		}
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
	public List<LigneCommandeDto> getAllLigneCommandes() {

		final String SQL_SELECT_ALL = " SELECT commande_id , produit_id , prixUnitaire , quantite FROM line_commande";
		
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
		
	    LigneCommandeDto ligneCommandeDto = new LigneCommandeDto();
	    List<LigneCommandeDto> ligneCommandes = new ArrayList<LigneCommandeDto>();
	    
	    try {
	    	connexion = daoFactory.getConnection();
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_ALL);
	        resultSet = preparedStatement.executeQuery();
	        
	        while ( resultSet.next() ) {
	        	ligneCommandeDto = map( resultSet );
	        	ligneCommandes.add(ligneCommandeDto);
	        }
		} catch (SQLException e) {
			
			throw new DaoException( e );
			
		}
		return ligneCommandes;
	}

}
