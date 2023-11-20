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
public class FactureDto {

	@JsonFormat(shape = JsonFormat.Shape.STRING , pattern = "yyyy-MM-dd",timezone = "UTC")
	Date date_facturation;
	
	Long montant_total;
	
	Long commande_id;
	
}
