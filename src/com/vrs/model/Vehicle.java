package com.vrs.model;

public class Vehicle {
   private int vehicleId;
    private String make;
    private String model;
    private String type;
    private String regNumber;
    private double dailyRate;
    private String status;
    private String insuranceExpiry;
    private String registrationExpiry;


public Vehicle(int vehicleId, String make, String model,
        String type, String regNumber, double dailyRate,
        String status, String insuranceExpiry,
        String registrationExpiry) {

this.vehicleId = vehicleId;
this.make = make;
this.model = model;
this.type = type;
this.regNumber = regNumber;
this.dailyRate = dailyRate;
this.status = status;
this.insuranceExpiry = insuranceExpiry;
this.registrationExpiry = registrationExpiry;}
public int getVehicleId() {
    return vehicleId;
}

public String getMake() {
    return make;
}

public String getModel() {
    return model;
}

public String getType() {
    return type;
}

public String getRegNumber() {
    return regNumber;
}

public double getDailyRate() {
    return dailyRate;
}

public String getStatus() {
    return status;
}

public String getInsuranceExpiry() {
    return insuranceExpiry;
}

public String getRegistrationExpiry() {
    return registrationExpiry;
}
public void setVehicleId(int vehicleId) {
    this.vehicleId = vehicleId;
}

public void setMake(String make) {
    this.make = make;
}

public void setModel(String model) {
    this.model = model;
}

public void setType(String type) {
    this.type = type;
}

public void setRegNumber(String regNumber) {
    this.regNumber = regNumber;
}

public void setDailyRate(double dailyRate) {
    this.dailyRate = dailyRate;
}

public void setStatus(String status) {
    this.status = status;
}

public void setInsuranceExpiry(String insuranceExpiry) {
    this.insuranceExpiry = insuranceExpiry;
}

public void setRegistrationExpiry(String registrationExpiry) {
    this.registrationExpiry = registrationExpiry;
}
}

