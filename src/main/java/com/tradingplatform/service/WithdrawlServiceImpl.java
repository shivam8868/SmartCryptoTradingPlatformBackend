package com.tradingplatform.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tradingplatform.domain.WithdrawlStatus;
import com.tradingplatform.modal.User;
import com.tradingplatform.modal.Withdrawl;
import com.tradingplatform.repository.WithdrawlRepository;

@Service
public class WithdrawlServiceImpl implements WithdrawlService {

    @Autowired
    private WithdrawlRepository withdrawlRepository;

    @Override
    public Withdrawl requestWithdrawl(Long amount, User user) {
        Withdrawl withdrawl = new Withdrawl();
        withdrawl.setAmount(amount);
        withdrawl.setUser(user);
        withdrawl.setStatus(WithdrawlStatus.PENDING);
        return withdrawlRepository.save(withdrawl);
    }

    @Override
    public Withdrawl proceedWithdrawl(Long withdrawlId, boolean accept) {
        System.out.println("STEP 1");
        Optional<Withdrawl> withdrawl = withdrawlRepository.findById(withdrawlId);
        System.out.println("STEP 2");
        if (withdrawl.isEmpty()) {
            throw new RuntimeException("Withdrawl request not found");
        }
        Withdrawl withdrawl1 = withdrawl.get();
        System.out.println("Current Status = " + withdrawl1.getStatus());
        if (withdrawl1.getStatus() != WithdrawlStatus.PENDING) {
            throw new RuntimeException("withdrawl already processed");
        }
        System.out.println("Current Status = " + withdrawl1.getStatus());
        withdrawl1.setDate(LocalDateTime.now());

        if (accept) {
            System.out.println("STEP 3 SUCCESS");
            withdrawl1.setStatus(WithdrawlStatus.SUCCESS);
        } else {
            System.out.println("STEP 3 DECLINE");
            withdrawl1.setStatus(WithdrawlStatus.DECLINE);
        }
        System.out.println("New Status = " + withdrawl1.getStatus());

        System.out.println("STEP 4");

        return withdrawlRepository.save(withdrawl1);
    }

    @Override
    public List<Withdrawl> getUsersWithdrawlsHistory(User user) {
        return withdrawlRepository.findByUserId(user.getId());
    }

    @Override
    public List<Withdrawl> getAllWithdrawlRequest() {
        return withdrawlRepository.findAll();
    }
}