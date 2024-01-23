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
public class UserDto {

	Integer userId;
	
	String nom;
	
	String prenom;
	
	String ville;

	String rue;

	String numero;

	String telephone;
	
	String profile;

    String email;

    String password;
	
	RoleDto role;
	
	String token;
}
