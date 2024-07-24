package com.example.fleemarket;

public class ItemForFree extends Item {

    protected boolean isDelivery, isPickup;

    public ItemForFree() { }

    public ItemForFree(String name, int price, float rate, boolean isDelivery, boolean isPickup, String userUid) {
        super(name,price,rate,userUid);

        this.isDelivery = isDelivery;
        this.isPickup = isPickup;

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

    @Override
    public String toString() {
        return "ItemForFree{" +
                "isDelivery=" + isDelivery +
                ", isPickup=" + isPickup +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", sellerUID='" + sellerUID + '\'' +
                ", picRef='" + picRef + '\'' +
                ", itemId='" + itemId + '\'' +
                ", rate=" + rate +
                '}';
    }
}
