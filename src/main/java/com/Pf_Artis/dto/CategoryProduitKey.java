package com.Pf_Artis.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryProduitKey {

	Integer produitId;
	
	Integer categoryId;
	
}
