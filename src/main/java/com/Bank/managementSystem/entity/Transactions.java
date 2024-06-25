package com.Bank.managementSystem.entity;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
public class Transactions {
    private String type;
    private int amount;
    private final LocalDateTime date;
    private long Id;
//    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy HH:mm:ss");

    public Transactions(){
        this.date = LocalDateTime.now();
    }

    public Transactions(String type, int amount, LocalDateTime date, long accountId) {
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.Id = accountId;
    }

    public Transactions(String type, int amount, LocalDateTime date){
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
        return type + amount + " Time : " + date + " " + Id;
    }
}