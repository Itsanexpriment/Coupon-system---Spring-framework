package com.paulgougassian.service.ex;

public class IllegalCouponPurchaseException extends RuntimeException {
    public IllegalCouponPurchaseException() {
    }

    public IllegalCouponPurchaseException(String message) {
        super(message);
    }

    public IllegalCouponPurchaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalCouponPurchaseException(Throwable cause) {
        super(cause);
    }
}
