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
import com.Pf_Artis.dto.RoleDto;
import com.Pf_Artis.exception.EntityNotFoundException;
import com.Pf_Artis.service.facade.RoleService;

import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RoleServiceImpl implements RoleService {

	private DaoFactory daoFactory;
	
	private static RoleDto map( ResultSet resultSet ) throws SQLException {
		
		RoleDto role = new RoleDto();
		
		role.setRoleId(resultSet.getInt( "role_id" ));
		role.setName(resultSet.getString( "name" ));
		
		return role;
		
	}
	
	@Override
	public List<RoleDto> findAll() {
		
		final String SQL_SELECT_ALL = " SELECT role_id , name FROM roles ";
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    RoleDto role = null;
	    List<RoleDto> roles = new ArrayList<RoleDto>();
	    
	    try {
	    	connexion = daoFactory.getConnection();
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_ALL);
	        resultSet = preparedStatement.executeQuery();
	        
	        while ( resultSet.next() ) {
	        	role = map( resultSet );
	            roles.add(role);
	        }
		} catch (SQLException e) {
			
			throw new DaoException( e );
			
		}
		
		return roles;
	}

	@Override
	public RoleDto findByName(String name) {
		final String SQL_SELECT_PAR_ID = "SELECT role_id , name FROM roles  WHERE name = ?";
		
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    RoleDto roleDto = new RoleDto();
	    
	    try {
	    	
	    	connexion = daoFactory.getConnection();
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_PAR_ID, name );
	        resultSet = preparedStatement.executeQuery();
	        
	        if ( resultSet.next() ) {
	        	
	        	roleDto = map( resultSet );
	            
	        }
		} catch (SQLException e) {

			throw new DaoException( e );
			
		}
		
		return roleDto;
	}

	@Override
	public RoleDto save(RoleDto roleDto) {

		final String SQL_INSERT = "INSERT INTO roles ( name ) VALUES ( ? ) ";
		final String SQL_SELECT_MAX = " SELECT max(role_id) as max_id from roles ";
		
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    try {
	    	connexion = daoFactory.getConnection();
	    	
	    	preparedStatement = RequestPrepare.initRequestPrepare( connexion , SQL_INSERT , roleDto.getName() );
	        preparedStatement.executeUpdate();
	        
	        PreparedStatement ps2 = RequestPrepare.initRequestPrepare( connexion , SQL_SELECT_MAX );
	        resultSet = ps2.executeQuery();
	        
	        if(resultSet.next()) {
				
	        	roleDto.setRoleId(resultSet.getInt("max_id"));
				
			}
	    	
		} catch (SQLException e) {
			throw new DaoException( e );
		}
	    
		return roleDto;
	}

	
	@Override
	public RoleDto update(RoleDto roleDto, Integer id) throws NotFoundException {

		if(this.findById(id)!=null) {
			final String SQL_UPDATE = "UPDATE roles SET name = ? where role_id = ? ";
			
			Connection connexion = null;
			PreparedStatement preparedStatement = null;
			
			try {
				
				connexion = daoFactory.getConnection();
				
		        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_UPDATE  , roleDto.getName() , id  );
		        preparedStatement.executeUpdate();
		        roleDto.setRoleId(id);
				
			} catch (SQLException e) {
				
				throw new DaoException( e );
				
			}
			return roleDto;
		}else {
			throw new EntityNotFoundException("Role Not found");
		}
	}

	@Override
	public RoleDto findById(Integer id) {
		
		final String SQL_SELECT_PAR_ID = "SELECT role_id , name FROM roles  WHERE role_id = ?";
		
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    RoleDto roleDto = new RoleDto();
	    
	    try {
	    	
	    	connexion = daoFactory.getConnection();
	        preparedStatement = RequestPrepare.initRequestPrepare( connexion, SQL_SELECT_PAR_ID, id );
	        resultSet = preparedStatement.executeQuery();
	        
	        if ( resultSet.next() ) {
	        	
	        	roleDto = map( resultSet );
	            
	        }
		} catch (SQLException e) {

			throw new DaoException( e );
			
		}
		
		return roleDto;
	}

	@Override
	public void delete(Integer id) {
		

		final String SQL_DESTROY = " Delete from roles where role_id=? ";
		
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

}
