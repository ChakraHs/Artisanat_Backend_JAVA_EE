package com.Pf_Artis.service.facade;

import java.util.List;

import javax.ws.rs.NotFoundException;

import com.Pf_Artis.dto.CommandeDto;

public interface CommandeServiceInterface {

	public CommandeDto createCommande(CommandeDto commandeDto)throws NotFoundException;
    public CommandeDto readCommande(Integer id);
    public CommandeDto updateCommande(CommandeDto commandeDto)throws NotFoundException;
    public void deleteCommande(Integer id);
    
    public List<CommandeDto> getAllCommandes();
	
}
