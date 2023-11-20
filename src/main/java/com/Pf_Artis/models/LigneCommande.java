package com.Pf_Artis.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "line_commande")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class LigneCommande implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	LigneCommandeKey id;
	
	@ManyToOne
	@MapsId("commandeId")
	@JoinColumn(name = "commande_id")
	Commande commande;
	
	@ManyToOne
	@MapsId("produitId")
	@JoinColumn(name = "produit_id")
	Produit produit;
	
	@Column(nullable = false)
	double quantite;
	
	@Column(nullable = false)
	double prixUnitaire;
	
}