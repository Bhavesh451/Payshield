package com.payshield.service;

import com.payshield.entity.Transaction;
import com.payshield.entity.User;
import com.payshield.repository.TransactionRepository;
import com.payshield.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public AdminService(
            UserRepository userRepository,
            TransactionRepository transactionRepository) {

        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public long getTotalUsers() {
        return userRepository.count();
    }

    public long getTotalTransactions() {
        return transactionRepository.count();
    }
}
