package com.paulgougassian.web.controller;

import com.paulgougassian.service.CompanyService;
import com.paulgougassian.service.CouponService;
import com.paulgougassian.web.dto.CompanyDto;
import com.paulgougassian.web.dto.CouponDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import static com.paulgougassian.web.util.ControllerUtils.extractUuid;

@RestController
@RequestMapping("api/company")
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;
    private final CouponService couponService;

    @GetMapping
    public ResponseEntity<? extends CompanyDto> getCompany(Principal principal) {
        CompanyDto dto = companyService.findByUuid(extractUuid(principal));
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/coupon/{uuid}")
    public ResponseEntity<? extends CouponDto> getCoupon(@PathVariable UUID uuid) {
        CouponDto dto = couponService.findByUuid(uuid);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/all")
    public ResponseEntity<List<? extends CouponDto>> getAllCompanyCoupons(Principal principal) {
        List<CouponDto> couponDtos = companyService.findAllCompanyCoupons(extractUuid(principal));

        return ResponseEntity.ok(couponDtos);
    }

    @PostMapping("/create-coupon")
    public ResponseEntity<? extends CouponDto> insertCoupon(Principal principal,
                                                            @RequestBody CouponDto dto) {
        CouponDto postInsertDto = companyService.insertCoupon(extractUuid(principal), dto);

        URI uri = createResourceLocation(
                "api/company/coupon/%s".formatted(postInsertDto.getUuid()));
        return ResponseEntity.created(uri).body(postInsertDto);
    }

    @PutMapping("/update-amount")
    public ResponseEntity<? extends CouponDto> updateCouponAmount(Principal principal,
                                                                  @RequestParam(name = "coupon") UUID couponUuid,
                                                                  @RequestParam Integer amount) {

        CouponDto dto = companyService.updateCouponAmount(extractUuid(principal), couponUuid,
                                                          amount);

        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/delete")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteCouponByUuid(Principal principal,
                                   @RequestParam(name = "coupon") UUID CouponUuid) {

        companyService.deleteCoupon(extractUuid(principal), CouponUuid);
    }

    private URI createResourceLocation(String path) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(path).build().toUri();
    }
}
