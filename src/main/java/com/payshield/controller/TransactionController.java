package com.payshield.controller;

import com.payshield.dto.TransferResponse;
import com.payshield.entity.Transaction;
import com.payshield.service.TransactionService;

import org.springframework.data.domain.Page;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(
            TransactionService transactionService) {

        this.transactionService = transactionService;
    }

    @PostMapping("/deposit/{walletId}")
    public Transaction deposit(
            @PathVariable Long walletId,
            @RequestParam BigDecimal amount) {

        return transactionService.deposit(
                walletId,
                amount
        );
    }

    @PostMapping("/withdraw/{walletId}")
    public Transaction withdraw(
            @PathVariable Long walletId,
            @RequestParam BigDecimal amount) {

        return transactionService.withdraw(
                walletId,
                amount
        );
    }

    @PostMapping("/transfer")
    public TransferResponse transfer(
            @RequestParam Long fromWalletId,
            @RequestParam Long toWalletId,
            @RequestParam BigDecimal amount) {

        return transactionService.transfer(
                fromWalletId,
                toWalletId,
                amount
        );
    }

    @GetMapping("/wallet/{walletId}")
    public Page<Transaction> getTransactions(
            @PathVariable Long walletId,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return transactionService.getTransactions(
                walletId,
                type,
                page,
                size
        );
    }

    @GetMapping("/reference/{referenceId}")
    public Transaction getTransactionByReference(
            @PathVariable String referenceId) {

        return transactionService
                .getTransactionByReference(referenceId);
    }
}