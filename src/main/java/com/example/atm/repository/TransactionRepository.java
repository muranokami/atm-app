package com.example.atm.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.atm.entity.Transaction;
import com.example.atm.entity.User;

/**
 * 取引履歴（Transaction）エンティティに対するデータアクセス用リポジトリインターフェース
 * 指定条件に基づいて、ユーザーの取引履歴を絞り込み取得するカスタムメソッドを定義
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
    List<Transaction> findByUserOrderByTransactionTimeDesc(User user);
    /**
     * 指定されたユーザーの取引履歴を、検索条件に応じて取得する。
     * 条件：
     * - 取引種別（type）が一致する場合
     * - 開始日以降の取引
     * - 終了日以前の取引
     * - 対象ユーザー名（counterpartUsername）を部分一致で検索
     * 
     * 結果は取引日時の降順（新しい順）で返される。
     */
    @Query("""
            select t from Transaction t
            where t.user = :user
            and (:type is null or t.type =:type)
            and (:startDate is null or t.transactionTime >= :startDate)
            and (:endDate is null or t.transactionTime <= :endDate)
            and (:counterpart is null or t.counterpartUsername like :counterpart)
            order by t.transactionTime desc
            """)
    
    List<Transaction> findByUserAndFilter(
            @Param("user") User user,
            @Param("type") String type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("counterpart")String counterpart);
    
 

    

}
