package com.Pf_Artis.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class CommandeByArtisanDto {

	Integer commandeId ;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING , pattern = "yyyy-MM-dd HH:mm:ss",timezone = "UTC")
	Date date_commande;
	
	String nomProduit;
	
}
