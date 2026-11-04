package com.tradingplatform.response;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tradingplatform.modal.Wallet;
import com.tradingplatform.modal.WalletTransaction;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction,Long>{

    List<WalletTransaction> findByWallet(Wallet wallet);
    
}
