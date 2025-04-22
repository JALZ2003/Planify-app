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

    //Método para crear un token por medio de la authentication
    public String generarToken(User user) {
        return buildToken(user);
    }

    //Método para extraer un Username apartir de un token
//    public String obtenerUsernameDeJwt(String token) {
//        Claims claims = Jwts.parser() // El método parser se utiliza con el fin de analizar el token
//                .setSigningKey(ConstantsSecurity.JWT_FIRMA)// Establece la clave de firma, que se utiliza para verificar la firma del token
//                .parseClaimsJws(token) //Se utiliza para verificar la firma del token, apartir del String "token"
//                .getBody(); /*Obtenemos el claims(cuerpo) ya verificado del token el cual contendrá la información de
//                nombre de usuario, fecha de expiración y firma del token*/
//        return claims.getSubject(); //Devolvemos el nombre de usuario
//    }
//
//    //Método para validar el token
//    public Boolean validarToken(String token) {
//        try {
//            //Validación del token por medio de la firma que contiene el String token(token)
//            //Si son idénticas validara el token o caso contrario saltara la excepción de abajo
//            Jwts.parser().sig(ConstantsSecurity.JWT_FIRMA).parseClaimsJws(token);
//            return true;
//        } catch (Exception e) {
//            throw new AuthenticationCredentialsNotFoundException("Jwt ah expirado o esta incorrecto");
//        }
//    }

    private String buildToken(User user) {
        return Jwts.builder()
                .id(user.getId().toString()) // ID del usuario como cadena
                .claims(Map.of("name", user.getName())) // Claims adicionales, en este caso el nombre
                .subject(user.getEmail()) // El sujeto del token es el correo electrónico del usuario
                .issuedAt(new Date(System.currentTimeMillis())) // Fecha de emisión del token
                .expiration(new Date(System.currentTimeMillis() + (long) 604800)) // Expiración en milisegundos (604800 segundos = 7 días)
                .signWith(getSignInKey()) // Firma del token con la clave secreta
                .compact(); // Construcción del token
    }

    private SecretKey getSignInKey() {
        System.out.println(secretKey); // Imprime la clave secreta para depuración
        byte[] keyByte = Decoders.BASE64.decode(secretKey); // Decodifica la clave secreta en bytes
        return Keys.hmacShaKeyFor(keyByte); // Genera la clave HMAC para firmar el token
    }

    public String extractId(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getId();
    }

}
