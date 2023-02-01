package com.paulgougassian.web.controller;

import com.paulgougassian.service.CouponService;
import com.paulgougassian.service.CustomerService;
import com.paulgougassian.web.dto.CouponDto;
import com.paulgougassian.web.dto.CustomerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import static com.paulgougassian.web.util.ControllerUtils.extractUuid;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    private final CouponService couponService;

    @GetMapping
    public ResponseEntity<? extends CustomerDto> getCustomer(Principal principal) {
        CustomerDto dto = customerService.findByUuid(extractUuid(principal));
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/coupon/{uuid}")
    public ResponseEntity<? extends CouponDto> getCoupon(@PathVariable UUID uuid) {
        CouponDto dto = couponService.findByUuid(uuid);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/all/purchased")
    public ResponseEntity<List<? extends CouponDto>> getAllCustomerCoupons(Principal principal) {
        List<CouponDto> couponsDtos = customerService.findCustomerCoupons(extractUuid(principal));

        return ResponseEntity.ok(couponsDtos);
    }

    @GetMapping("/all/not-purchased")
    public ResponseEntity<List<? extends CouponDto>> getAllCouponsNotOwnedByCustomer(
            Principal principal) {
        List<CouponDto> couponDtos = customerService.findCouponsNotOwnedByCustomer(
                extractUuid(principal));

        return ResponseEntity.ok(couponDtos);
    }

    @GetMapping("/all/purchased/one-week-from-expiry")
    public ResponseEntity<List<? extends CouponDto>> getAllCustomerCouponsWithExpiryLessThanAWeek(
            Principal principal) {
        List<CouponDto> couponDtos = customerService.findCustomerCouponsWithEndDateInWeekOrLess(
                extractUuid(principal));

        return ResponseEntity.ok(couponDtos);
    }

    @PostMapping("/purchase")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void purchaseCoupon(Principal principal,
                               @RequestParam(name = "coupon") UUID couponUuid) {

        customerService.purchaseCoupon(extractUuid(principal), couponUuid);
    }
}
