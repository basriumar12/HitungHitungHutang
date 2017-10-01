package com.tiyanrcode.bon.model;

import java.io.Serializable;

/**
 * Created by sulistiyanto on 19-Apr-15.
 */
public class Transaction implements Serializable {

    public static final String TAG = "Transasction";

    private long id;
    private String date;
    private int credit;
    private int pay;
    private int saldo;
    private Customer customer;

    public Transaction() {
    }

    public Transaction(String date, int credit, int pay, int saldo, Customer customer) {
        this.date = date;
        this.credit = credit;
        this.pay = pay;
        this.saldo = saldo;
        this.customer = customer;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public int getPay() {
        return pay;
    }

    public void setPay(int pay) {
        this.pay = pay;
    }

    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
