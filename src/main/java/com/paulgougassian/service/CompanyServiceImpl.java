package com.paulgougassian.service;

import com.paulgougassian.entity.Company;
import com.paulgougassian.entity.Coupon;
import com.paulgougassian.mapper.AppMapper;
import com.paulgougassian.repository.CompanyRepository;
import com.paulgougassian.service.ex.EntityNotFoundException;
import com.paulgougassian.service.ex.MismatchingCouponAttributeException;
import com.paulgougassian.web.dto.CompanyDto;
import com.paulgougassian.web.dto.CouponDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.paulgougassian.config.CacheNameContainer.COMPANIES;

@Service
@EnableCaching
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final CouponService couponService;
    private final CompanyRepository repository;
    private final AppMapper mapper;

    @Override
    @Cacheable(value = COMPANIES, key = "#uuid")
    public CompanyDto findByUuid(UUID uuid) {
        return mapper.map(findByUuid_AsEntity(uuid));
    }

    @Override
    public List<CouponDto> findAllCompanyCoupons(UUID uuid) {
        Company company = findByUuid_AsEntity(uuid);
        return couponService.findAllByCompany(company.getId());
    }

    @Transactional
    @Override
    public CouponDto insertCoupon(UUID companyUuid, CouponDto dto) {
        validateCouponHasCompany(dto, companyUuid);
        Coupon coupon = mapper.map(dto);

        coupon.setCompany(findByUuid_AsEntity(companyUuid));
        return couponService.insertCoupon(coupon);
    }

    @Transactional
    @Override
    public CouponDto updateCouponAmount(UUID companyUuid, UUID couponUuid, int amount) {
        CouponDto dto = couponService.findByUuid(couponUuid);
        validateCouponHasCompany(dto, companyUuid);

        return couponService.updateAmount(dto, amount);
    }

    @Transactional
    @Override
    public void deleteCoupon(UUID companyUuid, UUID couponUuid) {
        CouponDto dto = couponService.findByUuid(couponUuid);
        validateCouponHasCompany(dto, companyUuid);

        couponService.deleteByUuid(couponUuid);
    }

    private void validateCouponHasCompany(CouponDto dto, UUID companyUuid) {
        if (!dto.getCompany().getUuid().equals(companyUuid)) {
            throw new MismatchingCouponAttributeException(
                    "Unable to insert or update coupon: %s, its company uuid does not match the company that requested the change".formatted(
                            dto.getUuid()));
        }
    }

    private Company findByUuid_AsEntity(UUID uuid) {
        return repository.findByUuid(uuid)
                         .orElseThrow(() -> new EntityNotFoundException(
                                 "Unable to find company with uuid: %s".formatted(uuid)));
    }
}
