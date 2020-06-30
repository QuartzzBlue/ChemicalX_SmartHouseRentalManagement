package com.example.jiptalk.vo;

public class Account {
    String bank;
    String accountNum;
    String depositor;

    public Account() {
    }

    public Account(String bank, String accountNum, String depositor) {
        this.bank = bank;
        this.accountNum = accountNum;
        this.depositor = depositor;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    public String getDepositor() {
        return depositor;
    }

    public void setDepositor(String depositor) {
        this.depositor = depositor;
    }

    @Override
    public String toString() {
        return "Account{" +
                "bank='" + bank + '\'' +
                ", accountNum='" + accountNum + '\'' +
                ", depositor='" + depositor + '\'' +
                '}';
    }
}
