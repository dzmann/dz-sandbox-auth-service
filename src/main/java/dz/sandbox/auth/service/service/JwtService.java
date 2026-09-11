package dz.sandbox.auth.service.service;

import dz.sandbox.auth.service.entity.DzUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
  private final SecretKey secretKey;
  private final long expiration;

  //TODO: crear clase configuration para leer del properties
  public JwtService(
      @Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long expiration) {
    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expiration = expiration;
  }

  public String generateToken(DzUser dzUser) {
    Date now = new Date();

    return Jwts.builder()
        .subject(dzUser.getUsername())
        .claim("role", dzUser.getRole().name())
        .issuedAt(now)
        .expiration(new Date(now.getTime() + expiration))
        .signWith(secretKey)
        .compact();
  }

  public String extractUsername(String token) {
    return extractClaims(token).getSubject();
  }

  public boolean isTokenValid(String token) {

    try {
      Claims claims = extractClaims(token);
      return claims.getExpiration().after(new Date());
    } catch (Exception e) {
      return false;
    }
  }

  private Claims extractClaims(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
  }
}
