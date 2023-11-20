package com.Pf_Artis.service.facade;

import java.util.List;

import com.Pf_Artis.models.Facture;

public interface FactureServiceInterface {

	public Facture createFacture(Facture facture);
    public Facture readFacture(Long id);
    public Facture updateFacture(Facture facture);
    public void deleteFacture(Long id);
    
    public List<Facture> getAllFactures();
	
}
