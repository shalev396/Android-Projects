package com.example.fleemarket;

public class Seller  {
    private int soldCount,offersCount;
    private float rate;
    private String city,userID;

    public Seller() {}
    public Seller(int soldCount,float rate,String city,String userID,int offersCount)
    {

        this.soldCount=soldCount;
        this.rate=rate;
        this.city=city;
        this.userID=userID;
        this.offersCount=offersCount;
    }

    public int getSoldCount() {
        return soldCount;
    }

    public void setSoldCount(int soldCount) {
        this.soldCount = soldCount;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getOffersCount() {
        return offersCount;
    }

    public void setOffersCount(int offersCount) {
        this.offersCount = offersCount;
    }

    @Override
    public String toString() {
        return "Seller{" +
                "soldCount=" + soldCount +
                ", rate=" + rate +
                ", city='" + city + '\'' +
                '}';
    }
}
