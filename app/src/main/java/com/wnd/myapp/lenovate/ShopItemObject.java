package com.wnd.myapp.lenovate;

/**
 * Created by Sachin Kharb on 9/13/2016.
 */
public class ShopItemObject {
    private String name;
    private int photo;
    private int itemID;
    private int SubID;
    private int Price;
    private String itemimg;
    private String itemDesc;

    public String getItemimg() {
        return itemimg;
    }

    public void setItemimg(String itemimg) {
        this.itemimg = itemimg;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public ShopItemObject(String name, int photo, int itemID, int subID, int price,String itemImg) {
        this.name = name;
        this.photo = photo;
        this.itemID = itemID;
        SubID = subID;
        Price = price;
        this.itemimg=itemImg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public int getSubID() {
        return SubID;
    }

    public void setSubID(int subID) {
        SubID = subID;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }
}
