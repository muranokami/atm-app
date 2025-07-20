package com.example.atm.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * 取引情報を表すエンティティクラス
 * 各ユーザーが行った入金・出金・振込などの情報を格納
 */
@Entity
@Table(name = "transactions")
public class Transaction {
  // 主キー(自動採番)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  //取引の種類(入金・出金・振込など)
  private String type;
  
  //金額
  private Integer amount;
  
  //取引日時(デフォルトは現在時刻)
  @Column(name = "transaction_time")
  private LocalDateTime transactionTime = LocalDateTime.now();
  
  //対象ユーザー(多対一のリレーション)
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
  
  //振込の相手先ユーザー名(振込や受取の場合に利用)
  @Column(name = "counterpart_username")
  private String counterpartUsername;
  
  
  // --- Getter/Setter ---
  
  
  public String getCounterpartUsername() {
      return counterpartUsername;
  }
  
  public void setCounterpartUsername(String counterpartUsername) {
      this.counterpartUsername = counterpartUsername;
  }
  
  public Long getId() {
      return id;
  }
  
  public void setId(Long id) {
      this.id = id;
  }
  
  public String getType() {
      return type;
  }
  
  public void setType(String type) {
      this.type = type;
  }
  
  public Integer getAmount() {
      return amount;
  }
  
  public void setAmount(Integer amount) {
      this.amount = amount;
  }
  
  public LocalDateTime getTransactionTime() {
      return transactionTime;
  }
  
  public void setTransactionTime(LocalDateTime transactionTime) {
      this.transactionTime = transactionTime;
  }
  
  public User getUser() {
      return user;
  }
  
  public void setUser(User user) {
      this.user = user;
  }
  
  /**
   * エンティティ保存前に取引時刻を自動設定
   */
  @PrePersist
  protected void onCreate() {
      this.transactionTime = LocalDateTime.now();
  }
}
