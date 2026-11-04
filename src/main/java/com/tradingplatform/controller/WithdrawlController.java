package com.tradingplatform.controller;

import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
// import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tradingplatform.domain.USER_ROLE;
import com.tradingplatform.modal.User;
import com.tradingplatform.modal.Wallet;
// import com.tradingplatform.modal.WalletTransaction;
import com.tradingplatform.modal.Withdrawl;
import com.tradingplatform.service.UserService;
import com.tradingplatform.service.WalletService;
import com.tradingplatform.service.WithdrawlService;

@RestController
// @RequestMapping("/api/withdrawl")
public class WithdrawlController {

    private final WithdrawlService withdrawlService;


    private final WalletService walletService;

    private final UserService userService;

    WithdrawlController(WithdrawlService withdrawlService,WalletService walletService,UserService userService) {
        this.withdrawlService=withdrawlService;
        this.walletService=walletService;
        this.userService=userService;
    }

    @PostMapping("/api/withdrawl/{amount}")
    public ResponseEntity<?> withdrawlRequest(
            @PathVariable Long amount,
            @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        Wallet userWallet = walletService.getUserWallet(user);

        Withdrawl withdrawl = withdrawlService.requestWithdrawl(amount, user);
        walletService.addBalance(userWallet, -withdrawl.getAmount());

        // WalletTransaction walletTransaction =
        // walletTransactionService.createWalletTransaction();
        return new ResponseEntity<>(withdrawl, HttpStatus.OK);
    }

    @PatchMapping("/api/admin/withdrawl/{id}/proceed/{accept}")
    public ResponseEntity<?> proceedWithdrawl(
            @PathVariable Long id,
            @PathVariable boolean accept,
            @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        if (user.getRole() != USER_ROLE.ROLE_ADMIN) {
            throw new Exception("Access Denied");
        }
        Withdrawl withdrawl = withdrawlService.proceedWithdrawl(id, accept);

        // Wallet userWallet = walletService.getUserWallet(user);
        // if rejected returning back money to owner wallet back
        System.out.println("Withdrawl ID = " + withdrawl.getId());
        System.out.println("Status = " + withdrawl.getStatus());
        System.out.println("User ID = " + withdrawl.getUser().getId());
        if (!accept) {
            User owner = withdrawl.getUser();
            Wallet ownerWallet = walletService.getUserWallet(owner);
            walletService.addBalance(ownerWallet, withdrawl.getAmount());
        }
        return new ResponseEntity<>(withdrawl, HttpStatus.OK);
    }

    @GetMapping("/api/withdrawl")
    public ResponseEntity<List<Withdrawl>> getWithdrawlHistory(
            @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        List<Withdrawl> withdrawlHistory = withdrawlService.getUsersWithdrawlsHistory(user);
        return new ResponseEntity<>(withdrawlHistory, HttpStatus.OK);
    }

    @GetMapping("/api/admin/withdrawl")
    public ResponseEntity<List<Withdrawl>> getAllWithdrawlRequest(
            @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        if (user.getRole() != USER_ROLE.ROLE_ADMIN) {
            throw new Exception("Access Denied");
        }
        List<Withdrawl> withdrawl = withdrawlService.getAllWithdrawlRequest();

        return new ResponseEntity<>(withdrawl, HttpStatus.OK);
    }

}
