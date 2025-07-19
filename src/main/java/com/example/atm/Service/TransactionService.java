package com.example.atm.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.atm.entity.Transaction;
import com.example.atm.entity.User;
import com.example.atm.repository.TransactionRepository;
import com.example.atm.repository.UserRepository;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<Transaction> getUserTransactions(User user) {
        return transactionRepository.findByUserOrderByTransactionTimeDesc(user);
    }
    
    public void recordTransaction(String username, String type, int amount) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません: " + username));

            Transaction transaction = new Transaction();
            transaction.setUser(user);
            transaction.setType(type);
            transaction.setAmount(amount);
            transaction.setTransactionTime(LocalDateTime.now());

            transactionRepository.save(transaction);
    }

}
