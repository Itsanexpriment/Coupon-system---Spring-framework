package com.paulgougassian.service;

import com.paulgougassian.entity.Coupon;
import com.paulgougassian.web.dto.CouponDto;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

public interface CouponService {
    CouponDto findByUuid(UUID uuid);

    Coupon findByUuid_AsEntity(UUID uuid);

    List<CouponDto> findAllByCustomer(Long customerId);

    List<CouponDto> findAllByCompany(Long companyId);

    List<CouponDto> findAllNotOwnedByCustomer(Long customerId);

    List<CouponDto> findAllByCustomerAndDateIsBefore(Long customerId, Date date);

    CouponDto insertCoupon(Coupon coupon);

    void deleteByUuid(UUID uuid);

    boolean isOwnedByCustomer(Long couponId, Long customerId);

    boolean isAmountValid(Coupon coupon);

    CouponDto updateAmount(CouponDto dto, int amount);

    CouponDto updateAmount(Coupon coupon, int amount);
}
