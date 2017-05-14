package com.wnd.myapp.lenovate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Dhruv Sharma on 8/25/2016.
 */
public class GalleryViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{

public TextView categoryName, subCatIdHolder;
public ImageView categoryPhoto;
boolean isSubCatView =false, prod=false;
Context c;
String from;
int suBID;


public GalleryViewHolder(View itemView, Context c, boolean  gotoFullScree,boolean product,String gallery,int SubID )
{
    super(itemView);
    itemView.setOnClickListener(this);

    if(gallery.equals("gallery")) {
        itemView.setLayoutParams(new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, 450));
    }else{
        itemView.setLayoutParams(new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, 350));
    }

    this.suBID =SubID;
    this.c=c;
    this.from = gallery;

    this.prod=product;
    this.isSubCatView =gotoFullScree;
    categoryName = (TextView)itemView.findViewById(R.id.category_name);
    categoryPhoto = (ImageView)itemView.findViewById(R.id.category_photo);
    subCatIdHolder = (TextView) itemView.findViewById(R.id.idHolder);
}

@Override
public void onClick(View view) {

   //from gallery to subcategories
    if (!isSubCatView && !prod) {
        Intent i = new Intent(c, SubCategoryGallery.class);
        if(from.equals("shop")) {
            i.putExtra("fromclass", "shop");

        }else if(from.equals("gallery")){
            i.putExtra("fromclass", "gallery");

        }else if(from.equals("home")){
            i.putExtra("fromclass", "home");
        }
        i.putExtra("categ", categoryName.getText().toString());
        //next page has
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(i);
    }


    //Subcategory to ProductListings
    if(isSubCatView && !prod) {
        Intent i = new Intent(c, ProductListings.class);
        i.putExtra("subCat", categoryName.getText().toString());

        if(from.equals("home")){
            i.putExtra("from","home");
        }else if(from.equals("shop")){
            i.putExtra("from","shop");
        }
        i.putExtra("subId",Integer.parseInt(subCatIdHolder.getText().toString()));  // subcategory id
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(i);
    }

    //PRoductListings to Individual Product
    if (!isSubCatView && prod) {

        Intent i=null;

        if(from.equals("gallery")){
            i = new Intent(c, zoomview.class);
            i.putExtra("gallery","gallery");        // coming from gallery
            i.putExtra("itemId",Integer.parseInt(subCatIdHolder.getText().toString()));        // subcategory id as item id coming from gallery
        }
        else if(from.equals("mycollections")){
            i = new Intent(c, zoomview.class);
            i.putExtra("gallery","mycollections");        // coming from mycollection
            i.putExtra("itemId",Integer.parseInt(subCatIdHolder.getText().toString()));       // subcategory id as item id coming from mycollections

        }
        else if(from.equals("home")){
            i = new Intent(c, ProductInfo.class);
            i.putExtra("gallery","home");             // coming from home
            i.putExtra("itemId",Integer.parseInt(subCatIdHolder.getText().toString()));
        }
        else if(from.equals("shop")){
            i = new Intent(c, ProductInfo.class);
            i.putExtra("gallery","shop");             // coming from shop
            i.putExtra("itemId",Integer.parseInt(subCatIdHolder.getText().toString()));
        }
        i.putExtra("prodName", categoryName.getText().toString());
        //i.putExtra("subId",suBID);                                                      // not being used
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(i);
    }
}
}
