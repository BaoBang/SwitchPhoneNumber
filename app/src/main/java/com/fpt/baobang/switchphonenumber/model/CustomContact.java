package com.fpt.baobang.switchphonenumber.model;

public class CustomContact {
    private String contactId;
    private String name;
    private Phone phoneNumbers;
    private boolean isEdit = false;

    public CustomContact(String contactId, String name, Phone phoneNumbers) {
        this.contactId = contactId;
        this.name = name;
        this.phoneNumbers = phoneNumbers;
    }

    public CustomContact() {

    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Phone getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(Phone phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
}
