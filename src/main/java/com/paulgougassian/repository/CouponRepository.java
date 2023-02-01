package com.paulgougassian.repository;

import com.paulgougassian.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByUuid(UUID uuid);

    @Modifying
    @Query("delete from Coupon where uuid = :uuid")
    int deleteByUuid(UUID uuid);

    List<Coupon> findByCustomers_Id(Long id);

    List<Coupon> findByCompany_Id(Long id);

    List<Coupon> findByCustomers_IdAndEndDateBefore(Long id, Date endDate);

    boolean existsByIdAndCustomers_Id(Long couponId, Long customerId);

    @Modifying
    @Query("delete from Coupon c where c.endDate < :endDate")
    int deleteByEndDateBefore(Date endDate);
}
