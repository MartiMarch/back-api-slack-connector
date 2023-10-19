package com.valentiasoft.backapislackconnector.services

import com.valentiasoft.backapislackconnector.entities.UserEntity
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.crypto.spec.SecretKeySpec
import java.security.Key

@Service
class JwtService {
    private static final String PRIVATE_KEY = 'XXXX'
    private static final short TOKEN_EXPIRATION_HOURS = 8
    private Key key

    @Autowired
    JwtService(){
        key = generatePrivateKey()
    }

    String createToken(UserEntity userEntity){
        Map<String, Object> claims = [
            role: userEntity.getRole(),
            email: userEntity.getEmail(),
            username: userEntity.getUsername()
        ]

        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(userEntity.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (TOKEN_EXPIRATION_HOURS * 60 * 60 * 1000)))
                .signWith(generatePrivateKey())
                .compact()
    }

    String getUsername(String token){
        return getPayload(token).get('username', String.class)
    }

    boolean isValidToken(String token, UserEntity userEntity){
        Claims claims = getPayload(token)

        Date expirationDate = claims.getExpiration()
        boolean expirationDateCheck = expirationDate.before(new Date())

        String username = claims.get('username', String.class)
        boolean usernameCheck = (username == userEntity.getUsername())

        return !expirationDateCheck && usernameCheck
    }

    private Claims getPayload(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
    }

    private Key generatePrivateKey(){
        return new SecretKeySpec(
            Base64.getDecoder().decode(PRIVATE_KEY),
            SignatureAlgorithm.HS512.getJcaName()
        )
    }
}
