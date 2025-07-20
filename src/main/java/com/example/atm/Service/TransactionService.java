package com.example.atm.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.atm.entity.Transaction;
import com.example.atm.entity.User;
import com.example.atm.repository.TransactionRepository;
import com.example.atm.repository.UserRepository;

import jakarta.persistence.criteria.Predicate;

/**
 * 取引に関するビジネスロジックを提供するサービスクラス
 * 入金・出金・振込などの記録を管理する
 */
@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 指定したユーザーの取引履歴を取得(新しい順に並び替え)
     */
    public List<Transaction> getUserTransactions(User user) {
        return transactionRepository.findByUserOrderByTransactionTimeDesc(user);
    }
    
    
    public Specification<Transaction> buildSpecification(User user, String type, LocalDateTime startDate, LocalDateTime endDate, String counterpart) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // ユーザーIDで絞り込み（必須）
            predicates.add(cb.equal(root.get("user").get("id"), user.getId()));

            
            // 取引種別の条件（type=nullなら絞り込みしない）
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            
            // 期間の条件
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("transactionTime"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("transactionTime"), endDate));
            }
            
            // 相手ユーザー名（LIKE検索、nullなら条件なし）
            if (counterpart != null) {
                predicates.add(cb.like(root.get("counterpartUsername"), counterpart));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }


    
   public List<Transaction> searchTransactions(User user, String type, LocalDateTime startDate, LocalDateTime endDate, String counterpart) {
       Specification<Transaction> spec = buildSpecification(user, type, startDate, endDate, counterpart);
       
       Sort sort = Sort.by(Sort.Direction.DESC, "transactionTime");
       return transactionRepository.findAll(spec, sort);
   }

   
    /**
     * 単一の取引を記録する
     */
    public void recordTransaction(String username, String type, int amount) {
        // ユーザー名からユーザーエンティティを取得(存在しなければ例外)
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません: " + username));
        
            //取引情報の生成
            Transaction transaction = new Transaction();
            transaction.setUser(user);
            transaction.setType(type);
            transaction.setAmount(amount);
            transaction.setTransactionTime(LocalDateTime.now());
            
            //DBに保存
            transactionRepository.save(transaction);
    }

}
