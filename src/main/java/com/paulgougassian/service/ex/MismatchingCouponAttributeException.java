package com.paulgougassian.service.ex;

public class MismatchingCouponAttributeException extends RuntimeException {
    public MismatchingCouponAttributeException() {
    }

    public MismatchingCouponAttributeException(String message) {
        super(message);
    }

    public MismatchingCouponAttributeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MismatchingCouponAttributeException(Throwable cause) {
        super(cause);
    }
}
