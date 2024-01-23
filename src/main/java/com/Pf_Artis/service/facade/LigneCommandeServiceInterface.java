package com.Pf_Artis.service.facade;

import java.util.List;

import javax.ws.rs.NotFoundException;

import com.Pf_Artis.dto.LigneCommandeDto;
import com.Pf_Artis.models.LigneCommandeKey;

public interface LigneCommandeServiceInterface {

	public LigneCommandeDto createLigneCommande( LigneCommandeDto ligneCommandeDto )throws NotFoundException;
	
    public LigneCommandeDto readLigneCommande( LigneCommandeKey ligneCommandeKey );
    
    public LigneCommandeDto updateLigneCommande( LigneCommandeDto ligneCommandeDto )throws NotFoundException;
    
    public void deleteLigneCommande( LigneCommandeKey ligneCommandeKey );
    
    public List<LigneCommandeDto> getAllLigneCommandes();

	List<LigneCommandeDto> getLigneCommandesByProduit(Integer produitId);
	
}
