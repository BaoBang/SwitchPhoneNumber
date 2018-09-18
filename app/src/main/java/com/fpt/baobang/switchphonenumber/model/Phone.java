package com.fpt.baobang.switchphonenumber.model;

public class Phone {
    private String phoneId;
    private String first;
    private String phone;
    private String type;
    private String newFirst;


    public Phone(String first, String phone, String type, String newFirst) {
        this.first = first;
        this.phone = phone;
        this.type = type;
        this.newFirst = newFirst;
    }

    public String getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(String phoneId) {
        this.phoneId = phoneId;
    }

    public Phone() {
    }
    public String getNewFirst() {
        return newFirst;
    }

    public void setNewFirst(String newFirst) {
        this.newFirst = newFirst;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
