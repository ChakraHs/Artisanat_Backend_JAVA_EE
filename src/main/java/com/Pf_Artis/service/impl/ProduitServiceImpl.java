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
import com.Pf_Artis.dto.CategoryDto;
import com.Pf_Artis.dto.CategoryProduitDto;
import com.Pf_Artis.dto.ImageDto;
import com.Pf_Artis.dto.ProduitDto;
import com.Pf_Artis.dto.StoreDto;
import com.Pf_Artis.exception.EntityNotFoundException;
import com.Pf_Artis.service.facade.CategoryProduitService;
import com.Pf_Artis.service.facade.ImageServiceInterface;
import com.Pf_Artis.service.facade.ProduitServiceInterface;
import com.Pf_Artis.service.facade.StoreServiceInterface;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProduitServiceImpl implements ProduitServiceInterface {

	private DaoFactory daoFactory;

	private static ProduitDto map( ResultSet resultSet ) throws SQLException {
		
		ProduitDto produitDto = new ProduitDto();
		produitDto.setProduitId( resultSet.getInt( "produit_id" ) );
		produitDto.setNom( resultSet.getString( "nom" ) );
		produitDto.setDate_fabrication( resultSet.getDate( "date_fabrication" ) );
		produitDto.setDate_peremption( resultSet.getDate( "date_peremption" ) );
		produitDto.setDescription( resultSet.getString( "description" ) );
		produitDto.setPoids( resultSet.getDouble( "poids" ) );
		produitDto.setPrix( resultSet.getDouble( "prix" ) );
		produitDto.setStock( resultSet.getInt( "stock" ) );
		
		StoreServiceInterface serviceInterface = new StoreServiceImpl( DaoFactory.getInstance() );
		StoreDto storeDto = serviceInterface.readStore( resultSet.getInt( "store_id" ) );
		
		ImageServiceInterface imageService = new ImageServiceImpl( DaoFactory.getInstance() );
		List<ImageDto> images = imageService.getImagesByProduit(resultSet.getInt( "produit_id" ));
		
		produitDto.setStore(storeDto);
		produitDto.setImages(images);
		
		return produitDto;
	}
	
	@Override
	public ProduitDto createProduit(ProduitDto produitDto)throws NotFoundException {

		final String SQL_INSERT = "INSERT INTO produit ( date_fabrication , date_peremption , description , nom , poids , prix , stock , store_id ) VALUES (  ? , ? , ? , ? , ? , ? , ? , ? ) ";

		StoreServiceInterface serviceInterface = new StoreServiceImpl( DaoFactory.getInstance() );
		StoreDto storeDto = serviceInterface.readStore( produitDto.getStore().getStoreId() );
		
		if(storeDto.getStoreId()!=null) {
		    
		    try (
		    		Connection connexion = daoFactory.getConnection();
					PreparedStatement preparedStatement = RequestPrepare.initRequestPrepare( 
		    			connexion , 
		    			SQL_INSERT , 
		    			produitDto.getDate_fabrication() , 
		    			produitDto.getDate_peremption() , 
		    			produitDto.getDescription() , 
		    			produitDto.getNom() , 
		    			produitDto.getPoids() , 
		    			produitDto.getPrix() , 
		    			produitDto.getStock() , 
		    			produitDto.getStore().getStoreId()  
		    		);
		    	)
		    {
		        preparedStatement.executeUpdate();
		        
		        ProduitDto saved = getLastProduits();
		        
		        for(CategoryDto categoryDto : produitDto.getCategorys()) {
		        	
		        	CategoryProduitDto categoryProduitDto = new CategoryProduitDto();
		        	categoryProduitDto.setCategory(categoryDto);
		        	categoryProduitDto.setProduit(saved);
		        	
		        	CategoryProduitService categoryProduitService = new CategoryProduitServiceImpl(DaoFactory.getInstance());
		        	categoryProduitService.save(categoryProduitDto);
		        	
		        }
		        
			
		    } catch (SQLException e) {
				throw new DaoException( e );
			}
		    
			return getLastProduits();
		}else {
			
			throw new EntityNotFoundException("Store Not found");
			
		}
	}

	
	@Override
	public ProduitDto readProduit(Integer id) {
		
		final String SQL_SELECT_PAR_ID = " SELECT produit_id , date_fabrication , date_peremption , description , nom , poids , prix , stock , store_id FROM produit WHERE produit_id = ? ";
	    
	    
	    ProduitDto produitDto = new ProduitDto();
	    
	    try (
	    		Connection connexion = daoFactory.getConnection();
	    	    PreparedStatement preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_PAR_ID, id );
	    	    ResultSet resultSet = preparedStatement.executeQuery();
	    	)
	    {
	    	
	        if ( resultSet.next() ) {
	        	
	        	produitDto = map( resultSet );
	        	
	            
	        }
		} catch (SQLException e) {

			throw new DaoException( e );
			
		}
		
		return produitDto;
	}

	@Override
	public ProduitDto updateProduit(ProduitDto produitDto) throws NotFoundException {

		if(this.readProduit(produitDto.getProduitId())!=null) {

			final String SQL_UPDATE = "UPDATE produit SET date_fabrication = ? , date_peremption = ? , description = ?  , nom = ?  , poids = ?  , prix = ?  , stock = ? where produit_id = ? ";
			
			try (
					Connection connexion = daoFactory.getConnection();
					PreparedStatement preparedStatement = RequestPrepare.initRequestPrepare( 
						connexion, 
						SQL_UPDATE  , 
						produitDto.getDate_fabrication() , 
						produitDto.getDate_peremption() , 
						produitDto.getDescription() , 
						produitDto.getNom() , 
						produitDto.getPoids() , 
						produitDto.getPrix() , 
						produitDto.getStock() , 
						produitDto.getProduitId() 
					);
				)
			{
				
		        preparedStatement.executeUpdate();
				
			} catch (SQLException e) {
				
				throw new DaoException( e );
				
			}
			return produitDto;
		}else {
			throw new EntityNotFoundException("Image Not found");
		}
	}

	@Override
	public void deleteProduit(Integer id) {
		
		final String SQL_DESTROY = " Delete from produit where produit_id=? ";
		
		try (
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
	public List<ProduitDto> getAllProduits() {

		final String SQL_SELECT_ALL = "SELECT produit_id , date_fabrication , date_peremption , description , nom , poids , prix , stock , store_id FROM produit";
		
	    ProduitDto produitDto = new ProduitDto();
	    List<ProduitDto> produits = new ArrayList<ProduitDto>();
	    
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
	        	produitDto = map( resultSet );
	        	produits.add( produitDto );
	        }
		} catch (SQLException e) {
			throw new DaoException( e );	
		}
		return produits;
	}

	
	@Override
	public ProduitDto getLastProduits() {
		
		final String SQL_SELECT_MAX = " SELECT max(produit_id) as max_id from produit ";
		
	    Integer produitId = null ;
	    try (
	    		Connection connexion = daoFactory.getConnection();
	    	    PreparedStatement preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_MAX );
	    	    ResultSet resultSet = preparedStatement.executeQuery();	
	    	)
	    {
	        
	        if ( resultSet.next() ) {
	        	
	        	produitId = resultSet.getInt("max_id");
	            
	        }
		} catch (SQLException e) {

			throw new DaoException( e );
			
		}
	    return readProduit(produitId);
	}

}
