package com.paulgougassian.web.util;

import com.paulgougassian.web.TokenInfo;

import java.security.Principal;
import java.util.UUID;

public class ControllerUtils {

    public static UUID extractUuid(Principal principal) {
        return UUID.fromString(principal.getName());
    }

    public static TokenInfo toTokenInfo(String accessToken, String refreshToken) {
        return new TokenInfo(accessToken, refreshToken);
    }
}
