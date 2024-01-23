package com.Pf_Artis.service.facade;

import java.util.List;

import javax.ws.rs.NotFoundException;

import com.Pf_Artis.dto.UserDto;



public interface UserServiceInterface {

	public UserDto createUser(UserDto userDto) throws NotFoundException;
	
    public UserDto readUser(Integer id);
    
    public UserDto updateUser(UserDto userDto);
    
    public void deleteUser(Integer id);
    
    public List<UserDto> getAllUsers();
    
    public UserDto getUserByEmail( String email );
    
    public void close();
	
}
