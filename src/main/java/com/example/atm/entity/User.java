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

@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(unique = true, nullable = false)
  private String username;
  
  @Column(nullable = false)
  private String password;
  
  private String pin;
  private int balance;
  
  @Column(unique = true)
  private String email;
  
  @Column(name = "login_time")
  private LocalDateTime loginTime;
  
  @Column(name = "created_at")
  private LocalDateTime createdAt = LocalDateTime.now();
  
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Transaction> transactions = new ArrayList<>();
  
  public User() {}
  
  public User(String username, String pin, int balance) {
      this.username = username;
      this.pin = pin;
      this.balance = balance;
  }
  
  @PrePersist
  protected void onCreate() {
      this.createdAt = LocalDateTime.now();
  }
  
  
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
