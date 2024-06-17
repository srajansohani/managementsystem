package com.Bank.managementSystem.entity;
import jakarta.persistence.Embeddable;

@Embeddable
public class Transactions {
    private String type;
    private int amount;

    public Transactions(){}
    
    public Transactions(String type, int amount){
        this.type = type;
        this.amount = amount;
    }

    public String getType(){
        return type;
    }

    public int getAmount(){
        return amount;
    }

    @Override
    public String toString(){
        return type + amount;
    }
}