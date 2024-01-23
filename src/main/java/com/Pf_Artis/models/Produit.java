package com.Pf_Artis.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "produit")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Produit implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false,name = "produit_id")
	Integer produitId;
	
	@Column(nullable = false)
	String nom;
	
	@Column(nullable = false)
	String description;
	
	@Column(nullable = false)
	double prix;
	
	@Column(nullable = false)
	Integer stock;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING , pattern = "yyyy-MM-dd HH:mm:ss",timezone = "UTC")
	@Column(nullable = false , name = "date_fabrication")
	Date DateFabrication;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING , pattern = "yyyy-MM-dd HH:mm:ss",timezone = "UTC")
	@Column(nullable = false , name = "date_peremption")
	Date DatePeremption;
	
	@Column(nullable = false)
	double poids;
	
	@OneToMany(mappedBy = "produit",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	List<LigneCommande> ligneCommandes ;
	
	@ManyToOne
	@JoinColumn(name = "store_id")
	Store store;
	
	@ManyToMany
	@JoinTable(
			name = "produit_category",
			joinColumns = @JoinColumn(name="produitId"),
			inverseJoinColumns = @JoinColumn(name="categoryId")
			)
	List<Category> categories;
	
	@OneToMany(mappedBy = "produit",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	List<Image> images;
}
