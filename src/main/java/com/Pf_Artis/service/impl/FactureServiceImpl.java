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
import com.Pf_Artis.dto.FactureDto;
import com.Pf_Artis.exception.EntityNotFoundException;
import com.Pf_Artis.service.facade.FactureServiceInterface;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FactureServiceImpl implements FactureServiceInterface {

	private DaoFactory daoFactory;

	private static FactureDto map( ResultSet resultSet ) throws SQLException {
		
		FactureDto facture = new FactureDto();
		
		facture.setFactureId( resultSet.getInt( "facture_id" ) );
		facture.setDate_facturation(resultSet.getDate("date_facturation"));
		facture.setMontant_total(resultSet.getLong("montant_total"));
		
		CommandeServiceImpl serviceImpl = new CommandeServiceImpl(DaoFactory.getInstance());
		CommandeDto commandeDto = serviceImpl.readCommande(resultSet.getInt("commande_id"));
		
		facture.setCommande(commandeDto);
		
		return facture;
	}
	
	@Override
	public FactureDto createFacture(FactureDto factureDto)throws NotFoundException {
		
		CommandeServiceImpl serviceImpl = new CommandeServiceImpl(DaoFactory.getInstance());
		CommandeDto commandeDto = serviceImpl.readCommande( factureDto.getCommande().getCommandeId() );
		
		if(commandeDto.getCommandeId() != null) {
			final String SQL_INSERT = "INSERT INTO facture ( date_facturation , montant_total , commande_id ) VALUES (  ? , ? , ? ) ";
			final String SQL_SELECT_MAX = " SELECT max(facture_id) as max_id from facture ";
			
			Connection connexion = null;
			PreparedStatement preparedStatement = null;
		    ResultSet resultSet = null;
		    
		    try {
		    	connexion = daoFactory.getConnection();
		    	
		    	preparedStatement = RequestPrepare.initRequestPrepare( connexion , SQL_INSERT , factureDto.getDate_facturation() , factureDto.getMontant_total() , factureDto.getCommande().getCommandeId() );
		        preparedStatement.executeUpdate();
		        
		        PreparedStatement ps2 = RequestPrepare.initRequestPrepare( connexion , SQL_SELECT_MAX );
		        resultSet = ps2.executeQuery();
		        
		        if(resultSet.next()) {
					
					factureDto.setFactureId(resultSet.getInt("max_id"));
					
				}
		    	
			} catch (SQLException e) {
				throw new DaoException( e );
			}
			
			return factureDto;
		}else {
			throw new EntityNotFoundException("Commande Not found");
		}
	}

	@Override
	public FactureDto readFacture(Integer id) {
		final String SQL_SELECT_PAR_ID = " SELECT facture_id , date_facturation , montant_total , commande_id FROM facture WHERE facture_id = ? ";
		
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    FactureDto factureDto = new FactureDto();
	    
	    try {
	    	
	    	connexion = daoFactory.getConnection();
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_PAR_ID, id );
	        resultSet = preparedStatement.executeQuery();
	        
	        if ( resultSet.next() ) {
	        	
	        	factureDto = map( resultSet );
	            
	        }
		} catch (SQLException e) {

			throw new DaoException( e );
			
		}
	    
		return factureDto;
	}

	@Override
	public FactureDto updateFacture(FactureDto factureDto) {
		
		final String SQL_UPDATE = "UPDATE facture SET date_facturation = ? , montant_total = ? where facture_id = ? ";
		
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		
		try {
			
			connexion = daoFactory.getConnection();
			
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_UPDATE  , factureDto.getDate_facturation() , factureDto.getMontant_total() , factureDto.getFactureId()  );
	        preparedStatement.executeUpdate();
			
		} catch (SQLException e) {
			
			throw new DaoException( e );
			
		}
		return factureDto;
	}

	@Override
	public void deleteFacture(Integer id) {
		
		final String SQL_DESTROY = " Delete from facture where facture_id=? ";
		
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
	public List<FactureDto> getAllFactures() {
		
		final String SQL_SELECT_ALL = "SELECT facture_id , date_facturation , montant_total , commande_id FROM facture";
		
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
		
	    FactureDto factureDto = new FactureDto();
	    List<FactureDto> factures = new ArrayList<FactureDto>();
	    
	    try {
	    	
	    	connexion = daoFactory.getConnection();
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_ALL);
	        resultSet = preparedStatement.executeQuery();
	        
	        while ( resultSet.next() ) {
	        	factureDto = map( resultSet );
	        	factures.add( factureDto );
	        }
		} catch (SQLException e) {
			throw new DaoException( e );	
		}
		return factures;
	}

}
