package com.example.wire.model;

import java.io.Serializable;

public class MachineModel implements Serializable {
    String machineId, machineName, brandName, weightCapacity, color, price, details, image, status, userId, bookingDateTime, bookingAddress;

    public MachineModel() {
    }

    public MachineModel(String machineId, String machineName, String brandName, String weightCapacity, String color, String price, String details, String image, String status, String userId, String bookingDateTime, String bookingAddress) {
        this.machineId = machineId;
        this.machineName = machineName;
        this.brandName = brandName;
        this.weightCapacity = weightCapacity;
        this.color = color;
        this.price = price;
        this.details = details;
        this.image = image;
        this.status = status;
        this.userId = userId;
        this.bookingDateTime = bookingDateTime;
        this.bookingAddress = bookingAddress;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getWeightCapacity() {
        return weightCapacity;
    }

    public void setWeightCapacity(String weightCapacity) {
        this.weightCapacity = weightCapacity;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBookingDateTime() {
        return bookingDateTime;
    }

    public void setBookingDateTime(String bookingDateTime) {
        this.bookingDateTime = bookingDateTime;
    }

    public String getBookingAddress() {
        return bookingAddress;
    }

    public void setBookingAddress(String bookingAddress) {
        this.bookingAddress = bookingAddress;
    }
}
