package com.tradingplatform.modal;

import java.time.LocalDateTime;

import com.tradingplatform.domain.WithdrawlStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Withdrawl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private WithdrawlStatus status=WithdrawlStatus.PENDING;

    private Long amount;
    @ManyToOne
    private User user;

    private LocalDateTime date= LocalDateTime.now();

}
