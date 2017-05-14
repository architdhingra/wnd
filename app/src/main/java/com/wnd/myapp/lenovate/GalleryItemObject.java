package com.wnd.myapp.lenovate;

/**
 * Created by Sachin Kharb on 8/25/2016.
 */
public class GalleryItemObject  {

    private String name;
    private String photo;
    private int ID;
    private String showtext;
    private String categ_filter[];

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public GalleryItemObject(String name, String photo, int id, String text) {
        this.name = name;
        this.photo = photo;
        this.ID = id;
        this.showtext = text;
    }

    public String getName() {
        return name;
    }

    public String getshowtext() {
        return showtext;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}

