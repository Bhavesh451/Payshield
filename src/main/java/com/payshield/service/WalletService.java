package com.payshield.service;

import com.payshield.entity.User;
import com.payshield.entity.Wallet;
import com.payshield.repository.UserRepository;
import com.payshield.repository.WalletRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public WalletService(
            WalletRepository walletRepository,
            UserRepository userRepository) {

        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
    }

    public Wallet createWallet(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        User currentUser = getCurrentUser();

        if (!currentUser.getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You are not allowed to create a wallet for this user");
        }

        if (walletRepository.findByUser(user).isPresent()) {
            throw new RuntimeException("Wallet already exists");
        }

        Wallet wallet = new Wallet();

        wallet.setUser(user);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCurrency("INR");

        return walletRepository.save(wallet);
    }

    public Wallet getWallet(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        User currentUser = getCurrentUser();

        if (!currentUser.getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You are not allowed to access this wallet");
        }

        return walletRepository.findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException("Wallet not found"));
    }

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Current user not found"));
    }
}