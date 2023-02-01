package com.paulgougassian.config;

import com.paulgougassian.service.ex.*;
import com.paulgougassian.web.ExceptionResponseInfo;
import jakarta.persistence.OptimisticLockException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.web.context.request.RequestContextListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@Configuration
public class AppConfig {

    @Bean(name = "exceptionsInfo")
    public Map<Class<? extends Exception>, ExceptionResponseInfo> exceptionsResponseInfo() {
        Map<Class<? extends Exception>, ExceptionResponseInfo> map = new HashMap<>();

        map.put(EntityNotFoundException.class, new ExceptionResponseInfo(NOT_FOUND, "Unable to locate requested resource"));
        map.put(UsernameNotFoundException.class, new ExceptionResponseInfo(NOT_FOUND, "Unable to find user with provided username"));

        map.put(DuplicateEntityException.class, new ExceptionResponseInfo(BAD_REQUEST, "Attempting to insert an entity which already exists"));
        map.put(InvalidCouponAttributeException.class, new ExceptionResponseInfo(BAD_REQUEST, "One or more attributes of provided coupon are invalid"));
        map.put(MismatchingCouponAttributeException.class, new ExceptionResponseInfo(BAD_REQUEST, "One or more attributes of provided coupon don't match the coupon stored in the db"));
        map.put(IllegalCouponPurchaseException.class, new ExceptionResponseInfo(BAD_REQUEST, "Attempting to purchase a coupon which is already owned"));

        map.put(InvalidBearerTokenException.class, new ExceptionResponseInfo(UNAUTHORIZED, "One or more attributes of jwt are invalid"));
        map.put(BadCredentialsException.class, new ExceptionResponseInfo(UNAUTHORIZED, "Username and / or password are incorrect"));
        map.put(IllegalUserTypeException.class, new ExceptionResponseInfo(UNAUTHORIZED, "The provided user-type is invalid"));

        map.put(AccessDeniedException.class, new ExceptionResponseInfo(FORBIDDEN, "User does not have permission to access this resource"));

        map.put(OptimisticLockException.class, new ExceptionResponseInfo(CONFLICT, "A conflict has occurred when trying to update one or more rows in the database"));

        return Collections.unmodifiableMap(map);
    }

    @Bean
    public RequestContextListener requestContextListener(){
        return new RequestContextListener();
    }
}
