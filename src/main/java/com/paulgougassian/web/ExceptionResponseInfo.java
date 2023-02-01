package com.paulgougassian.web;

import org.springframework.http.HttpStatus;

public record ExceptionResponseInfo(HttpStatus status, String title) {
}
