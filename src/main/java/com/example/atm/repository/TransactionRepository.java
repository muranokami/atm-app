package com.example.atm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.atm.entity.Transaction;
import com.example.atm.entity.User;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserOrderByTransactionTimeDesc(User user);

}
