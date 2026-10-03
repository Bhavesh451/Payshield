package com.payshield.repository;

import com.payshield.entity.Transaction;
import com.payshield.entity.Wallet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    List<Transaction> findByWallet(Wallet wallet);

    Page<Transaction> findByWallet(
            Wallet wallet,
            Pageable pageable
    );

    Page<Transaction> findByWalletAndType(
            Wallet wallet,
            String type,
            Pageable pageable
    );

    Optional<Transaction> findByReferenceId(String referenceId);
}