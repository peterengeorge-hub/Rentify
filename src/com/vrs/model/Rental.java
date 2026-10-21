 package com.vrs.model;

public class Rental {
    private int rentalId;
    private int vehicleId;
    private int customerId;
    private String startDate;
    private String endDate;
    private String actualReturnDate;
    private double totalCharge;
    private double lateFee;
    private String status;

    public Rental(int rentalId, int vehicleId, int customerId,
                  String startDate, String endDate,
                  String actualReturnDate, double totalCharge,
                  double lateFee, String status) {
        this.rentalId = rentalId;
        this.vehicleId = vehicleId;
        this.customerId = customerId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.actualReturnDate = actualReturnDate;
        this.totalCharge = totalCharge;
        this.lateFee = lateFee;
        this.status = status;
    }

    public int getRentalId() {
        return rentalId;
    }

    public void setRentalId(int rentalId) {
        this.rentalId = rentalId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getActualReturnDate() {
        return actualReturnDate;
    }

    public double getTotalCharge() {
        return totalCharge;
    }

    public double getLateFee() {
        return lateFee;
    }

    public String getStatus() {
        return status;
    }
}