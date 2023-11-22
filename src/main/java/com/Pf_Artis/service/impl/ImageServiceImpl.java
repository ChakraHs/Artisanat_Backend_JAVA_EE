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
import com.Pf_Artis.models.Image;
import com.Pf_Artis.models.Produit;
import com.Pf_Artis.service.facade.ImageServiceInterface;
import com.Pf_Artis.service.facade.ProduitServiceInterface;

public class ImageServiceImpl implements ImageServiceInterface {

	private DaoFactory daoFactory;
	
	public ImageServiceImpl(DaoFactory daoFactory) {
		super();
		this.daoFactory = daoFactory;
	}

	private static Image map( ResultSet resultSet ) throws SQLException {
		Image image = new Image();
		image.setId( resultSet.getLong( "id" ) );
		image.setPath( resultSet.getString( "path" ) );
		
		ProduitServiceInterface produitService = new ProduitServiceImpl(DaoFactory.getInstance());
		Produit produit = produitService.readProduit( resultSet.getLong("produit_id") );
		
		image.setProduit(produit);
		
		return image;
	}
	
	@Override
	public Image createImage(Image image) {
		
		final String SQL_INSERT = "INSERT INTO image ( path , produit_id ) VALUES (  ? , ? ) ";
		final String SQL_SELECT_MAX = " SELECT max(id) as max_id from image ";
		
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    try {
			
	    	connexion = daoFactory.getConnection();
	    	
	    	preparedStatement = RequestPrepare.initRequestPrepare( connexion , SQL_INSERT , image.getPath() , image.getProduit().getId() );
	    	preparedStatement.executeUpdate();
	    	
	    	PreparedStatement ps2 = RequestPrepare.initRequestPrepare( connexion , SQL_SELECT_MAX );
	        resultSet = ps2.executeQuery();
	    	
	        if(resultSet.next()) {
				
				image.setId(resultSet.getLong("max_id"));
				
			}
	        
		} catch (SQLException e) {
			throw new DaoException( e );
		}
		
		return image;
	}

	@Override
	public Image readImage(Long id) {
		
		final String SQL_SELECT_PAR_ID = "SELECT id , path , produit_id FROM image WHERE id = ?";
		
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
		
	    Image image = new Image();
	    
	    try {
			
	    	connexion = daoFactory.getConnection();
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_PAR_ID, id );
	        resultSet = preparedStatement.executeQuery();
	        
	        if ( resultSet.next() ) {
	        	
	        	image = map( resultSet );
	            
	        }
	        
		} catch (SQLException e) {

			throw new DaoException( e );
			
		}
	    
		return image;
	}

	@Override
	public Image updateImage(Image image) {

final String SQL_UPDATE = "UPDATE image SET path = ? where id = ? ";
		
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		
		try {
			
			connexion = daoFactory.getConnection();
			
			preparedStatement = RequestPrepare.initRequestPrepare(connexion, SQL_UPDATE, image.getPath(),image.getId());
			preparedStatement.executeUpdate();
			
			
			
		} catch (SQLException e) {
			
			throw new DaoException( e );
			
		}
		return image;
	}

	@Override
	public void deleteImage(Long id) {
final String SQL_DESTROY = " Delete from image where id=? ";
		
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
	public List<Image> getAllImages() {
		
		final String SQL_SELECT_ALL = "SELECT id , path , produit_id FROM image";
		
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    Image image = new Image();
	    List<Image> images = new ArrayList<Image>();
	    
try {
			
	    	connexion = daoFactory.getConnection();
	    	preparedStatement = RequestPrepare.initRequestPrepare(connexion, SQL_SELECT_ALL );
	    	resultSet = preparedStatement.executeQuery();
	    	
	    	while ( resultSet.next() ) {
	            image = map( resultSet );
	            images.add(image);
	        }
	    	
		} catch (SQLException e) {
			
			throw new DaoException( e );
			
		}
		
		return images;
	}

}
