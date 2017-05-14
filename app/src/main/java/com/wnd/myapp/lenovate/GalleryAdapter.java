package com.wnd.myapp.lenovate;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Sachin Kharb on 8/25/2016.
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryViewHolder> {

    private List<GalleryItemObject> itemList;
    private Context context;

    String from;
    boolean isSubcategoryView;
    int subID;
    boolean prod;

    public GalleryAdapter(Context context, List<GalleryItemObject> itemList, boolean f, boolean prod, String gallery, int subId) {
        this.itemList = itemList;
        this.context = context;
        this.isSubcategoryView = f;
        this.subID = subId;
        this.prod = prod;
        from = gallery;
    }

    @Override
    public GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_card, null);

        GalleryViewHolder rcv = new GalleryViewHolder(layoutView, context, isSubcategoryView, prod, from, subID);
        return rcv;
    }

    String s;

    @Override
    public void onBindViewHolder(final GalleryViewHolder holder, final int position) {

        s = Integer.toString(itemList.get(position).getID());

        if (itemList.get(position).getshowtext().equals("no")) {
            holder.categoryName.setVisibility(View.GONE);
            holder.categoryName.setText(itemList.get(position).getName());
        } else {
            holder.categoryName.setText(itemList.get(position).getName());
        }
        Log.d("Gallery adapter","loading image: "+ itemList.get(position).getPhoto() );

        //Loadimg loadimg = new Loadimg(context, itemList.get(position).getPhoto(), holder.categoryPhoto);
        //loadimg.start();
        Picasso.with(context)
                .load(itemList.get(position).getPhoto())
                .placeholder(R.drawable.placeholder)
                .into(holder.categoryPhoto);
        //Picasso.with(context).setLoggingEnabled(true);



        if (isSubcategoryView || prod) {
            s = Integer.toString(itemList.get(position).getID());
            holder.subCatIdHolder.setText(s);
        }
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    /*class Loadimg extends Thread {
        Context con;
        String url;
        ImageView imgview;

        Loadimg(Context context, String url, ImageView imgview) {
            this.con = context;
            this.url = url;
            this.imgview = imgview;
        }

        public void run() {

            Picasso.with(this.con)
                    .load(url)
                    .placeholder(R.drawable.placeholder)
                    .into(imgview);
        }
    }*/
}