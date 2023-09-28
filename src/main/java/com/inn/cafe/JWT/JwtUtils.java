package com.inn.cafe.JWT;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.web.server.ServerHttpSecurity.OAuth2ResourceServerSpec.JwtSpec;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtUtils {

	@Value("${jwt.secret}")
    private String secret;
	
//	 @Autowired
//	    public JwtUtils(@Value("${JWT_SECRET:your_default_secret_key_here}") String secret) {
//	        this.secretKey = new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
//	    }
//	
	
	SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);	
	
	public String extractUsername(String token) {
		return extractClamis(token, Claims::getSubject);
	}
	
	public Date extractExpiration(String token) {
		return extractClamis(token, Claims::getExpiration);
	}
	
	private <T> T extractClamis(String token,Function<Claims,T> clamisResolver ) {
		final Claims claims=extractAllClaims(token);
		return clamisResolver.apply(claims);
	}

	public Claims extractAllClaims(String token) {
	    return Jwts.parserBuilder()
	    		.setSigningKey(secretKey)
	    		.build()
	    		.parseClaimsJws(token)
	    		.getBody();
	}

	
	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}
	
	public String generateToken(String username,String role) {
		Map<String, Object> claims=new HashMap<>();
		claims.put("role", role);
		return createToken(claims, username);
	}
	
	private String createToken(Map<String, Object> claims,String subject) {
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(subject)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() +1000 * 60 * 60 * 10))
                .signWith(secretKey)
                .compact();
	}
	
	public Boolean validateToken(String token,UserDetails userDetails) {
		final String username =extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
	
}
