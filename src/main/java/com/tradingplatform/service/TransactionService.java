package com.tradingplatform.service;

import java.util.List;

import com.tradingplatform.modal.Wallet;
import com.tradingplatform.modal.WalletTransaction;

public interface TransactionService {

    List<WalletTransaction> getTransactionsByWallet(Wallet wallet);
    WalletTransaction saveTransaction(WalletTransaction transaction);
}
