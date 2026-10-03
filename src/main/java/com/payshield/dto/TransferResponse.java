package com.payshield.dto;

import java.math.BigDecimal;

public class TransferResponse {

    private String message;
    private Long fromWalletId;
    private Long toWalletId;
    private BigDecimal amount;

    public TransferResponse(
            String message,
            Long fromWalletId,
            Long toWalletId,
            BigDecimal amount) {

        this.message = message;
        this.fromWalletId = fromWalletId;
        this.toWalletId = toWalletId;
        this.amount = amount;
    }

    public String getMessage() {
        return message;
    }

    public Long getFromWalletId() {
        return fromWalletId;
    }

    public Long getToWalletId() {
        return toWalletId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}