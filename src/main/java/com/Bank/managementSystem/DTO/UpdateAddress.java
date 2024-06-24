package com.Bank.managementSystem.DTO;

import com.Bank.managementSystem.entity.Address;


public class UpdateAddress {
    private Address address;

    @Override
    public String toString() {
        return "UpdateAddress{" +
                "address=" + address +
                '}';
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
