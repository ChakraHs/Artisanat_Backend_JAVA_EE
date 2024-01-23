package com.Pf_Artis.service.facade;

import java.util.List;

import javax.ws.rs.NotFoundException;

import com.Pf_Artis.dto.ProduitDto;

public interface ProduitServiceInterface {

	public ProduitDto createProduit(ProduitDto produitDto)throws NotFoundException;
	
    public ProduitDto readProduit(Integer id);
    
    public ProduitDto updateProduit(ProduitDto produitDto)throws NotFoundException;
    
    public void deleteProduit(Integer id);
    
    public List<ProduitDto> getAllProduits();
    
    public ProduitDto getLastProduits();
    
}
