package com.wnd.myapp.lenovate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by Sachin Kharb on 9/13/2016.
 */
public class ShopItemObjectViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener {
    NetworkImageView productImage;
    TextView prodName;
    TextView prodID;
    Context c;
    TextView prodPrice;
    public  ShopItemObjectViewHolder(View itemView, Context c)
    {super(itemView);
        this.c =c;
        itemView.setOnClickListener(this);
        productImage =  (NetworkImageView) itemView.findViewById(R.id.product_photo);
        prodName = (TextView) itemView.findViewById(R.id.product_name);
        prodPrice = (TextView) itemView.findViewById(R.id.product_price);
        prodID = (TextView) itemView.findViewById(R.id.shopIdHolder);
    }
    @Override
    public void onClick(View v) {
        Intent i = new Intent(c, ProductInfo.class);
        Log.d("clicked item", prodID.getText().toString());
        i.putExtra("itemId", Integer.parseInt(prodID.getText().toString()));
        i.putExtra("prodName", prodName.getText().toString());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("gallery","no");
        c.startActivity(i);
    }
}
