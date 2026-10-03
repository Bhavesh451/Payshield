package com.payshield.controller;

import com.payshield.entity.Wallet;
import com.payshield.service.WalletService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/create/{userId}")
    public Wallet createWallet(@PathVariable Long userId) {
        return walletService.createWallet(userId);
    }

    @GetMapping("/{userId}")
    public Wallet getWallet(@PathVariable Long userId) {
        return walletService.getWallet(userId);
    }
}