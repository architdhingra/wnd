package com.wnd.myapp.lenovate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

/**
 * Created by dhruv sharma on 9/1/2016.
 */
public class homePopAdapter extends RecyclerView.Adapter<homePopHolderViewHolder> {
    public ImageView iconPlace;
    Context context;
    List<GalleryItemObject> iList;

    public homePopAdapter(Context c, List<GalleryItemObject> itemLis) {
        super();
        this.context = c;
        this.iList = itemLis;

    }

    @Override
    public homePopHolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View lv = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_icon_container, null);
        homePopHolderViewHolder hvR = new homePopHolderViewHolder(lv, context);
        return hvR;
    }

    @Override
    public void onBindViewHolder(final homePopHolderViewHolder holder, int position) {

        holder.tcv.setText(iList.get(position).getName());

        Picasso.with(context)
                .load(iList.get(position).getPhoto())
                .error(R.drawable.placeholder)
                .into(holder.categoryPhoto);

    }

    @Override
    public int getItemCount() {
        return this.iList.size();
    }
}
