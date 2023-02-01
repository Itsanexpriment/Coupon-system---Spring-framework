package com.paulgougassian.service;

import com.paulgougassian.entity.Coupon;
import com.paulgougassian.mapper.AppMapper;
import com.paulgougassian.repository.CouponRepository;
import com.paulgougassian.service.ex.DuplicateEntityException;
import com.paulgougassian.service.ex.EntityNotFoundException;
import com.paulgougassian.service.ex.InvalidCouponAttributeException;
import com.paulgougassian.web.dto.CouponDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.*;

import static com.paulgougassian.config.CacheNameContainer.*;

@Service
public class CouponServiceImpl implements com.paulgougassian.service.CouponService {
    private final CouponRepository repository;
    private final AppMapper mapper;
    private final int minCouponAmount;
    private final BigDecimal minCouponPrice;

    public CouponServiceImpl(CouponRepository repository, AppMapper mapper,
                             @Value("${coupon.min-amount}") int minCouponAmount,
                             @Value("${coupon.min-price}") BigDecimal minCouponPrice) {
        this.repository = repository;
        this.mapper = mapper;
        this.minCouponAmount = minCouponAmount;
        this.minCouponPrice = minCouponPrice;
    }

    @Override
    @Cacheable(value = COUPONS, key = "#uuid")
    public CouponDto findByUuid(UUID uuid) {
        return mapper.map(findByUuid_AsEntity(uuid));
    }

    @Override
    public Coupon findByUuid_AsEntity(UUID uuid) {
        return repository.findByUuid(uuid)
                         .orElseThrow(() -> new EntityNotFoundException(
                                 "Unable to find coupon with uuid: %s".formatted(uuid)));
    }

    @Override
    @Cacheable(value = CUSTOMER_COUPONS, key = "#customerId")
    public List<CouponDto> findAllByCustomer(Long customerId) {
        List<CouponDto> coupons = repository.findByCustomers_Id(customerId)
                                            .stream()
                                            .map(mapper::map)
                                            .toList();

        return coupons.isEmpty() ? Collections.emptyList() : coupons;
    }

    @Override
    @Cacheable(value = COMPANY_COUPONS, key = "#companyId")
    public List<CouponDto> findAllByCompany(Long companyId) {
        List<CouponDto> coupons = repository.findByCompany_Id(companyId)
                                            .stream()
                                            .map(mapper::map)
                                            .toList();

        return coupons.isEmpty() ? Collections.emptyList() : coupons;
    }

    @Override
    @Cacheable(value = CUSTOMER_NOT_OWNED_COUPONS, key = "#customerId")
    public List<CouponDto> findAllNotOwnedByCustomer(Long customerId) {
        List<Coupon> allCoupons = repository.findAll();
        Set<Coupon> ownedByCustomer = new HashSet<>(repository.findByCustomers_Id(customerId));

        List<CouponDto> coupons = allCoupons.stream()
                                     .filter(c -> !ownedByCustomer.contains(c))
                                     .map(mapper::map)
                                     .toList();

        return coupons.isEmpty() ? Collections.emptyList() : coupons;
    }

    @Override
    public List<CouponDto> findAllByCustomerAndDateIsBefore(Long customerId, Date date) {
        List<CouponDto> coupons = repository.findByCustomers_IdAndEndDateBefore(customerId, date)
                                            .stream()
                                            .map(mapper::map)
                                            .toList();

        return coupons.isEmpty() ? Collections.emptyList() : coupons;
    }

    @Override
    @Transactional
    @CacheEvict(value = {COUPONS, CUSTOMER_COUPONS, CUSTOMER_NOT_OWNED_COUPONS, COMPANY_COUPONS}, allEntries = true)
    public void deleteByUuid(UUID uuid) {
        int deletedRows = repository.deleteByUuid(uuid);

        if (deletedRows < 1) {
            throw new EntityNotFoundException(
                    "Unable to delete coupon: %s. reason - coupon not found ".formatted(uuid));
        }
    }

    @Override
    public boolean isOwnedByCustomer(Long couponId, Long customerId) {
        return repository.existsByIdAndCustomers_Id(couponId, customerId);
    }

    @Override
    public boolean isAmountValid(Coupon coupon) {
        return coupon.getAmount() >= minCouponAmount;
    }

    @Override
    public CouponDto updateAmount(CouponDto dto, int amount) {
        return updateAmount(findByUuid_AsEntity(dto.getUuid()), amount);
    }

    @Transactional
    @Override
    @CacheEvict(value = {COUPONS, CUSTOMER_COUPONS, CUSTOMER_NOT_OWNED_COUPONS, COMPANY_COUPONS}, allEntries = true)
    public CouponDto updateAmount(Coupon coupon, int amount) {
        validateAmount(coupon, amount);
        coupon.setAmount(amount);
        repository.save(coupon);

        return mapper.map(coupon);
    }

    @Transactional
    @Override
    @CacheEvict(value = {COUPONS, CUSTOMER_NOT_OWNED_COUPONS, COMPANY_COUPONS}, allEntries = true)
    public CouponDto insertCoupon(Coupon coupon) {
        UUID uuid = coupon.getUuid();
        if (repository.findByUuid(uuid).isPresent()) {
            throw new DuplicateEntityException(
                    "Coupon: %s already exists in the database".formatted(uuid));
        }

        validateAmount(coupon, coupon.getAmount());
        validatePrice(coupon);

        return mapper.map(repository.save(coupon));
    }

    private void validateAmount(Coupon coupon, int amount) {
        if (!isAmountValid(coupon)) {
            throw new InvalidCouponAttributeException(
                    "Unable to insert or update coupon: %s, amount is less than %d".formatted(
                            coupon.getUuid(), minCouponAmount));
        }
    }

    private void validatePrice(Coupon coupon) {
        if (coupon.getPrice().compareTo(minCouponPrice) < 0) {
            throw new InvalidCouponAttributeException(
                    "Unable to insert or update coupon: %s, price is less than %.02f".formatted(
                            coupon.getUuid(), minCouponPrice.doubleValue()));
        }
    }
}
