package com.Pf_Artis.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class StoreDto {

	Integer storeId;
	
	String nom;
	
	String adress;
	
	String telephone;
	
	String profile;
	
	UserDto artisant;
	
	
}
