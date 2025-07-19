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

@Entity
@Table(name = "transactions")
public class Transaction {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String type;
  private Integer amount;
  @Column(name = "transaction_time")
  private LocalDateTime transactionTime = LocalDateTime.now();
  
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
  
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
  
  @PrePersist
  protected void onCreate() {
      this.transactionTime = LocalDateTime.now();
  }
}
