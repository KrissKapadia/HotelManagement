package com.example.demo.models;

public class Guest {
    private String fullName;
    private String phoneNumber;
    private String email;
    private String address;
    private String province;
    private String city;
    private String postalCode;
    private String idProofType;
    private String idProofNumber;

    public Guest(String fullName, String phoneNumber, String email, String address, String province, String city, String postalCode, String idProofType, String idProofNumber) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.province = province;
        this.city = city;
        this.postalCode = postalCode;
        this.idProofType = idProofType;
        this.idProofNumber = idProofNumber;
    }

    // --- Getters ---
    public String getFullName() {
        return fullName;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getEmail() {
        return email;
    }
    public String getAddress() {
        return address;
    }
    public String getProvince() {
        return province;
    }
    public String getCity() {
        return city;
    }
    public String getPostalCode() {
        return postalCode;
    }
    public String getIdProofType() {
        return idProofType;
    }
    public String getIdProofNumber() {
        return idProofNumber;
    }

    // --- Setters (New additions) ---
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setProvince(String province) {
        this.province = province;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    public void setIdProofType(String idProofType) {
        this.idProofType = idProofType;
    }
    public void setIdProofNumber(String idProofNumber) {
        this.idProofNumber = idProofNumber;
    }
}
