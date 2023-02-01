package com.paulgougassian.task;

import com.paulgougassian.repository.CouponRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
@EnableScheduling
@EnableAsync
@Slf4j
public class ScheduledTasks {
    private final CouponRepository couponRepository;
    private final String dbTimezone;

    public ScheduledTasks(CouponRepository couponRepository, @Value("${db.timezone}") String dbTimezone) {
        this.couponRepository = couponRepository;
        this.dbTimezone = dbTimezone;
    }

    @Scheduled(cron = "5 0 0 * * *", zone = "Universal")
    @Transactional
    @Async
    public void deleteExpiredCoupons() {
        String threadName = Thread.currentThread().getName();

        log.info("start of task - deleteExpiredCoupons from thread - %s".formatted(
                threadName));

        ZonedDateTime nowUTC = ZonedDateTime.ofInstant(Instant.now(), ZoneId.of(dbTimezone));
        couponRepository.deleteByEndDateBefore(Date.valueOf(nowUTC.toLocalDate()));

        log.info("end of task - deleteExpiredCoupons from thread - %s".formatted(
                threadName));
    }
}
