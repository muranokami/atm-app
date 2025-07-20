package com.example.atm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.atm.entity.User;

/**
 * ユーザー情報（Userエンティティ）に対するデータアクセス用リポジトリインターフェース
 * JpaRepositoryを継承して基本的なCRUD操作を提供する
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * 指定したユーザー名でユーザーを検索する
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 指定したメールアドレスが既に存在するかを判定する
     */
    boolean existsByEmail(String email);
    
    /**
     * 指定したユーザー名が既に存在するかを判定する
     */
    boolean existsByUsername(String username);
}