package com.Pf_Artis.config;


import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;


public class GenerateJwtToken {
	
	public static String generateJwtToken(String email , String role) {
		
		Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("exp", Date.from(Instant.now().plusSeconds(3600))); // 1 hour expiration

        String token = JsonWebTokenUtil.encode("yourSecretKey", claims);
        return token;
	}
	
	public String JsonWebTokenUtil( String secretKey, Map<String, Object> claims ) {
		
		JsonObjectBuilder builder = Json.createObjectBuilder();
        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            builder.add(entry.getKey(), entry.getValue().toString());
        }

        JsonObject jsonPayload = builder.build();
        String jwt = Jwt.builder()
                .setPayload(jsonPayload.toString())
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return jwt;
		
	}
	
}
