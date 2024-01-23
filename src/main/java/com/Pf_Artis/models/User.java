package com.Pf_Artis.models;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "user")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false,name = "user_id")
	Integer userId;
	
	@Column(nullable = false)
	String nom;
	
	@Column(nullable = false)
	String prenom;
	
	@Column(nullable = false)
	String ville;
	
	@Column(nullable = false)
	String rue;
	
	@Column(nullable = false)
	String numero;
	
	@Column(nullable = false)
	String telephone;
	
	String profile;
	
	@Column(nullable = false)
    String email;
	
	@Column(nullable = false)
    String password;
	
	@ManyToOne()
	@JoinColumn(name = "role_id")
	Role role;
	
	@Column(nullable = false)
	String token;
	
	@OneToMany(mappedBy = "client",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	List<Commande> commandes ;
	
	@OneToMany(mappedBy = "artisant",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	List<Store> stores;
	
}
