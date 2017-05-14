package com.wnd.myapp.lenovate;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

/**
 * Created by jaskaran singh on 8/5/2016.
 */
public class Swip_Image extends PagerAdapter {
    private Context ctx;
    String[] arrbannerimg, arrscat, scid;
    private LayoutInflater layoutInflater_swip_Image;

    public Swip_Image(Context ctx) {
        this.ctx = ctx;
    }

    public Swip_Image(Context applicationContext, String[] arrbannerimg, String[] arrscat, String[] scid) {
        this.ctx = applicationContext;
        this.arrscat = arrscat;
        this.scid = scid;
        this.arrbannerimg = arrbannerimg;
    }

    @Override
    public int getCount() {

        return arrscat.length;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        layoutInflater_swip_Image = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater_swip_Image.inflate(R.layout.swip_image_layout, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.bgheader);

        Picasso.with(ctx)
                .load(arrbannerimg[position])
                .placeholder(R.drawable.placeholder)
                .into(imageView);

        container.addView(itemView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ctx, "working" + position, Toast.LENGTH_LONG).show();
                Intent i = new Intent(ctx, SubCategoryGallery.class);
                if (Integer.parseInt(scid[position])>2000) {
                    i.putExtra("fromclass", "shop");
                }
                else if (Integer.parseInt(scid[position])<200){
                    i.putExtra("fromclass", "home");
                }
                else {
                    i.putExtra("fromclass", "gallery");
                }
                Log.d("arrscat", arrscat[position]);
                i.putExtra("categ", arrscat[position]);
                //next page has
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(i);
            }
        });
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

}