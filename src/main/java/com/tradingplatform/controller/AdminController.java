package com.tradingplatform.controller;

import com.tradingplatform.domain.AccountStatus;
import com.tradingplatform.domain.USER_ROLE;
import com.tradingplatform.domain.WithdrawlStatus;
import com.tradingplatform.modal.Order;
import com.tradingplatform.modal.User;
import com.tradingplatform.modal.WalletTransaction;
import com.tradingplatform.modal.Withdrawl;
import com.tradingplatform.repository.OrderRepository;
// import com.tradingplatform.modal.Withdrawl;
import com.tradingplatform.repository.UserRepository;
import com.tradingplatform.repository.WatchlistRepository;
import com.tradingplatform.repository.WithdrawlRepository;
import com.tradingplatform.response.WalletTransactionRepository;
import com.tradingplatform.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;

    private final UserService userService;

    private final WithdrawlRepository withdrawlRepository;

    private final WalletTransactionRepository walletTransactionRepository;

    private final WatchlistRepository watchlistRepository;
    private final OrderRepository orderRepository;
    AdminController(UserRepository userRepository, UserService userService, WithdrawlRepository withdrawlRepository,
            WalletTransactionRepository walletTransactionRepository,OrderRepository orderRepository, WatchlistRepository watchlistRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.withdrawlRepository = withdrawlRepository;
        this.walletTransactionRepository = walletTransactionRepository;
        this.orderRepository=orderRepository;
        this.watchlistRepository=watchlistRepository;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(@RequestHeader("Authorization") String jwt) {
        User user = userService.findUserProfileByJwt(jwt);
        if (user.getRole() != USER_ROLE.ROLE_ADMIN) {
            throw new RuntimeException("Access Denied");
        }
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@RequestHeader("Authorization") String jwt, @PathVariable Long id) {
        User usercheck = userService.findUserProfileByJwt(jwt);
        if (usercheck.getRole() != USER_ROLE.ROLE_ADMIN) {
            throw new RuntimeException("Access Denied");
        }
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@RequestHeader("Authorization") String jwt, @PathVariable Long id) {
        User user = userService.findUserProfileByJwt(jwt);
        if (user.getRole() != USER_ROLE.ROLE_ADMIN) {
            throw new RuntimeException("Access Denied");
        }
        if (userRepository.existsById(id)) {
            watchlistRepository.deleteById(id);
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/users/{id}/accountStatus/{status}")
    public ResponseEntity<User> setAccountStatus(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long id,
            @PathVariable String status) {

        User admin = userService.findUserProfileByJwt(jwt);
        if (admin.getRole() != USER_ROLE.ROLE_ADMIN) {
            throw new RuntimeException("Access denied");
        }

        User userEntity = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            AccountStatus newStatus = AccountStatus.valueOf(status.toUpperCase());
            userEntity.setAccountStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status. Allowed: ACTIVE, INACTIVE, BAN");
        }

        userRepository.save(userEntity);
        return ResponseEntity.ok(userEntity);
    }

    @GetMapping("/wallet-transactions")
    public ResponseEntity<List<WalletTransaction>> getAllTransactions(@RequestHeader("Authorization") String jwt) {
        User userCheck = userService.findUserProfileByJwt(jwt);
        if (userCheck.getRole() != USER_ROLE.ROLE_ADMIN) {
            throw new RuntimeException("Access denied");
        }
        List<WalletTransaction> transactions = walletTransactionRepository.findAll();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats(@RequestHeader("Authorization") String jwt) {
        User user = userService.findUserProfileByJwt(jwt);
        if (user.getRole() != USER_ROLE.ROLE_ADMIN) {
            throw new RuntimeException("Access Denied");
        }
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalusers", userRepository.count());
        stats.put("totalAdmins", userRepository.countByRole(USER_ROLE.ROLE_ADMIN));
        stats.put("totalCustomers", userRepository.countByRole(USER_ROLE.ROLE_CUSTOMER));
        stats.put("pendingWithdrawls", withdrawlRepository.countByStatus(WithdrawlStatus.PENDING));
        stats.put("totalWithdrawl", withdrawlRepository.count());
        stats.put("successWithdrawl", withdrawlRepository.countByStatus(WithdrawlStatus.SUCCESS));
        stats.put("declineWithdrawl", withdrawlRepository.countByStatus(WithdrawlStatus.DECLINE));
        stats.put("totalOrders", orderRepository.count());
        return ResponseEntity.ok(stats);
    }

    @PatchMapping("users/{id}/role/{role}")
    public ResponseEntity<User> changeRole(@RequestHeader("Authorization") String jwt, @PathVariable Long id,
            @PathVariable String role) {
        User usercheck = userService.findUserProfileByJwt(jwt);
        if (usercheck.getRole() != USER_ROLE.ROLE_ADMIN) {
            throw new RuntimeException("Access Denied");
        }
        if(id==1052) throw new RuntimeException("can not change the role of super admin");
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(USER_ROLE.valueOf(role));
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users/email/{email}")
    public ResponseEntity<User> getUserByEmail(@RequestHeader("Authorization") String jwt, @PathVariable String email) {
        User usercheck = userService.findUserProfileByJwt(jwt);
        if (usercheck.getRole() != USER_ROLE.ROLE_ADMIN) {
            throw new RuntimeException("Access Denied");
        }
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/recent-withdrawals")
    public ResponseEntity<List<Withdrawl>> getRecentWithdrawals(
            @RequestHeader("Authorization") String jwt) {

        User admin = userService.findUserProfileByJwt(jwt);
        if (admin.getRole() != USER_ROLE.ROLE_ADMIN) {
            throw new RuntimeException("Access denied");
        }

        List<Withdrawl> withdrawals = withdrawlRepository.findTop10ByOrderByDateDesc();
        return ResponseEntity.ok(withdrawals);
    }

        @GetMapping("/withdrawals")
    public ResponseEntity<List<Withdrawl>> getAllWithdrawals(
            @RequestHeader("Authorization") String jwt) {

        User admin = userService.findUserProfileByJwt(jwt);
        if (admin.getRole() != USER_ROLE.ROLE_ADMIN) {
            throw new RuntimeException("Access denied");
        }

        List<Withdrawl> withdrawals = withdrawlRepository.findAll();
        return ResponseEntity.ok(withdrawals);
    }
        @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders(
            @RequestHeader("Authorization") String jwt) {

        User admin = userService.findUserProfileByJwt(jwt);
        if (admin.getRole() != USER_ROLE.ROLE_ADMIN) {
            throw new RuntimeException("Access denied");
        }

        List<Order> orders = orderRepository.findAll();
        return ResponseEntity.ok(orders);
    }

}
