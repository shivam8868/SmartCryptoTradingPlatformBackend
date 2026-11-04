package com.tradingplatform.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tradingplatform.modal.Wallet;
import com.tradingplatform.modal.WalletTransaction;
import com.tradingplatform.response.WalletTransactionRepository;

@Service
public class TransactionServiceImpl implements TransactionService {

        @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    @Override
    public List<WalletTransaction> getTransactionsByWallet(Wallet wallet) {
        return walletTransactionRepository.findByWallet(wallet);
    }

    @Override
    public WalletTransaction saveTransaction(WalletTransaction transaction) {
        return walletTransactionRepository.save(transaction);
    }
}
