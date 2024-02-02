package com.Pf_Artis.config;


import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.*;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;


public class GenerateJwtToken {
	
	private static SecretKey secretKey;

    static {
        try {
            // Initialisez la clé secrète une seule fois
            secretKey = generateSecretKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace(); // Gérez les erreurs d'initialisation
        }
    }

	private static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        // Utilisez l'algorithme HMAC SHA-256 pour générer la clé secrète
        KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
        keyGen.init(256); // Spécifiez la longueur de la clé en bits
        return keyGen.generateKey();
    }
	
	public static String generateJwtToken( String email ) throws NoSuchAlgorithmException {
		
		try {
			
			JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256).build();
			
			JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(email) // Le nom d'utilisateur comme sujet
                    .issueTime(new Date()) // Temps d'émission du JWT
                    .expirationTime(Date.from(Instant.now().plusSeconds(3600))) // Temps d'expiration du JWT (1 heure)
                    .build();
			
			JWSSigner signer = new MACSigner(secretKey);
			System.out.println("secretKey : "+secretKey);
//            SignedJWT signedJWT = new SignedJWT(header, claimsSet);
//            signedJWT.sign(signer);
//			return signedJWT.serialize();
			JWSObject jwsObject = new JWSObject(header, new Payload(claimsSet.toJSONObject()));
			jwsObject.sign(signer);
			return jwsObject.serialize();
            
            
		} catch (JOSEException e) {
			
			e.printStackTrace(); // Gérez les erreurs d'encodage du JWT
            return null;
		}
	}
	
	public static boolean isValidJwt(String jwt) throws NoSuchAlgorithmException {
        try {
//        	Parsez le JWT
//            SignedJWT signedJWT = SignedJWT.parse(jwt);
//            System.out.println("signedJWT : "+signedJWT);
            JWSObject jwsObject = JWSObject.parse(jwt);
            JWSVerifier verifier = new MACVerifier(secretKey);

            if (!jwsObject.verify(verifier)) {
                // La signature est valide
            	System.out.println("JWT is not valid");
            	return false;
            } 
            // Obtenez les revendications du JWT
            JWTClaimsSet claimsSet = SignedJWT.parse(jwt).getJWTClaimsSet();

            // Obtenez la date d'expiration du JWT
            Date expirationTime = claimsSet.getExpirationTime();

            // Vérifiez si le JWT est expiré
            if (expirationTime != null && expirationTime.before(new Date())) {
                System.out.println("JWT has expired");
                return false; // JWT expiré
            }
            
            return true; // JWT valide
            // Vérifiez la signature du JWT
//            JWSObject jwsObject = new JWSObject(signedJWT.getHeader(), new Payload(signedJWT.getPayload().toString()));
//            System.out.println("jwsObject : "+jwsObject);
//            System.out.println("secretKey : "+secretKey);
//            if (!jwsObject.verify(new MACVerifier(secretKey))) {
//                return false; // Signature invalide
//            }
//
//            // Vérifiez si le JWT est toujours valide (pas expiré)
//            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
//            Date expirationTime = claimsSet.getExpirationTime();
//            if (expirationTime != null && expirationTime.before(new Date())) {
//                return false; // JWT expiré
//            }
//
            
        } catch (ParseException | JOSEException e) {
            e.printStackTrace(); // Gérez les erreurs d'analyse du JWT
            return false;
        }
    }
	public static String extractUsername(String jwt) {
        try {
            // Parsez le JWT
            SignedJWT signedJWT = SignedJWT.parse(jwt);

            // Obtenez les revendications du JWT
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            // Obtenez le nom d'utilisateur à partir des revendications
            return claimsSet.getSubject();
        } catch (ParseException e) {
            e.printStackTrace(); // Gérez les erreurs d'analyse du JWT
            return null;
        }
    }
	
}
