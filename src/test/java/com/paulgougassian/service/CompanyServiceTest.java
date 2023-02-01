package com.paulgougassian.service;

import com.paulgougassian.entity.Company;
import com.paulgougassian.entity.Coupon;
import com.paulgougassian.mapper.AppMapper;
import com.paulgougassian.repository.CompanyRepository;
import com.paulgougassian.service.ex.EntityNotFoundException;
import com.paulgougassian.service.ex.MismatchingCouponAttributeException;
import com.paulgougassian.web.dto.CompanyDto;
import com.paulgougassian.web.dto.CouponDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.paulgougassian.util.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class CompanyServiceTest {

    @Autowired
    private CompanyService companyService;
    @Autowired
    private AppMapper mapper;
    @MockBean
    private CompanyRepository companyRepository;
    @MockBean
    private CouponService couponService;

    @Test
    public void whenCompanyDoesntExist_ShouldThrowEntityNotFoundEx() {
        when(companyRepository.findByUuid(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.findByUuid(UUID.randomUUID())).isInstanceOf(
                EntityNotFoundException.class);
    }

    @Test
    public void whenCompanyExists_ShouldFindByUuid() {
        Company company = genCompany();
        UUID uuid = company.getUuid();

        when(companyRepository.findByUuid(uuid)).thenReturn(Optional.of(company));

        CompanyDto actual = companyService.findByUuid(uuid);
        CompanyDto expected = mapper.map(company);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void whenCompanyHasCoupons_ShouldReturnAllCompanyCoupons() {
        Company company = genCompany();
        Set<Coupon> coupons = genCoupons(4);
        company.setCoupons(coupons);

        when(companyRepository.findByUuid(company.getUuid())).thenReturn(Optional.of(company));
        when(couponService.findAllByCompany(company.getId())).thenReturn(
                coupons.stream().map(mapper::map).toList());

        CouponDto[] couponDtos = coupons.stream().map(mapper::map).toArray(CouponDto[]::new);
        assertThat(
                companyService.findAllCompanyCoupons(company.getUuid())).containsExactlyInAnyOrder(
                couponDtos);
    }

    @Test
    void whenInsertingCouponWithWrongCompanyUuid_ShouldThrowMismatchingCouponAttributeEx() {
        Company company = genCompany();
        CouponDto dto = genCouponDto();

        assertThatThrownBy(() -> companyService.insertCoupon(company.getUuid(), dto)).isInstanceOf(
                MismatchingCouponAttributeException.class);
    }

    @Test
    void whenInsertingValidCoupon_ShouldReturnInsertedDto() {
        Company company = genCompany();
        CouponDto dto = genCouponDto();
        dto.setCompany(mapper.map(company));

        when(companyRepository.findByUuid(company.getUuid())).thenReturn(Optional.of(company));
        when(couponService.insertCoupon(any())).thenReturn(dto);

        CouponDto postInsert = companyService.insertCoupon(company.getUuid(), dto);
        assertThat(postInsert).isEqualTo(dto);
    }
}