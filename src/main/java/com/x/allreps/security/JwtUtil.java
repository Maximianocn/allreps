package com.x.allreps.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtil {

    private final String jwtSecret = "846f21c5216290c9867026360a37da384b57fdddd48aeecda432cb9ec2bbeb5f6510c5bce207ea39ea6613e7522a6daedd1a295fb13d79774c8c258bfa73bb65";
    private final long jwtExpirationMs = 86400000; // 1 dia

    public String generateJwtToken(UserDetailsImpl userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // Aqui o getUsername retorna o email
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getEmailFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Token inválido
        }
        return false;
    }
}
