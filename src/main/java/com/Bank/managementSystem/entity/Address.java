package com.Bank.managementSystem.entity;

import jakarta.persistence.Embeddable;
import org.springframework.stereotype.Component;

@Embeddable
public class Address {
    private String line1;
    private String line2;
    private Long zip;

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public Long getZip() {
        return zip;
    }

    public void setZip(Long zip) {
        this.zip = zip;
    }


}
