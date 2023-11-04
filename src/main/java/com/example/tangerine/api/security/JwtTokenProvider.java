package com.example.tangerine.api.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.tangerine.api.domain.Role;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  @Value("${jwt.secret}")
  private String jwtSecret;

  @Value("${jwt.issuer}")
  private String jwtIssuer;

  public String generateToken(String username, List<Role> roles) {
    return JWT.create()
        .withIssuer(jwtIssuer)
        .withSubject(username)
        .withClaim("roles", roles.stream().map(Role::getName).toList())
        .withExpiresAt(LocalDate.now()
            .plusDays(15)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant())
        .sign(Algorithm.HMAC256(jwtSecret));
  }

  public Optional<DecodedJWT> toDecodedJWT(String token) {
    try {
      return Optional.of(JWT.require(Algorithm.HMAC256(jwtSecret))
          .withIssuer(jwtIssuer)
          .build()
          .verify(token));
    } catch (JWTVerificationException exception) {
      return Optional.empty();
    }
  }

  public String getUsernameFromToken(String token) {
    return JWT.require(Algorithm.HMAC256(jwtSecret))
        .withIssuer(jwtIssuer)
        .build()
        .verify(token)
        .getSubject();
  }
}