package com.example.atm.main;

/**
 * ATMの基本的な機能（入金、出金、残高照会）を提供するクラス。
 * コンソールやWebアプリで利用する簡易的なATMロジック。
 */
public class ATM {
    // 現在の残高を保持するフィールド
    private int balance;
    
    /**
     * 初期残高を設定するコンストラクタ
     */
    public ATM(int initialBalance) {
        this.balance = initialBalance;
    }
    
    /**
     * 入金処理を行うメソッド
     */
    public String deposit(int amount) {
        balance += amount;
        return "入金完了：" + amount + "円";
    }
    
    
    /**
     * 出金処理を行うメソッド
     */
    public String withdraw(int amount) {
        if(amount > balance) {
            return "残高不足です";
        }
        balance -= amount;
        return amount + "円を出金しました";
    }
    
    
    /**
     * 現在の残高を表示するメソッド
     */
    public String checkBalance() {
        return "現在の残高：" + balance + "円";
    }
  }