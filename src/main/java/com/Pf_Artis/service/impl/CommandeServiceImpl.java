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
import com.Pf_Artis.dto.CommandeByArtisanDto;
import com.Pf_Artis.dto.CommandeDto;
import com.Pf_Artis.dto.ProduitByDateDto;
import com.Pf_Artis.dto.UserDto;
import com.Pf_Artis.exception.EntityNotFoundException;
import com.Pf_Artis.service.facade.CommandeServiceInterface;
import com.Pf_Artis.service.facade.UserServiceInterface;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CommandeServiceImpl implements CommandeServiceInterface {

	private DaoFactory daoFactory;

	private static CommandeDto map( ResultSet resultSet ) throws SQLException {
		
		CommandeDto commandeDto = new CommandeDto();
		
		commandeDto.setCommandeId( resultSet.getInt( "commande_id" ) );
		commandeDto.setDate_commande( resultSet.getDate( "date_commande" ) );
		
		UserServiceInterface serviceInterface = new UserServiceImpl(DaoFactory.getInstance());
		UserDto userDto = serviceInterface.readUser(resultSet.getInt("client_id"));
		
		commandeDto.setUser(userDto);
		
		return commandeDto;
	}
	
	@Override
	public CommandeDto createCommande(CommandeDto commandeDto) throws NotFoundException {
		
		UserServiceInterface serviceInterface = new UserServiceImpl(DaoFactory.getInstance());
		UserDto userDto = serviceInterface.readUser(commandeDto.getUser().getUserId());
		
		if(userDto.getUserId()!=null) {
			final String SQL_INSERT = "INSERT INTO commande ( date_commande , client_id ) VALUES (  ? , ? ) ";
			final String SQL_SELECT_MAX = " SELECT max(commande_id) as max_id from commande ";
			
			Connection connexion = null;
			PreparedStatement preparedStatement = null;
		    ResultSet resultSet = null;
		    
		    try {
		    	connexion = daoFactory.getConnection();
		    	
		    	preparedStatement = RequestPrepare.initRequestPrepare( connexion , SQL_INSERT , commandeDto.getDate_commande() , commandeDto.getUser().getUserId() );
		        preparedStatement.executeUpdate();
		        
		        PreparedStatement ps2 = RequestPrepare.initRequestPrepare( connexion , SQL_SELECT_MAX );
		        resultSet = ps2.executeQuery();
		        
		        if(resultSet.next()) {
					
		        	commandeDto.setCommandeId(resultSet.getInt("max_id"));
					
				}
		    	
			} catch (SQLException e) {
				throw new DaoException( e );
			}
		    return commandeDto	;
		}else {
			throw new EntityNotFoundException("Client Not found");
		}
		
	    
		
	}

	@Override
	public CommandeDto readCommande(Integer id) {
		
		final String SQL_SELECT_PAR_ID = "SELECT commande_id , date_commande , client_id FROM commande WHERE commande_id = ? ";
		
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    CommandeDto commandeDto = new CommandeDto();
	    
	    try {
	    	
	    	connexion = daoFactory.getConnection();
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_PAR_ID, id );
	        resultSet = preparedStatement.executeQuery();
	        
	        if ( resultSet.next() ) {
	        	
	        	commandeDto = map( resultSet );
	            
	        }
		} catch (SQLException e) {

			throw new DaoException( e );
			
		}
		
		return commandeDto;
	}

	@Override
	public CommandeDto updateCommande(CommandeDto commandeDto) throws NotFoundException {
		
		if(this.readCommande(commandeDto.getCommandeId())!=null) {

			final String SQL_UPDATE = "UPDATE commande SET date_commande = ? where commande_id = ? ";
			
			Connection connexion = null;
			PreparedStatement preparedStatement = null;
			
			try {
				
				connexion = daoFactory.getConnection();
				
		        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_UPDATE  , commandeDto.getDate_commande() , commandeDto.getCommandeId()  );
		        preparedStatement.executeUpdate();
				
			} catch (SQLException e) {
				
				throw new DaoException( e );
				
			}
			
			return commandeDto;
		}else {
			throw new EntityNotFoundException("Commande Not found");
		}
	}

	@Override
	public void deleteCommande(Integer id) {
		
		final String SQL_DESTROY = " Delete from commande where commande_id=? ";
		
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
	public List<CommandeDto> getAllCommandes() {
		
		final String SQL_SELECT_ALL = " SELECT commande_id , date_commande , client_id FROM commande";
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
		
	    CommandeDto commandeDto = null;
	    List<CommandeDto> commandes = new ArrayList<CommandeDto>();
	    
	    try {
	    	
	    	connexion = daoFactory.getConnection();
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_ALL);
	        resultSet = preparedStatement.executeQuery();
	        
	        while ( resultSet.next() ) {
	        	commandeDto = map( resultSet );
	            commandes.add(commandeDto);
	        }
		} catch (SQLException e) {
			throw new DaoException( e );	
		}
		return commandes;
	}
	
	@Override
	public List<ProduitByDateDto> getNbreProduitAchatByArtisan(Integer id){
		
		final String SQL_SELECT = " SELECT c.date_commande, SUM(lc.quantite) AS nombre_achats "
				+ "FROM commande c "
				+ "JOIN line_commande lc ON c.commande_id = lc.commande_id "
				+ "JOIN produit p ON lc.produit_id = p.produit_id "
				+ "JOIN store s ON p.store_id = s.store_id "
				+ "JOIN user a ON s.artisant_id = a.user_id "
				+ "WHERE a.user_id = ? "
				+ "GROUP BY c.date_commande "
				+ "ORDER BY c.date_commande;";
		
		List<ProduitByDateDto> dtos = new ArrayList<ProduitByDateDto>();
		
		try (
				Connection connexion = daoFactory.getConnection();
				PreparedStatement preparedStatement = RequestPrepare.initRequestPrepare( 
					connexion, 
					SQL_SELECT,
					id
				);
			    ResultSet resultSet = preparedStatement.executeQuery();
			){
			
			while(resultSet.next()) {
				
				ProduitByDateDto dto = new ProduitByDateDto();
				
				dto.setDateAchat(resultSet.getDate( "c.date_commande" ));
				dto.setNbreAchats(resultSet.getInt( "nombre_achats" ));
				dtos.add(dto);
				
			}
			
		} catch (SQLException e) {
			throw new DaoException( e );	
		}
		
		return dtos;
	}
	
	@Override
	public List<CommandeByArtisanDto> getCommandeByArtisan(Integer artisan_id){
		
		final String SQL_SELECT = "SELECT c.commande_id , c.date_commande , p.nom "
				+ "FROM commande c "
				+ "JOIN line_commande lc ON c.commande_id = lc.commande_id "
				+ "JOIN produit p ON lc.produit_id = p.produit_id "
				+ "JOIN store s ON p.store_id = s.store_id "
				+ "JOIN user a ON s.artisant_id = a.user_id "
				+ "WHERE a.user_id = ? ;";
		
		List<CommandeByArtisanDto> dtos = new ArrayList<CommandeByArtisanDto>();
		
		try (
				Connection connexion = daoFactory.getConnection();
				PreparedStatement preparedStatement = RequestPrepare.initRequestPrepare( 
					connexion, 
					SQL_SELECT,
					artisan_id
				);
			    ResultSet resultSet = preparedStatement.executeQuery();
			){
			
			while(resultSet.next()) {
				
				CommandeByArtisanDto dto = new CommandeByArtisanDto();
				
				dto.setDate_commande(resultSet.getDate( "c.date_commande" ));
				dto.setNomProduit(resultSet.getString("p.nom"));
				dto.setCommandeId(resultSet.getInt("c.commande_id"));
				
				dtos.add(dto);
				
			}
		} catch (SQLException e) {
			throw new DaoException( e );	
		}
		
		return dtos;
	}

}
