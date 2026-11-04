package com.tradingplatform.service;

import com.tradingplatform.modal.Order;
import com.tradingplatform.modal.User;
import com.tradingplatform.modal.Wallet;

public interface WalletService {
    Wallet getUserWallet(User user);
    Wallet addBalance(Wallet wallet,Long money);
    Wallet findWalletById(Long id);
    Wallet walletToWalletTransfer(User sender, Wallet receiverWallet, Long amount);
    Wallet payOrderPayment(Order order,User user);
}
