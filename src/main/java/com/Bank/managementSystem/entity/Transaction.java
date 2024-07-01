package com.Bank.managementSystem.entity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Embeddable
public class Transaction {
    private String type;


    public void setSender(Long sender) {
        this.Sender = sender;
    }

    public void setReciever(Long reciever) {
        this.Reciever = reciever;
    }

    private int amount;
    private final LocalDateTime date;
    private long Sender;
    private long Reciever;

    public LocalDateTime getDate() {
        return date;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public long getSender() {
        return Sender;
    }

    public void setSender(long sender) {
        Sender = sender;
    }

    public long getReciever() {
        return Reciever;
    }

    public void setReciever(long reciever) {
        this.Reciever = reciever;
    }

    public Transaction(){
        this.date = LocalDateTime.now();
    }

    public Transaction(String type, int amount, LocalDateTime date, Long sender, Long reciever){
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.Sender = sender;
        this.Reciever = reciever;
    }

    public Transaction(String type, int amount, LocalDateTime date, long accountId) {
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.Reciever = accountId;
    }

    public Transaction(String type, int amount, LocalDateTime date){
        this.type = type;
        this.amount = amount;
        this.date = date;
    }

    public String getType(){return type;
    }

    public int getAmount(){return amount;
    }

    @Override
    public String toString(){
//        return type + amount + " |" + " Time : " + date + " |" + " " + "UserTo/From: " + Id + " |" + " (NOTE: 0- Means self transfer)";

        if (Reciever == 0) {return  "Self" +  " "  + type+ amount ;}
        else {return  "From " + Sender + " To " + Reciever +  " " + type + amount;}
    }
}