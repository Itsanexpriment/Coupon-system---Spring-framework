package com.paulgougassian.mapper;

import com.paulgougassian.entity.Company;
import com.paulgougassian.entity.Coupon;
import com.paulgougassian.entity.Customer;
import com.paulgougassian.web.dto.CompanyDto;
import com.paulgougassian.web.dto.CouponDto;
import com.paulgougassian.web.dto.CustomerDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AppMapper {
    AppMapper INSTANCE = Mappers.getMapper(AppMapper.class);

    CouponDto map(Coupon coupon);

    Coupon map(CouponDto dto);

    CompanyDto map(Company company);

    Company map(CompanyDto dto);

    CustomerDto map(Customer customer);

    Customer map(CustomerDto dto);
}
