package com.example.atm.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * ユーザー情報を表すエンティティクラス
 * ATMアプリ内の各ユーザーの基本情報を保持する
 */
@Entity
@Table(name = "users")
public class User {
    
  //ユーザーID（主キー、自動生成）
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  //ユーザー名(ユニーク制約、必須)
  @Column(unique = true, nullable = false)
  private String username;
  
  //パスワード（必須）
  @Column(nullable = false)
  private String password;
  
  //暗証番号（PINコード）
  private String pin;
  
  //残高
  private int balance;
  
  //メールアドレス（ユニーク制約あり）
  @Column(unique = true)
  private String email;
  
  //最終ログイン時刻
  @Column(name = "login_time")
  private LocalDateTime loginTime;
  
  //登録日時
  @Column(name = "created_at")
  private LocalDateTime createdAt = LocalDateTime.now();
  
  //このユーザーに紐づく取引履歴（1対多）
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Transaction> transactions = new ArrayList<>();
  
  //--- コンストラクタ ---
  public User() {}
  
  //残高・PIN・ユーザー名を指定するコンストラクタ
  public User(String username, String pin, int balance) {
      this.username = username;
      this.pin = pin;
      this.balance = balance;
  }
  
  //エンティティ保存前に作成日時を設定
  @PrePersist
  protected void onCreate() {
      this.createdAt = LocalDateTime.now();
  }
  
  
  //--- Getter/Setter ---
  
  public Long getId() {
      return id;
  }
  
  public void setId(Long id) {
      this.id = id;
  }
  
  public String getUsername() {
      return username;
  }
  
  public void setUsername(String username) {
      this.username = username;
  }
  
  public String getEmail() {
      return email;
  }
  
  public void setEmail(String email) {
      this.email = email;
  }
  
  public String getPassword() {
      return password;
  }
  
  public void setPassword(String password) {
      this.password = password;
  }
  
  public String getPin() {
      return pin;
  }
  
  public void setPin(String pin) {
      this.pin = pin;
  }
  
  public int getBalance() {
      return balance;
  }
  
  public void setBalance(int balance) {
      this.balance = balance;
  }
  
  public LocalDateTime getLoginTime() {
      return loginTime;
  }
  
  public void setLoginTime(LocalDateTime loginTime) {
      this.loginTime = loginTime;
  }
  
  public LocalDateTime getCreateAt() {
      return createdAt;
  }
   
  public List<Transaction> getTransactions() {
      return transactions;
  }
  
  public void setTransactions(List<Transaction> transactions) {
      this.transactions = transactions;
  }

  
}
