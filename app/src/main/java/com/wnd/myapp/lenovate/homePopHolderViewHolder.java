package com.wnd.myapp.lenovate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Sachin Kharb on 9/1/2016.
 */
public class homePopHolderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView tcv;
    public ImageView categoryPhoto;
    Context c;

    public homePopHolderViewHolder(View itemView, Context c) {

        super(itemView);
        itemView.setOnClickListener(this);
        //  itemView.setOnTouchListener(new RippleClickListener().setCardView(itemView));
        this.c = c;

        tcv = (TextView) itemView.findViewById(R.id.category_name);
        categoryPhoto = (ImageView) itemView.findViewById(R.id.category_photo);
    }

    @Override
    public void onClick(View v) {
        Log.d("card clicked","card clicked");
        Intent i = new Intent(c, SubCategoryGallery.class);
        i.putExtra("fromclass", "home");
        i.putExtra("categ", tcv.getText().toString());
        i.putExtra("type", "sampler");
        c.startActivity(i);
    }
}

