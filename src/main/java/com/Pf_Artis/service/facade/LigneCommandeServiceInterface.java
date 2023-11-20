package com.Pf_Artis.service.facade;

import java.util.List;

import com.Pf_Artis.models.LigneCommande;
import com.Pf_Artis.models.LigneCommandeKey;

public interface LigneCommandeServiceInterface {

	public LigneCommande createLigneCommande( LigneCommande ligneCommande );
	
    public LigneCommande readLigneCommande( LigneCommandeKey ligneCommandeKey );
    
    public LigneCommande updateLigneCommande( LigneCommande ligneCommande );
    
    public void deleteLigneCommande( LigneCommandeKey ligneCommandeKey );
    
    public List<LigneCommande> getAllLigneCommandes();
	
}
