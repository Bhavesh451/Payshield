package com.payshield.service;

import com.payshield.dto.TransferResponse;
import com.payshield.entity.Transaction;
import com.payshield.entity.User;
import com.payshield.entity.Wallet;
import com.payshield.repository.TransactionRepository;
import com.payshield.repository.UserRepository;
import com.payshield.repository.WalletRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            WalletRepository walletRepository,
            UserRepository userRepository) {

        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
    }

    public Transaction deposit(Long walletId, BigDecimal amount) {

        validateAmount(amount);

        Wallet wallet = getWallet(walletId);

        wallet.setBalance(
                wallet.getBalance().add(amount)
        );

        walletRepository.save(wallet);

        Transaction transaction = new Transaction();

        transaction.setAmount(amount);
        transaction.setType("DEPOSIT");
        transaction.setStatus("SUCCESS");
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setWallet(wallet);

        return transactionRepository.save(transaction);
    }

    public Transaction withdraw(Long walletId, BigDecimal amount) {

        validateAmount(amount);

        Wallet wallet = getWallet(walletId);

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(
                wallet.getBalance().subtract(amount)
        );

        walletRepository.save(wallet);

        Transaction transaction = new Transaction();

        transaction.setAmount(amount);
        transaction.setType("WITHDRAW");
        transaction.setStatus("SUCCESS");
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setWallet(wallet);

        return transactionRepository.save(transaction);
    }

    @Transactional
    public TransferResponse transfer(
            Long fromWalletId,
            Long toWalletId,
            BigDecimal amount) {

        validateAmount(amount);

        if (fromWalletId.equals(toWalletId)) {
            throw new RuntimeException(
                    "Cannot transfer to the same wallet");
        }

        Wallet fromWallet = getWallet(fromWalletId);

        Wallet toWallet = walletRepository.findById(toWalletId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Receiver wallet not found"));

        if (fromWallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        fromWallet.setBalance(
                fromWallet.getBalance().subtract(amount)
        );

        toWallet.setBalance(
                toWallet.getBalance().add(amount)
        );

        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);

        Transaction senderTransaction = new Transaction();

        senderTransaction.setAmount(amount);
        senderTransaction.setType("TRANSFER_OUT");
        senderTransaction.setStatus("SUCCESS");
        senderTransaction.setCreatedAt(LocalDateTime.now());
        senderTransaction.setWallet(fromWallet);

        transactionRepository.save(senderTransaction);

        Transaction receiverTransaction = new Transaction();

        receiverTransaction.setAmount(amount);
        receiverTransaction.setType("TRANSFER_IN");
        receiverTransaction.setStatus("SUCCESS");
        receiverTransaction.setCreatedAt(LocalDateTime.now());
        receiverTransaction.setWallet(toWallet);

        transactionRepository.save(receiverTransaction);

        return new TransferResponse(
                "Transfer successful",
                fromWalletId,
                toWalletId,
                amount
        );
    }

    public Page<Transaction> getTransactions(
            Long walletId,
            String type,
            int page,
            int size) {

        Wallet wallet = getWallet(walletId);

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("createdAt").descending()
                );

        if (type != null && !type.isBlank()) {

            return transactionRepository
                    .findByWalletAndType(
                            wallet,
                            type.toUpperCase(),
                            pageable
                    );
        }

        return transactionRepository
                .findByWallet(wallet, pageable);
    }

    public Transaction getTransactionByReference(
            String referenceId) {

        Transaction transaction =
                transactionRepository
                        .findByReferenceId(referenceId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Transaction not found"));

        User currentUser = getCurrentUser();

        if (!transaction.getWallet()
                .getUser()
                .getId()
                .equals(currentUser.getId())) {

            throw new RuntimeException(
                    "You are not allowed to access this transaction");
        }

        return transaction;
    }

    private Wallet getWallet(Long walletId) {

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() ->
                        new RuntimeException("Wallet not found"));

        User currentUser = getCurrentUser();

        if (!wallet.getUser()
                .getId()
                .equals(currentUser.getId())) {

            throw new RuntimeException(
                    "You are not allowed to access this wallet");
        }

        return wallet;
    }

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new RuntimeException(
                    "User is not authenticated");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Current user not found"));
    }

    private void validateAmount(BigDecimal amount) {

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "Amount must be greater than zero");
        }
    }
}