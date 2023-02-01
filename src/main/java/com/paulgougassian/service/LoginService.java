package com.paulgougassian.service;

import com.paulgougassian.web.UserCredentials;
import org.springframework.security.core.Authentication;

public interface LoginService {
    Authentication authenticate(UserCredentials credentials, String type);

    String generateAccessToken(Authentication authentication);

    String generateRefreshToken(Authentication authentication);
}
