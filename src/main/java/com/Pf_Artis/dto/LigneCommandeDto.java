package com.Pf_Artis.dto;

import com.Pf_Artis.models.LigneCommandeKey;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class LigneCommandeDto {
	
	LigneCommandeKey id;
	
	CommandeDto commande;
	
	ProduitDto produit;
	
	double quantite;
	
	double prixUnitaire;

}
