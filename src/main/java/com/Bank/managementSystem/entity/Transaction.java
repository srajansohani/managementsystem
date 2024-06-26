package com.Bank.managementSystem.entity;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
public class Transaction {
    private String type;

    public LocalDateTime getDate() {
        return date;
    }

    private int amount;
    private final LocalDateTime date;
    private long Id;
//    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy HH:mm:ss");

    public Transaction(){
        this.date = LocalDateTime.now();
    }

    public Transaction(String type, int amount, LocalDateTime date, long accountId) {
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.Id = accountId;
    }

    public Transaction(String type, int amount, LocalDateTime date){
        this.type = type;
        this.amount = amount;
        this.date = date;
    }

    public String getType(){
        return type;
    }

    public int getAmount(){
        return amount;
    }

    @Override
    public String toString(){
        return type + amount + " Time : " + date + " " + "UserTo/From: " + Id + " (NOTE: 0- Means self transfer)";
    }
}