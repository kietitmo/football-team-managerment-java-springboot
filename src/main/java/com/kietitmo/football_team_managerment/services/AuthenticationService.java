package com.kietitmo.football_team_managerment.services;

import com.kietitmo.football_team_managerment.dto.request.AuthenticationRequest;
import com.kietitmo.football_team_managerment.dto.request.IntrospectRequest;
import com.kietitmo.football_team_managerment.dto.response.AuthenticationResponse;
import com.kietitmo.football_team_managerment.dto.response.IntrospectResponse;
import com.kietitmo.football_team_managerment.entities.*;
import com.kietitmo.football_team_managerment.exceptions.AppException;
import com.kietitmo.football_team_managerment.exceptions.ErrorCode;
import com.kietitmo.football_team_managerment.repositories.InvalidatedTokenRepository;
import com.kietitmo.football_team_managerment.repositories.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Value;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    RefreshTokenService refreshTokenService;

    @NonFinal
    @Value("${jwt.signerAccessKey}")
    protected String SIGNER_ACCESS_KEY ;

    @NonFinal
    @Value("${jwt.signerRefreshKey}")
    protected String SIGNER_REFRESH_KEY ;

    @NonFinal
    @Value("${jwt.accessTokenDuration}")
    protected String ACCESS_TOKEN_DURATION;

    @NonFinal
    @Value("${jwt.refreshTokenDuration}")
    protected  String REFRESH_TOKEN_DURATION;

    public IntrospectResponse introspect(IntrospectRequest request)
            throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;
        try {
            verifyRefreshToken(token);
        } catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request){
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        System.out.println(request);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        System.out.println(authenticated);
        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);
        var token = RefreshToken.builder()
                .token(refreshToken)
                .user(user)
                .build();

        refreshTokenService.save(token);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private String generateAccessToken(User user) {
        return generateToken(user, ACCESS_TOKEN_DURATION, SIGNER_ACCESS_KEY);
    }

    private String generateRefreshToken(User user) {
        return generateToken(user, REFRESH_TOKEN_DURATION, SIGNER_REFRESH_KEY);
    }

    private String generateToken(User user, String tokenDuration, String signerKey) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("kietitmo.com")
                .issueTime(new Date())
                .jwtID(UUID.randomUUID().toString())
                .expirationTime(new Date(
                        Instant.now().plus(Long.parseLong(tokenDuration), ChronoUnit.HOURS).toEpochMilli()
                ))
                // set claim roles include: roles and permissions from output of method buildsScope
//                .claim("roles", buildScope(user))
                .claim("roles", user.getRoles().stream().map(role -> "ROLE_" + role.getName()).toList())
                .claim("permissions", user.getRoles().stream()
                        .flatMap(role -> role.getPermissions().stream()).map(Permission::getName) // Lấy tất cả permissions từ từng role
                        .distinct() // Xóa bỏ các phần tử trùng lặp
                        .toList())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public AuthenticationResponse refreshToken(HttpServletRequest request)
            throws ParseException, JOSEException {

        var refreshToken = request.getHeader("x-token");
        var signedJWT = verifyRefreshToken(refreshToken);

        // Tạo 2 tokens mới
        var username = signedJWT.getJWTClaimsSet().getSubject();

        var user = userRepository.findByUsername(username).orElseThrow(
                () -> new AppException(ErrorCode.UNAUTHENTICATED)
        );

        var newAccessToken = generateAccessToken(user);

        return AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void logout(HttpServletRequest request) throws ParseException, JOSEException {
        try {
            String accessToken = extractToken(request.getHeader("Authorization"));
            String refreshToken = request.getHeader("x-token");

            // verify Access Token
            SignedJWT accessTokenVerifier = verifyAccessToken(accessToken);
            String accessTokenId = accessTokenVerifier.getJWTClaimsSet().getJWTID();
            Date accessTokenExpiryTime = accessTokenVerifier.getJWTClaimsSet().getExpirationTime();

            // verify Refresh Token
            verifyRefreshToken(refreshToken);

            // Disable Access Token
            InvalidatedToken invalidatedAccessToken = InvalidatedToken.builder()
                    .id(accessTokenId)
                    .expiryTime(accessTokenExpiryTime)
                    .build();
            invalidatedTokenRepository.save(invalidatedAccessToken);

            // Disable refresh token
            var user = userRepository.findByUsername(accessTokenVerifier.getJWTClaimsSet().getSubject())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            refreshTokenService.delete(user.getUsername());

        } catch (AppException e){
            log.info("Token already expired");
        }

    }


    private SignedJWT verifyRefreshToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_REFRESH_KEY);

        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        String username = signedJWT.getJWTClaimsSet().getSubject();
        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date())))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (!refreshTokenService.isRefreshTokenExists(username))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }

    private SignedJWT verifyAccessToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_ACCESS_KEY);

        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date())))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (invalidatedTokenRepository
                .existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }

    /** This method to handle combine role and permission in a string
    private String buildScope(User user){
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions()
                            .forEach(permission -> stringJoiner.add(permission.getName()));
       });

        return stringJoiner.toString();
    }
    **/
    private String extractToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new IllegalArgumentException("Invalid Bearer Token");
    }

}
