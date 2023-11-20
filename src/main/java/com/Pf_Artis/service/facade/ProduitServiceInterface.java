package com.Pf_Artis.service.facade;

import java.util.List;

import com.Pf_Artis.models.Produit;

public interface ProduitServiceInterface {

	public Produit createProduit(Produit produit);
    public Produit readProduit(Long id);
    public Produit updateProduit(Produit produit);
    public void deleteProduit(Long id);
    
    public List<Produit> getAllProduits();
    
}
