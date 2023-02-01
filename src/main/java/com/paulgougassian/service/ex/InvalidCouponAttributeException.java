package com.paulgougassian.service.ex;

public class InvalidCouponAttributeException extends RuntimeException {
    public InvalidCouponAttributeException() {
    }

    public InvalidCouponAttributeException(String message) {
        super(message);
    }

    public InvalidCouponAttributeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCouponAttributeException(Throwable cause) {
        super(cause);
    }
}
