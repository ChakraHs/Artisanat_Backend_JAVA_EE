package com.Pf_Artis.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProduitByDateDto {

	@JsonFormat(shape = JsonFormat.Shape.STRING , pattern = "yyyy-MM-dd HH:mm:ss",timezone = "UTC")
	Date dateAchat;
	
	Integer nbreAchats;
	
}
