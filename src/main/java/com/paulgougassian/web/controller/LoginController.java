package com.paulgougassian.web.controller;

import com.paulgougassian.service.LoginService;
import com.paulgougassian.web.TokenInfo;
import com.paulgougassian.web.UserCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.paulgougassian.web.util.ControllerUtils.toTokenInfo;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<TokenInfo> login(@RequestBody UserCredentials credentials,
                                           @RequestParam String type) {
        Authentication authentication = loginService.authenticate(credentials, type);

        String accessToken = loginService.generateAccessToken(authentication);
        String refreshToken = loginService.generateRefreshToken(authentication);

        return ResponseEntity.ok().body(toTokenInfo(accessToken, refreshToken));
    }
}
