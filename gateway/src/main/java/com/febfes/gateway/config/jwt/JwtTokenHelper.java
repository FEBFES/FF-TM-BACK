//package com.febfes.gateway.config.jwt;
//
//import com.febfes.gateway.domain.UserResponse;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.MalformedJwtException;
//import io.jsonwebtoken.security.SignatureException;
//import org.springframework.stereotype.Component;
//
//@Component
//public class JwtTokenHelper {
//
//    // Your Custom Key
//    private String SECRET_KEY;
//
//    // Validate the JWT token
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    public UserResponse extractPayloadFromToken(String token) {
//        Claims claims = getAllClaimsFromToken(token);
//        return UserResponse.builder()
//                .userId((String) claims.get("userId"))
//                .email((String) claims.get("email"))
//                .role((String) claims.get("role"))
//                .build();
//    }
//
//    public Claims getAllClaimsFromToken(String token) {
//        try {
//            return Jwts.parserBuilder().setSigningKey(SECRET_KEY)
//                    .build().parseClaimsJws(token).getBody();
//        } catch (ExpiredJwtException | SignatureException | MalformedJwtException e) {
//            throw new ApiException("Invalid Token: " + e.getLocalizedMessage());
//        }
//    }
//}