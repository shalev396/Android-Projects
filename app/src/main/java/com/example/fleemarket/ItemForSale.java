package com.example.fleemarket;

public class ItemForSale extends Item {
    protected boolean isDelivery, isPickup,isPayPal,isCash,isBit;



    public ItemForSale() { }



    public ItemForSale(String name, int price, float rate, boolean isDelivery, boolean isPickup, boolean isPayPal, boolean isCash, boolean isBit, String userUid) {
        super(name,price,rate,userUid);

        this.isDelivery = isDelivery;
        this.isPickup = isPickup;
        this.isPayPal = isPayPal;
        this.isCash = isCash;
        this.isBit = isBit;

    }



    public boolean isDelivery() {
        return isDelivery;
    }

    public void setDelivery(boolean delivery) {
        isDelivery = delivery;
    }

    public boolean isPickup() {
        return isPickup;
    }

    public void setPickup(boolean pickup) {
        isPickup = pickup;
    }

    public boolean isPayPal() {
        return isPayPal;
    }

    public void setPayPal(boolean payPal) {
        isPayPal = payPal;
    }

    public boolean isCash() {
        return isCash;
    }

    public void setCash(boolean cash) {
        isCash = cash;
    }

    public boolean isBit() {
        return isBit;
    }

    public void setBit(boolean bit) {
        isBit = bit;
    }


    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", rate='" + rate + '\'' +
                ", isDelivery=" + isDelivery +
                ", isPickup=" + isPickup +
                ", isPayPal=" + isPayPal +
                ", isCash=" + isCash +
                ", isBit=" + isBit +
                '}';
    }
}
