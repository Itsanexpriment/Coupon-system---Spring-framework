package com.paulgougassian.util;

import com.paulgougassian.entity.Company;
import com.paulgougassian.entity.Coupon;
import com.paulgougassian.entity.Customer;
import com.paulgougassian.mapper.AppMapper;
import com.paulgougassian.web.dto.CouponDto;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestUtils {
    private static final AppMapper MAPPER = AppMapper.INSTANCE;

    public static Coupon genCoupon() {
        return Coupon.builder()
                     .uuid(UUID.randomUUID())
                     .description(randomString())
                     .company(genCompany())
                     .price(BigDecimal.valueOf(randomInt()))
                     .amount(randomInt())
                     .title(randomString())
                     .startDate(Date.valueOf(LocalDate.now()))
                     .endDate(Date.valueOf(LocalDate.now().plusYears(1)))
                     .category(randomInt())
                     .imageUrl(randomString())
                     .customers(new HashSet<>())
                     .build();
    }

    public static Company genCompany() {
        return Company.builder()
                      .name(randomString())
                      .uuid(UUID.randomUUID())
                      .email(randomString())
                      .password(randomString())
                      .build();
    }

    public static Customer genCustomer() {
        return Customer.builder()
                       .firstName(randomString())
                       .lastName(randomString())
                       .uuid(UUID.randomUUID())
                       .email(randomString())
                       .password(randomString())
                       .coupons(new HashSet<>())
                       .build();
    }

    public static CouponDto genCouponDto() {
        return MAPPER.map(genCoupon());
    }

    public static Set<Coupon> genCoupons(int size) {
        return Stream.generate(TestUtils::genCoupon)
                     .limit(size)
                     .collect(Collectors.toSet());
    }

    private static String randomString() {
        return UUID.randomUUID().toString().substring(0, 5);
    }

    private static int randomInt() {
        return 1 + ((int) (Math.random() * 100));
    }
}
