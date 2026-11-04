package com.tradingplatform.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tradingplatform.domain.OrderType;
import com.tradingplatform.domain.WalletTransactionType;
import com.tradingplatform.modal.Order;
import com.tradingplatform.modal.User;
import com.tradingplatform.modal.Wallet;
import com.tradingplatform.modal.WalletTransaction;
import com.tradingplatform.repository.WalletRepository;
@Service
public class WalletServiceimpl implements WalletService {
    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionService transactionService;

    @Override
    public Wallet getUserWallet(User user) {
        Wallet wallet=walletRepository.findByUserId(user.getId());
        if(wallet==null){
            wallet=new Wallet();
            wallet.setUser(user);
            // i addded this line by myself
            wallet.setBalance(BigDecimal.ZERO);
            wallet = walletRepository.save(wallet);// save kiya
        }    
        return wallet;
    }

    // @Override
    // public Wallet addBalance(Wallet wallet, Long money) {
    //     BigDecimal balance=wallet.getBalance();
    //     BigDecimal newBalance=balance.add(BigDecimal.valueOf(money));
    //     wallet.setBalance(newBalance);
    //     return walletRepository.save(wallet);
    // }
    @Override
public Wallet addBalance(Wallet wallet, Long money) {
    BigDecimal newBalance = wallet.getBalance().add(BigDecimal.valueOf(money));
    wallet.setBalance(newBalance);
    Wallet updatedWallet = walletRepository.save(wallet);

    // ✅ Save transaction
    WalletTransaction tx = new WalletTransaction();
    tx.setWallet(wallet);
    tx.setAmount(money);
    tx.setType(WalletTransactionType.ADD_MONEY);
    tx.setPurpose("Deposit");
    tx.setDate(LocalDate.now());
    transactionService.saveTransaction(tx);

    return updatedWallet;
}


    @Override
    public Wallet findWalletById(Long id) {
        Optional<Wallet> wallet=walletRepository.findById(id);
        if(wallet.isPresent()){
            return wallet.get();
        }
        throw new RuntimeException("Wallet not found with id: "+id);
    }

    // @Override
    // public Wallet walletToWalletTransfer(User sender, Wallet receiverWallet, Long amount) {
    //     Wallet senderWallet=getUserWallet(sender);
    //     if(senderWallet.getBalance().compareTo(BigDecimal.valueOf(amount))<0) {
    //         throw new RuntimeException("Insufficient balance in sender's wallet");
    //     }
    //     BigDecimal senderBalance = senderWallet.getBalance().subtract(BigDecimal.valueOf(amount));
    //     senderWallet.setBalance(senderBalance);
    //     walletRepository.save(senderWallet);

    //     BigDecimal receiverBalance = receiverWallet.getBalance().add(BigDecimal.valueOf(amount));
    //     receiverWallet.setBalance(receiverBalance);
    //     walletRepository.save(receiverWallet);

    //     return senderWallet;
    // }

    @Override
public Wallet walletToWalletTransfer(User sender, Wallet receiverWallet, Long amount) {
    Wallet senderWallet = getUserWallet(sender);
    if (senderWallet.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0) {
        throw new RuntimeException("Insufficient balance in sender's wallet");
    }

    senderWallet.setBalance(senderWallet.getBalance().subtract(BigDecimal.valueOf(amount)));
    receiverWallet.setBalance(receiverWallet.getBalance().add(BigDecimal.valueOf(amount)));

    walletRepository.save(senderWallet);
    walletRepository.save(receiverWallet);

    String transferId = UUID.randomUUID().toString();

    // ✅ Save sender transaction
    WalletTransaction debitTx = new WalletTransaction();
    debitTx.setWallet(senderWallet);
    debitTx.setAmount(amount);
    debitTx.setType(WalletTransactionType.WALLET_TRANSFER);
    debitTx.setPurpose("Transfer to wallet " + receiverWallet.getId());
    debitTx.setDate(LocalDate.now());
    debitTx.setTransferId(transferId);
    transactionService.saveTransaction(debitTx);

    // ✅ Save receiver transaction
    WalletTransaction creditTx = new WalletTransaction();
    creditTx.setWallet(receiverWallet);
    creditTx.setAmount(amount);
    creditTx.setType(WalletTransactionType.WALLET_TRANSFER);
    creditTx.setPurpose("Received from wallet " + senderWallet.getId());
    creditTx.setDate(LocalDate.now());
    creditTx.setTransferId(transferId);
    transactionService.saveTransaction(creditTx);

    return senderWallet;
}


    // @Override
    // public Wallet payOrderPayment(Order order, User user) {
    //     Wallet wallet=getUserWallet(user);
        
    //     if(order.getOrderType().equals(OrderType.BUY)) {
    //         BigDecimal newBalance = wallet.getBalance().subtract(order.getPrice());
    //         if(newBalance.compareTo(order.getPrice())<0) {
    //             throw new RuntimeException("Insufficient balance in wallet for this transaction");

    //         }
    //         wallet.setBalance(newBalance);
    //     }
    //     else {
    //         BigDecimal newBalance = wallet.getBalance().add(order.getPrice());
    //         wallet.setBalance(newBalance);
    //     }
    //     walletRepository.save(wallet);
    //     return wallet;
    // }

    @Override
public Wallet payOrderPayment(Order order, User user) {
    Wallet wallet = getUserWallet(user);

    if (order.getOrderType().equals(OrderType.BUY)) {
        BigDecimal newBalance = wallet.getBalance().subtract(order.getPrice());
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Insufficient balance in wallet for this transaction");
        }
        wallet.setBalance(newBalance);

        // ✅ Save transaction
        WalletTransaction tx = new WalletTransaction();
        tx.setWallet(wallet);
        tx.setAmount(order.getPrice().longValue());
        tx.setType(WalletTransactionType.BUY_ASSET);
        tx.setPurpose("Buy asset order");
        tx.setDate(LocalDate.now());
        transactionService.saveTransaction(tx);

    } else {
        BigDecimal newBalance = wallet.getBalance().add(order.getPrice());
        wallet.setBalance(newBalance);

        // ✅ Save transaction
        WalletTransaction tx = new WalletTransaction();
        tx.setWallet(wallet);
        tx.setAmount(order.getPrice().longValue());
        tx.setType(WalletTransactionType.SELL_ASSET);
        tx.setPurpose("Sell asset order");
        tx.setDate(LocalDate.now());
        transactionService.saveTransaction(tx);
    }

    walletRepository.save(wallet);
    return wallet;
}


}
