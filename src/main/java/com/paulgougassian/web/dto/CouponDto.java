package com.paulgougassian.web.dto;

import com.google.common.base.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.UUID;

@Builder
@Getter
@Setter
public class CouponDto {
    private UUID uuid;
    private CompanyDto company;
    private String title;
    private int category;
    private Date startDate;
    private Date endDate;
    private int amount;
    private String description;
    private double price;
    private String imageUrl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CouponDto couponDto = (CouponDto) o;
        return Objects.equal(uuid, couponDto.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }
}
