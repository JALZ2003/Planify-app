package com.planify.app.security;

import com.planify.app.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
public class JwtGenerador {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    public String generarToken(User user) {
        return buildToken(user);
    }

    public boolean isTokenValid(String token, User user) {
        String email = extractEmail(token);
        return (email.equals(user.getEmail())) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private String buildToken(User user) {
        return Jwts.builder()
                .id(user.getId().toString()) // ID del usuario como cadena
                .claims(Map.of("name", user.getName())) // Claims adicionales, en este caso el nombre
                .subject(user.getEmail()) // El sujeto del token es el correo electrónico del usuario
                .issuedAt(new Date(System.currentTimeMillis())) // Fecha de emisión del token
                .expiration(new Date(System.currentTimeMillis() + (long) ConstantsSecurity.JWT_EXPIRATION_TOKEN)) // Expiración en milisegundos (604800 segundos = 7 días)
                .signWith(getSignInKey()) // Firma del token con la clave secreta
                .compact(); // Construcción del token
    }

    public String extractId(String token){
        return getClaims(token).getId();
    }

    public String extractEmail(String token){
        return getClaims(token).getSubject();
    }

    public Date extractExpiration(String token){
        return getClaims(token).getExpiration();
    }

    private SecretKey getSignInKey() {
        byte[] keyByte = Decoders.BASE64.decode(secretKey); // Decodifica la clave secreta en bytes
        return Keys.hmacShaKeyFor(keyByte); // Genera la clave HMAC para firmar el token
    }

    private Claims getClaims(String token){
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
