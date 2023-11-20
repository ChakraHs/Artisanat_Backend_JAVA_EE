package com.Pf_Artis.service.facade;

import java.util.List;

import com.Pf_Artis.models.Store;

public interface StoreServiceInterface {

	public Store createStore(Store store);
    public Store readStore(Long id);
    public Store updateStore(Store store);
    public void deleteStore(Long id);
    
    public List<Store> getAllStores();
	
}
