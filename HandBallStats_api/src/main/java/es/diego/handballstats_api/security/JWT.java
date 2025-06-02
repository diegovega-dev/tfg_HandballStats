package es.diego.handballstats_api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.security.Key;

public class JWT {

    private final static String SECRET_KEY = "oOqjKfP/YRkwUj6i+I4tNvmZ0S8A5qY3u9RnO1Yf7IkgWqeqhGv9vptFbcQmKP7IuC91yVq9RoMsmN8ht3JfYw==";
    private final static long EXPIRATION_TIME = 4000 * 60 * 60; // 4 horas en milisegundos

    private static Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // Genera un token con un username
    public static String generarToken(String username) {
        return "Bearer " + Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extrae el email (subject) desde el token
    public static String obtenerEmailDesdeToken(String token) {
        return getClaims(token).getSubject();
    }

    // Verifica si el token es válido (firma y expiración)
    public static boolean validarToken(String token) {
        try {
            Claims claims = getClaims(token);  // Verifica la firma del token
            // Verifica si el token ha expirado
            return claims.getExpiration().after(new Date());  // Solo verifica expiración aquí
        } catch (Exception e) {
            return false;  // Si no se puede verificar el token, lo consideramos inválido
        }
    }

    // Método interno para obtener los claims del token
    private static Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .parseClaimsJws(token)  // Esto valida la firma automáticamente
                .getBody();
    }
}
