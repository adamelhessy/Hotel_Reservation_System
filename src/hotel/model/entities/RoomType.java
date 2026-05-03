package hotel.model.entities;
import hotel.model.enums.*;

import java.io.Serializable;

public class RoomType implements Serializable
{
    private String typeName;
    private RoomView roomView;
    private double pricePerNight;
    private String description;
    private double seasonMultiplier;
    private double basePrice;
    private int maxCapacity;


    public RoomType() {}

    public RoomType(String typeName, double pricePerNight, RoomView roomView, String description, double seasonMultiplier,double basePrice, int maxCapacity)
    {
        this.typeName = typeName;
        this.roomView = roomView;
        this.pricePerNight = pricePerNight;
        this.description = description;
        this.seasonMultiplier = seasonMultiplier;
        this.basePrice=basePrice;
        this.maxCapacity = maxCapacity;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getSeasonMultiplier() {
        return seasonMultiplier;
    }

    public void setSeasonMultiplier(double seasonMultiplier) 
    {
        this.seasonMultiplier = seasonMultiplier;
        this.pricePerNight = this.basePrice * seasonMultiplier;     
    }

    public RoomView getRoomView() {
        return roomView;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public double getEffectivePrice() {
        return basePrice * seasonMultiplier;
    }
    public void setRoomView(RoomView roomView){this.roomView = roomView;}

}

