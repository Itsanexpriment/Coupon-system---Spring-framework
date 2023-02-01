package com.paulgougassian.service;

import com.paulgougassian.web.dto.CompanyDto;
import com.paulgougassian.web.dto.CouponDto;

import java.util.List;
import java.util.UUID;

public interface CompanyService {
    CompanyDto findByUuid(UUID uuid);

    List<CouponDto> findAllCompanyCoupons(UUID uuid);

    CouponDto insertCoupon(UUID companyUuid, CouponDto dto);

    CouponDto updateCouponAmount(UUID companyUuid, UUID couponUuid, int amount);

    void deleteCoupon(UUID companyUuid, UUID couponUuid);
}
