package com.tradingplatform.modal;

import java.time.LocalDate;

import com.tradingplatform.domain.WalletTransactionType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class WalletTransaction {

@Id
@GeneratedValue(strategy = GenerationType.AUTO)
private Long id;

@ManyToOne
private Wallet wallet;

private WalletTransactionType type;

private LocalDate date;

// transfer id is only for wallet to wallet transfer, it will be null for other transaction types. It can be used to track the transfer and link the sender and receiver transactions together.
private String transferId;

private String purpose;

private Long amount;

}
