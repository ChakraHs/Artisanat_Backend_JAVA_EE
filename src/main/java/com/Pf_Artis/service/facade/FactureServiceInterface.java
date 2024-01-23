package com.Pf_Artis.service.facade;

import java.util.List;

import javax.ws.rs.NotFoundException;

import com.Pf_Artis.dto.FactureDto;

public interface FactureServiceInterface {

	public FactureDto createFacture(FactureDto factureDto)throws NotFoundException;
	
    public FactureDto readFacture(Integer id);
    
    public FactureDto updateFacture(FactureDto factureDto)throws NotFoundException;
    
    public void deleteFacture(Integer id);
    
    public List<FactureDto> getAllFactures();
	
}
