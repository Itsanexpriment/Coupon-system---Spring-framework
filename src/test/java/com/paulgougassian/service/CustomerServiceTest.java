package com.paulgougassian.service;

import com.paulgougassian.entity.Coupon;
import com.paulgougassian.entity.Customer;
import com.paulgougassian.mapper.AppMapper;
import com.paulgougassian.repository.CustomerRepository;
import com.paulgougassian.service.ex.EntityNotFoundException;
import com.paulgougassian.web.dto.CouponDto;
import com.paulgougassian.web.dto.CustomerDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.paulgougassian.util.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private AppMapper mapper;
    @MockBean
    private CustomerRepository customerRepo;
    @MockBean
    private CouponService couponService;

    @Test
    void whenCustomerDoesntExist_ShouldThrowEntityNotFoundEx() {
        when(customerRepo.findByUuid(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.findByUuid(UUID.randomUUID())).isInstanceOf(
                EntityNotFoundException.class);
    }

    @Test
    void whenCustomerExists_ShouldFindByUuid() {
        Customer customer = genCustomer();

        when(customerRepo.findByUuid(any())).thenReturn(Optional.of(customer));
        CustomerDto dto = customerService.findByUuid(customer.getUuid());

        assertThat(dto).isEqualTo(mapper.map(customer));
    }

    @Test
    void whenCustomerHasCoupons_ShouldReturnAllCustomerCoupons() {
        Customer customer = genCustomer();
        Set<Coupon> coupons = genCoupons(4);
        customer.setCoupons(coupons);

        when(customerRepo.findByUuid(customer.getUuid())).thenReturn(Optional.of(customer));
        when(couponService.findAllByCustomer(customer.getId()))
                .thenReturn(
                coupons.stream().map(mapper::map).toList());

        CouponDto[] couponDtos = coupons.stream().map(mapper::map).toArray(CouponDto[]::new);
        assertThat(customerService.findCustomerCoupons(customer.getUuid())).containsExactlyInAnyOrder(
                couponDtos);
    }

    @Test
    void whenCustomerDoesntHaveCoupons_ShouldReturnEmptyList() {
        Customer customer = genCustomer();

        when(customerRepo.findByUuid(customer.getUuid())).thenReturn(Optional.of(customer));
        when(couponService.findAllByCustomer(customer.getId())).thenReturn(
                Collections.emptyList());

        assertThat(customerService.findCustomerCoupons(customer.getUuid())).isEmpty();
    }

    @Test
    void whenPurchasingCoupon_ShouldSaveCustomerToDatabase() {
        Customer customer = genCustomer();
        Coupon coupon = genCoupon();

        when(customerRepo.findByUuid(any())).thenReturn(Optional.of(customer));
        when(couponService.findByUuid_AsEntity(any())).thenReturn(coupon);
        when(couponService.isOwnedByCustomer(any(),any())).thenReturn(false);
        when(couponService.isAmountValid(any())).thenReturn(true);

        customerService.purchaseCoupon(customer.getUuid(), coupon.getUuid());

        verify(customerRepo).save(customer);
    }

    @Test
    void whenPurchasingCoupon_ShouldDecrementCouponAmountByOne() {
        Customer customer = genCustomer();
        Coupon coupon = genCoupon();

        when(customerRepo.findByUuid(any())).thenReturn(Optional.of(customer));
        when(couponService.findByUuid_AsEntity(any())).thenReturn(coupon);
        when(couponService.isAmountValid(coupon)).thenReturn(true);

        int expected = coupon.getAmount() - 1;
        customerService.purchaseCoupon(customer.getUuid(), coupon.getUuid());
        int actual = coupon.getAmount();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void whenPurchasingCoupon_ShouldAddCouponToCustomerCoupons() {
        Customer customer = genCustomer();
        Coupon coupon = genCoupon();

        when(customerRepo.findByUuid(any())).thenReturn(Optional.of(customer));
        when(couponService.findByUuid_AsEntity(any())).thenReturn(coupon);
        when(couponService.isOwnedByCustomer(any(),any())).thenReturn(false);
        when(couponService.isAmountValid(any())).thenReturn(true);

        customerService.purchaseCoupon(customer.getUuid(), coupon.getUuid());

        assertThat(customer.getCoupons()).contains(coupon);
    }
}