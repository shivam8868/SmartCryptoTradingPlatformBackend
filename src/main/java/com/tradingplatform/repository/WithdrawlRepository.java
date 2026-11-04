package com.tradingplatform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tradingplatform.domain.WithdrawlStatus;
import com.tradingplatform.modal.Withdrawl;

public interface WithdrawlRepository extends JpaRepository<Withdrawl, Long> {
    List<Withdrawl> findByUserId(Long userId);

        long countByStatus(WithdrawlStatus status);
        List<Withdrawl> findTop10ByOrderByDateDesc();
        
}
