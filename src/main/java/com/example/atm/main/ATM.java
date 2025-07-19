package com.example.atm.main;

public class ATM {
    private int balance;
    
    public ATM(int initialBalance) {
        this.balance = initialBalance;
    }
    
    public String deposit(int amount) {
        balance += amount;
        return "入金完了：" + amount + "円";
    }
    
    public String withdraw(int amount) {
        if(amount > balance) {
            return "残高不足です";
        }
        balance -= amount;
        return amount + "円を出金しました";
    }
    
    public String checkBalance() {
        return "現在の残高：" + balance + "円";
    }
  }