package com.tradingplatform.service;

import java.util.List;

import com.tradingplatform.modal.User;
import com.tradingplatform.modal.Withdrawl;

public interface WithdrawlService {
    Withdrawl requestWithdrawl(Long amount, User user);

    Withdrawl proceedWithdrawl(Long withdrawlId,boolean accept);

    List<Withdrawl> getUsersWithdrawlsHistory(User user);

    List<Withdrawl> getAllWithdrawlRequest();
}
