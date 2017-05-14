package com.wnd.myapp.lenovate;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Sachin Kharb on 9/7/2016.
 */
public class SubCatAdapter extends RecyclerView.Adapter<GalleryViewHolder> {
    private List<GalleryItemObject> itemList;
    private Context context;
    gallery gal;
    boolean isFinalView;
    int cols;

    public SubCatAdapter(Context context, List<GalleryItemObject> itemList, gallery gal, boolean f, int cols) {
        this.itemList = itemList;
        this.context = context;
        this.gal = gal;
        this.isFinalView = f;
        this.cols = cols;
    }

    @Override
    public GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_card, null);
       // GalleryViewHolder rcv = new GalleryViewHolder(layoutView, context, isSubcategoryView, gal, cols);
        return null;
    }

    @Override
    public void onBindViewHolder(GalleryViewHolder holder, int position) {
        holder.categoryName.setText(itemList.get(position).getName());
        //holder.categoryPhoto.setImageResource(itemList.get(position).getPhoto());

    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}
