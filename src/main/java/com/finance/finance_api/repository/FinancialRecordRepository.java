package com.finance.finance_api.repository;

import com.finance.finance_api.model.FinancialRecord;
import com.finance.finance_api.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {
    List<FinancialRecord> findByUserId(Long userId);

    @Query("SELECT r FROM FinancialRecord r WHERE r.user.id = :userId " +
           "AND (:category IS NULL OR r.category = :category) " +
           "AND (:type IS NULL OR r.type = :type) " +
           "AND (:startDate IS NULL OR r.date >= :startDate) " +
           "AND (:endDate IS NULL OR r.date <= :endDate)")
    List<FinancialRecord> filterRecords(
            @Param("userId") Long userId,
            @Param("category") String category,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
