package com.wnd.myapp.lenovate;

/**
 * Created by Dhruv on 23-03-2016.
 */
public class CartItems {


        String itemname,price,cardtype,url,itemid,qty, type, bookid, date;

        CartItems(){

        }

    public void setItemname(String name){
            this.itemname = name;
        }
    public void setqty(String quantity){
        this.qty = quantity;
    }
    public void setprice(String price){
        this.price = price;
    }
    public void setitemid(String item){
        this.itemid = item;
    }
    public void setcard(String crd){
        this.cardtype = crd;
    }
    public void seturl(String url){
        this.url = url;
    }
    public void settype(String typ){this.type = typ;}
    public void setBookid(String typ){this.bookid = typ;}
    public void setDate(String typ){this.date = typ;}

    public String gettype(){return this.type;}
    public String getItemname(){
        return this.itemname;
    }
    public String getprice(){
        return this.price;
    }
    public String getqty(){
        return this.qty;
    }
    public String getCardtype(){
        return this.cardtype;
    }
    public String geturl(){
        return this.url;
    }
    public String getDate(){
        return this.date;
    }
    public String getBookid(){
        return this.bookid;
    }

    public String getitemid(){
        return this.itemid;
    }

}
