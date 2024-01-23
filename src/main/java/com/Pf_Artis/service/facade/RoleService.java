package com.Pf_Artis.service.facade;

import java.util.List;

import com.Pf_Artis.dto.RoleDto;

import jakarta.ws.rs.NotFoundException;

public interface RoleService {


	List<RoleDto> findAll();
	
	RoleDto findByName(String name);
	
	RoleDto save( RoleDto roleDto );
	
	RoleDto update( RoleDto roleDto , Integer id ) throws NotFoundException;
	
	RoleDto findById( Integer id );
	
	void delete( Integer id );
	
}
