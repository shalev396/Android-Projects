package com.example.fleemarket;

public class Item {
    protected String name;
    protected int price;
    protected String sellerUID,picRef,itemId;
    protected float rate;


    public Item() { }



    public Item(String name, int price, float rate, String userUid) {
        this.name = name;
        this.price = price;
        this.rate = rate;
        this.sellerUID =userUid;
        this.itemId="";
        this.picRef="";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }


    public String getPicRef() {
        return picRef;
    }

    public void setPicRef(String picRef) {
        this.picRef = picRef;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getSellerUID() {
        return sellerUID;
    }

    public void setSellerUID(String sellerUID) {
        this.sellerUID = sellerUID;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", rate='" + rate + '\'' +
                '}';
    }
}
