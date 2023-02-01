package com.paulgougassian.web.advice;

import com.paulgougassian.web.ExceptionResponseInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class AppControllerAdvice {
    private final Map<Class<? extends Exception>, ExceptionResponseInfo> exceptionsInfo;

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception ex) {
        ExceptionResponseInfo exceptionInfo = exceptionsInfo.get(ex.getClass());

        if (exceptionInfo == null) {
            return handleGenericException(ex);
        }

        ProblemDetail problemDetail = ProblemDetail.forStatus(exceptionInfo.status());
        problemDetail.setTitle(exceptionInfo.title());
        problemDetail.setDetail(ex.getMessage());

        return problemDetail;
    }

    public ProblemDetail handleGenericException(Exception ex) {
        String exceptionId = UUID.randomUUID().toString().substring(0, 8);
        log.error("An unexpected exception has occurred - exceptionId %s".formatted(exceptionId),
                  ex);

        ProblemDetail problemDetail = ProblemDetail.forStatus(INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("An unexpected exception has occurred");
        problemDetail.setDetail(
                "For further information, contact server with exceptionId - %s".formatted(
                        exceptionId));

        return problemDetail;
    }
}
