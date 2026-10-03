package com.payshield.repository;

import com.payshield.entity.Wallet;
import com.payshield.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByUser(User user);
}
