package com.wnd.myapp.lenovate;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

/**
 * Created by Sachin Kharb on 9/13/2016.
 */
public class ProductAdapter extends RecyclerView.Adapter<ShopItemObjectViewHolder> {

    NetworkImageView productImage;
    TextView prodName;
    TextView prodPrice, prodID;
    List<ShopItemObject>  allWoods;
    Context context;
    ImageLoader ilDr;

    public ProductAdapter(List<ShopItemObject> allWoods, Context context, ImageLoader imageLoader) {
        this.allWoods = allWoods;
        this.context = context;
        this.ilDr = imageLoader;
    }

    @Override
    public ShopItemObjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_card, null);
        ShopItemObjectViewHolder rcv = new ShopItemObjectViewHolder(layoutView,context );
        return rcv;
    }

    @Override
    public void onBindViewHolder(ShopItemObjectViewHolder holder, int position) {

        holder.prodName.setText(allWoods.get(position).getName());
        holder.prodPrice.setText(String.valueOf(allWoods.get(position).getPrice()));
        holder.productImage.setImageUrl(allWoods.get(position).getItemimg(),ilDr);
        Log.d("product adapter: ", "itemid: "  + allWoods.get(position).getItemID());
        holder.prodID.setText(String.valueOf(allWoods.get(position).getItemID()));

    }

    @Override
    public int getItemCount() {
        return allWoods.size();
    }
}
