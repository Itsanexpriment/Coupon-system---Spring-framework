package com.paulgougassian.service;

import com.paulgougassian.entity.Coupon;
import com.paulgougassian.entity.Customer;
import com.paulgougassian.mapper.AppMapper;
import com.paulgougassian.repository.CustomerRepository;
import com.paulgougassian.service.ex.EntityNotFoundException;
import com.paulgougassian.service.ex.IllegalCouponPurchaseException;
import com.paulgougassian.service.ex.InvalidCouponAttributeException;
import com.paulgougassian.web.dto.CouponDto;
import com.paulgougassian.web.dto.CustomerDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static com.paulgougassian.config.CacheNameContainer.*;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CouponService couponService;
    private final AppMapper mapper;
    private final String dbTimezone;
    private final int minCouponAmount;


    public CustomerServiceImpl(CustomerRepository customerRepository, CouponService couponService,
                               AppMapper mapper, @Value("${db.timezone}") String dbTimezone,
                               @Value("${coupon.min-amount}") int minCouponAmount) {
        this.customerRepository = customerRepository;
        this.couponService = couponService;
        this.mapper = mapper;
        this.dbTimezone = dbTimezone;
        this.minCouponAmount = minCouponAmount;
    }

    @Override
    @Cacheable(value = CUSTOMERS, key = "#uuid")
    public CustomerDto findByUuid(UUID uuid) {
        return mapper.map(findByUuid_AsEntity(uuid));
    }

    @Override
    public List<CouponDto> findCustomerCoupons(UUID uuid) {
        Customer customer = findByUuid_AsEntity(uuid);
        return couponService.findAllByCustomer(customer.getId());
    }

    @Override
    public List<CouponDto> findCouponsNotOwnedByCustomer(UUID uuid) {
        Customer customer = findByUuid_AsEntity(uuid);
        return couponService.findAllNotOwnedByCustomer(customer.getId());
    }

    @Override
    @Transactional
    @CacheEvict(value = {COUPONS, CUSTOMER_COUPONS, CUSTOMER_NOT_OWNED_COUPONS,
                         COMPANY_COUPONS}, allEntries = true)
    public void purchaseCoupon(UUID customerUuid, UUID couponUuid) {
        Customer customer = findByUuid_AsEntity(customerUuid);
        Coupon coupon = couponService.findByUuid_AsEntity(couponUuid);

        if (couponService.isOwnedByCustomer(coupon.getId(), customer.getId())) {
            throw new IllegalCouponPurchaseException(
                    "Unable to process purchase, customer: %s already owns coupon: %s".formatted(
                            customerUuid, couponUuid));
        }

        if (!couponService.isAmountValid(coupon)) {
            throw new InvalidCouponAttributeException(
                    "Unable to purchase coupon: %s, it's amount is less than %d".formatted(
                            coupon.getUuid(), minCouponAmount));
        }

        coupon.setAmount(coupon.getAmount() - 1);
        customer.getCoupons().add(coupon);

        customerRepository.save(customer);
    }

    @Override
    public List<CouponDto> findCustomerCouponsWithEndDateInWeekOrLess(UUID uuid) {
        Customer customer = findByUuid_AsEntity(uuid);
        Date eightDaysFromNow = Date.valueOf(createDatePlusDaysFromNow(8, ZoneId.of(dbTimezone)));

        return couponService.findAllByCustomerAndDateIsBefore(customer.getId(), eightDaysFromNow);
    }

    private LocalDate createDatePlusDaysFromNow(int days, ZoneId zoneId) {
        ZonedDateTime utcDateTime = ZonedDateTime.now(zoneId);
        return utcDateTime.plusDays(days).toLocalDate();
    }

    private Customer findByUuid_AsEntity(UUID uuid) {
        return customerRepository.findByUuid(uuid)
                                 .orElseThrow(() -> new EntityNotFoundException(
                                         "Unable to find customer with uuid: %s".formatted(uuid)));
    }
}
