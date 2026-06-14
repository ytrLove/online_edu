package com.util;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret:defaultSecretKey12345678901234567890}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400}")
    private int jwtExpirationMs;
    
    @Value("${jwt.refresh-expiration:604800}")
    private int refreshExpirationMs;

    /**
     * 生成JWT令牌
     *
     * @param username 用户名
     * @return JWT令牌
     */
    public String generateJwtToken(String username) {
        try {
            logger.info("生成JWT令牌 - 用户名: {}", username);
            // 创建HMAC签名器
            JWSSigner signer = new MACSigner(jwtSecret);

            // 准备JWT声明
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(username)
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + jwtExpirationMs * 1000))
                    .build();

            // 创建签名的JWT
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            signedJWT.sign(signer);

            logger.debug("令牌生成成功 - 用户名: {}, 过期时间: {}", username, claimsSet.getExpirationTime());
            return signedJWT.serialize();
        } catch (JOSEException e) {
            logger.error("生成JWT令牌失败", e);
            throw new RuntimeException("Error generating JWT token", e);
        }
    }

    /**
     * 从JWT令牌中获取用户名
     *
     * @param token JWT令牌
     * @return 用户名
     */
    public String getUserNameFromJwtToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            throw new RuntimeException("Error parsing JWT token", e);
        }
    }

    /**
     * 验证JWT令牌
     *
     * @param authToken JWT令牌
     * @return 是否有效
     */
    public boolean validateJwtToken(String authToken) {
        try {
            logger.debug("验证JWT令牌: {}", authToken);
            SignedJWT signedJWT = SignedJWT.parse(authToken);
            JWSVerifier verifier = new MACVerifier(jwtSecret);

            // 验证签名
            boolean isSignatureValid = signedJWT.verify(verifier);

            // 验证过期时间
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            boolean isTokenExpired = expirationTime != null && expirationTime.before(new Date());

            logger.info("令牌验证结果 - 签名有效: {}, 过期: {}", isSignatureValid, isTokenExpired);
            return isSignatureValid && !isTokenExpired;
        } catch (ParseException | JOSEException e) {
            logger.error("令牌验证异常", e);
            return false;
        }
    }
}