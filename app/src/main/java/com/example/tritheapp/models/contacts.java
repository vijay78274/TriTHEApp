package com.example.tritheapp.models;

public class contacts {
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    String phoneNumber;
    contacts(){
    }
    public contacts(String phone){
        this.phoneNumber = phone;
    }
}
