package com.Pf_Artis.service.facade;

import java.util.List;

import com.Pf_Artis.models.Commande;

public interface CommandeServiceInterface {

	public Commande createCommande(Commande user);
    public Commande readCommande(Long id);
    public Commande updateCommande(Commande user);
    public void deleteCommande(Long id);
    
    public List<Commande> getAllCommandes();
	
}
