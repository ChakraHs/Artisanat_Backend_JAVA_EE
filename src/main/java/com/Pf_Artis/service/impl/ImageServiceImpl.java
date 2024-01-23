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
import com.Pf_Artis.dto.ImageDto;
import com.Pf_Artis.exception.EntityNotFoundException;
import com.Pf_Artis.service.facade.ImageServiceInterface;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ImageServiceImpl implements ImageServiceInterface {

	private DaoFactory daoFactory;

	private static ImageDto map( ResultSet resultSet ) throws SQLException {
		
		ImageDto imageDto = new ImageDto();
		
		imageDto.setImageId( resultSet.getInt( "image_id" ) );
		imageDto.setPath( resultSet.getString( "path" ) );
		
		return imageDto;
		
	}
	
	@Override
	public ImageDto createImage( ImageDto imageDto ) throws NotFoundException{
		
		final String SQL_INSERT = "INSERT INTO image ( path , produit_id ) VALUES (  ? , ? ) ";
	    
	    try (
	    		Connection connexion = daoFactory.getConnection();
	    		PreparedStatement preparedStatement = RequestPrepare.initRequestPrepare( 
	    			connexion , 
	    			SQL_INSERT , 
	    			imageDto.getPath() , 
	    			imageDto.getProduit().getProduitId() 
	    		);
	    	)
	    {
	    	preparedStatement.executeUpdate();
	        
		} catch (SQLException e) {
			throw new DaoException( e );
		}
		
		return getLastImages();
	}

	@Override
	public ImageDto readImage(Integer id) {
		
		final String SQL_SELECT_PAR_ID = "SELECT image_id , path , produit_id FROM image WHERE image_id = ?";
		
	    ImageDto imageDto = new ImageDto();
	    
	    try (
	    		Connection connexion = daoFactory.getConnection();
	    	    PreparedStatement preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_PAR_ID, id );
	    	    ResultSet resultSet = preparedStatement.executeQuery();
	    	)
	    {
	        
	        if ( resultSet.next() ) {
	        	
	        	imageDto = map( resultSet );
	            
	        }
	        
		} catch (SQLException e) {

			throw new DaoException( e );
			
		}
	    
		return imageDto;
	}

	@Override
	public ImageDto updateImage(ImageDto imageDto) throws NotFoundException {

		ImageDto dto = this.readImage(imageDto.getImageId());
		
		if ( dto.getImageId() !=null) {
			final String SQL_UPDATE = "UPDATE image SET path = ? where image_id = ? ";
			
			try (
					Connection connexion = daoFactory.getConnection();
					PreparedStatement preparedStatement = RequestPrepare.initRequestPrepare(
						connexion, 
						SQL_UPDATE, 
						imageDto.getPath(),
						imageDto.getImageId()
					);
				)
			{
				
				preparedStatement.executeUpdate();

			} catch (SQLException e) {
				
				throw new DaoException( e );
				
			}
			return imageDto;
		}else {
			throw new EntityNotFoundException("Image Not found");
		}
	}

	@Override
	public void deleteImage(Integer id) {

		final String SQL_DESTROY = " Delete from image where image_id=? ";
		
		try (
				Connection connexion = daoFactory.getConnection();
				PreparedStatement preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_DESTROY , id );
			)
		{
			
	        preparedStatement.execute();
			
		} catch (SQLException e) {
			
			throw new DaoException( e );
			
		}
	}

	@Override
	public List<ImageDto> getAllImages() {
		
		final String SQL_SELECT_ALL = "SELECT image_id , path , produit_id FROM image";

	    ImageDto imageDto = new ImageDto();
	    List<ImageDto> images = new ArrayList<ImageDto>();
	    
	    try (
	    		Connection connexion = daoFactory.getConnection();
	    		PreparedStatement preparedStatement = RequestPrepare.initRequestPrepare(connexion, SQL_SELECT_ALL );
	    	    ResultSet resultSet = preparedStatement.executeQuery();
	    	)
	    
	    {
	    	
	    	while ( resultSet.next() ) {
	            imageDto = map( resultSet );
	            images.add(imageDto);
	        }
	    	
		} catch (SQLException e) {
			
			throw new DaoException( e );
			
		}
		
		return images;
	}

	
	@Override
	public List<ImageDto> getImagesByProduit(Integer produitId) {
		
		final String SQL_SELECT_BY_PRODUIT = "SELECT image_id , path , produit_id FROM image where produit_id = ? ";
	    
	    ImageDto imageDto = new ImageDto();
	    List<ImageDto> images = new ArrayList<ImageDto>();
	    
	    try (
	    		Connection connexion = daoFactory.getConnection();
	    		PreparedStatement preparedStatement = RequestPrepare.initRequestPrepare(connexion, SQL_SELECT_BY_PRODUIT , produitId );
	    	    ResultSet resultSet = preparedStatement.executeQuery();
	    	)
	    {
	    	
	    	while ( resultSet.next() ) {
	            imageDto = map( resultSet );
	            images.add(imageDto);
	        }
	    	
		} catch (SQLException e) {
			
			throw new DaoException( e );
			
		}
		
		return images;
	}

	@Override
	public ImageDto getLastImages() {
		
		final String SQL_SELECT_MAX = " SELECT max(image_id) as max_id from image ";
	    
	    Integer imageId = null ;
	    try (
	    		Connection connexion = daoFactory.getConnection();
	    	    PreparedStatement preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_MAX );
	    	    ResultSet resultSet = preparedStatement.executeQuery();
	    	)
	    
	    {
	        
	        if ( resultSet.next() ) {
	        	
	        	imageId = resultSet.getInt("max_id");
	            
	        }
		} catch (SQLException e) {

			throw new DaoException( e );
			
		}
	    return readImage(imageId);
	}
	
}
