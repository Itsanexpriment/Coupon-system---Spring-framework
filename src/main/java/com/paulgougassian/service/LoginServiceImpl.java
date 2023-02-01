package com.paulgougassian.service;

import com.paulgougassian.security.UserType;
import com.paulgougassian.service.ex.IllegalUserTypeException;
import com.paulgougassian.web.UserCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
public class LoginServiceImpl implements LoginService {
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder encoder;
    private final int accessTokenExpiryMins;
    private final int refreshTokenExpiryMins;

    public LoginServiceImpl(AuthenticationManager authenticationManager, JwtEncoder encoder,
                            @Value("${jwt.access-token-expiry}") int accessTokenExpiryMins,
                            @Value("${jwt.refresh-token-expiry}") int refreshTokenExpiryMins) {
        this.authenticationManager = authenticationManager;
        this.encoder = encoder;
        this.accessTokenExpiryMins = accessTokenExpiryMins;
        this.refreshTokenExpiryMins = refreshTokenExpiryMins;
    }

    @Override
    public Authentication authenticate(UserCredentials credentials, String type) {
        try {
            UserType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalUserTypeException("User-type: %s doesn't exist".formatted(type));
        }

        var byUsernameAndPassword = new UsernamePasswordAuthenticationToken(
                credentials.email(), credentials.password());

        return authenticationManager.authenticate(byUsernameAndPassword);
    }

    @Override
    public String generateAccessToken(Authentication authentication) {
       return generateToken(authentication, accessTokenExpiryMins);
    }

    @Override
    public String generateRefreshToken(Authentication authentication) {
        return generateToken(authentication, refreshTokenExpiryMins);
    }

    private String generateToken(Authentication authentication, int tokenExpiryMins) {
        Instant now = Instant.now();
        String roles = authentication.getAuthorities().stream()
                                           .map(GrantedAuthority::getAuthority)
                                           .collect(Collectors.joining(","));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                                          .issuer("self")
                                          .issuedAt(now)
                                          .expiresAt(now.plus(tokenExpiryMins, ChronoUnit.MINUTES))
                                          .subject(authentication.getName())
                                          .claim("role", roles)
                                          .build();

        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
