package com.Pf_Artis.service.facade;

import java.util.List;

import javax.ws.rs.NotFoundException;

import com.Pf_Artis.dto.StoreDto;

public interface StoreServiceInterface {

	public StoreDto createStore(StoreDto storeDto) throws NotFoundException;
	
    public StoreDto readStore(Integer id);
    
    public StoreDto updateStore(StoreDto storeDto) throws NotFoundException;
    
    public void deleteStore(Integer id);
    
    public List<StoreDto> getAllStores();

    StoreDto getLastStores();

	List<StoreDto> findStoreByArtisan(Integer id);

	Integer countStoreByArtisan(Integer id);
	
}
