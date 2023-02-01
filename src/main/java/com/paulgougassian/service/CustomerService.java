package com.paulgougassian.service;

import com.paulgougassian.web.dto.CouponDto;
import com.paulgougassian.web.dto.CustomerDto;

import java.util.List;
import java.util.UUID;

public interface CustomerService {
    CustomerDto findByUuid(UUID uuid);

    List<CouponDto> findCustomerCoupons(UUID uuid);

    List<CouponDto> findCouponsNotOwnedByCustomer(UUID uuid);

    void purchaseCoupon(UUID customerUuid, UUID couponUuid);

    List<CouponDto> findCustomerCouponsWithEndDateInWeekOrLess(UUID uuid);
}
