package com.challenge.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final Key key;
    private final long accessTokenExpTime;

    @Value("${jwt.refresh_expiration_day}")
    private long refreshExpireDay;

    private static final String AUTHORIZATION_HEADER = "Authorization";

    public JwtUtil(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access_expiration_time}") long accessTokenExpTime) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpTime = accessTokenExpTime;
    }

    /**
     * access token 생성 메소드
     *
     * @param memberId
     * @return
     */
    public String createAccessToken(Long memberId) {
        return createToken(memberId, accessTokenExpTime);
    }

    /**
     * refresh token 생성 메소드
     *
     * @param memberId
     * @return
     */
    public String createRefreshToken(Long memberId) {
        return createToken(memberId, refreshExpireDay);
    }

    /**
     * token claim에서 만료 시간 추출 메소드
     *
     * @param token
     * @return
     */
    public Long getTokenExpirationTime(String token) {
        return parseClaims(token).getExpiration().getTime();
    }

    /**
     * token claim에서 memberId 추출 메소드
     *
     * @param token
     * @return
     */
    public Long getMemberId(String token) {
        return parseClaims(token).get("memberId", Long.class);
    }

    /**
     * request의 header에서 token 값 추출 메소드
     *
     * @param request
     * @return
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * JWT 검증
     *
     * @param token
     * @return IsValidate
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
            // throw new JwtAuthenticationException(ErrorStatus.INVALID_TOKEN_EXCEPTION);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
            // throw new JwtAuthenticationException(ErrorStatus.EXPIRED_JWT_EXCEPTION);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
            // throw new JwtAuthenticationException(ErrorStatus.INVALID_TOKEN_EXCEPTION);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
            // throw new JwtAuthenticationException(ErrorStatus.INVALID_TOKEN_EXCEPTION);
        }
        return true;
    }

    /**
     * token 생성 메소드
     *
     * @param memberId
     * @param expireTime
     * @return
     */
    private String createToken(Long memberId, Long expireTime) {
        Claims claims = Jwts.claims();
        claims.put("memberId", memberId);

        long now = (new Date()).getTime();
        Date validity = new Date(now + expireTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(now))
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT Claims 추출 메소드
     *
     * @param accessToken
     * @return JWT Claims
     */
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}
