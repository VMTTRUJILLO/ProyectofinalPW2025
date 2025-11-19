package com.serena.serena_shop.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    // Clave secreta para firmar el token (en producción debe estar en variables de entorno)
    private static final String SECRET_KEY = "SereneShop2024SecretKeyForJWTAuthenticationSystemVerySecureLongKey123456789";
    private static final long JWT_EXPIRATION = 86400000; // 24 horas en milisegundos

    // Generar token para un usuario
    public String generateToken(String correo, Integer usuarioId, String rol) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("usuarioId", usuarioId);
        claims.put("rol", rol);
        return createToken(claims, correo);
    }

    // Crear el token JWT
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Obtener la clave de firma
    private Key getSignKey() {
        byte[] keyBytes = SECRET_KEY.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Extraer el correo del token
    public String extractCorreo(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extraer el usuarioId del token
    public Integer extractUsuarioId(String token) {
        return extractClaim(token, claims -> claims.get("usuarioId", Integer.class));
    }

    // Extraer el rol del token
    public String extractRol(String token) {
        return extractClaim(token, claims -> claims.get("rol", String.class));
    }

    // Extraer la fecha de expiración
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extraer un claim específico
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extraer todos los claims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Verificar si el token expiró
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Validar el token
    public Boolean validateToken(String token, String correo) {
        final String tokenCorreo = extractCorreo(token);
        return (tokenCorreo.equals(correo) && !isTokenExpired(token));
    }
}
