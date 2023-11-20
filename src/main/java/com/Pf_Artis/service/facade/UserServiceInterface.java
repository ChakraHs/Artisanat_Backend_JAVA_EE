package com.Pf_Artis.service.facade;

import java.util.List;

import com.Pf_Artis.models.User;



public interface UserServiceInterface {

	public User createUser(User user);
    public User readUser(Long id);
    public User updateUser(User user);
    public void deleteUser(Long id);
    
    public List<User> getAllUsers();
    
    public void close();
	
}
