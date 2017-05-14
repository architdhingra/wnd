package com.wnd.myapp.lenovate;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.docViewHolder> {

    private ArrayList<CartItems> arrList = new ArrayList<>();
    Context con;

    CartAdapter(ArrayList<CartItems> arrList, Context context){

        this.con = context;
        this.arrList = arrList;
        notifyItemChanged(0,arrList.size());
    }

    public static class docViewHolder extends RecyclerView.ViewHolder{
        public View itemview;
        CardView cv;
        TextView itemName,id;
        ImageView photo;
        TextView tprice, typeText, bookid, date;




        public docViewHolder(final View itemView) {
            super(itemView);
            this.itemview = itemView;
            cv = (CardView)itemView.findViewById(R.id.cv_nearby);
            itemName = (TextView)itemView.findViewById(R.id.name);
            tprice = (TextView)itemView.findViewById(R.id.pricetxt);
            id = (TextView)itemView.findViewById(R.id.itemsid);
            photo = (ImageView) itemView.findViewById(R.id.photo);
            typeText = (TextView) itemView.findViewById(R.id.typetext);
            date = (TextView) itemView.findViewById(R.id.date_value);
            bookid = (TextView) itemView.findViewById(R.id.bookid);

            }
    }


    @Override
    public docViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = null;

        if(arrList.get(i).getCardtype().equals("cart")) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_cart, viewGroup, false);
        }else{

            if(arrList.get(i).getCardtype().equals("hist_details")){
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_historydetails, viewGroup, false);
                v.setLayoutParams(new LinearLayout.LayoutParams(600, 500));
            }else if(arrList.get(i).getCardtype().equals("hist")){
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_hist, viewGroup, false);
            }
        }
        docViewHolder pvh = new docViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(docViewHolder DocViewHolder, final int i) {
        if (arrList.get(i).getCardtype().equals("hist")){
            DocViewHolder.bookid.setText(arrList.get(i).getBookid());
            DocViewHolder.tprice.setText(arrList.get(i).getprice());
            DocViewHolder.date.setText(arrList.get(i).getDate());
        }
        else {
            DocViewHolder.itemName.setText(arrList.get(i).getItemname() + "  ( x" + arrList.get(i).getqty() + ")");
            DocViewHolder.tprice.setText(arrList.get(i).getprice());
            if (arrList.get(i).getCardtype().equals("cart")) {
                if (arrList.get(i).gettype().equals("sample")) {
                    DocViewHolder.typeText.setVisibility(View.VISIBLE);
                }
                DocViewHolder.id.setText(arrList.get(i).getitemid());
            }

            Picasso.with(con)
                    .load(arrList.get(i).geturl())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(DocViewHolder.photo);

        }
        DocViewHolder.itemview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (arrList.get(i).getCardtype().equals("hist")) {
                    Intent inte = new Intent(con , OrderDetails.class);
                    inte.putExtra("date", arrList.get(i).getDate());
                    inte.putExtra("bid", arrList.get(i).getBookid());
                    inte.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    con.startActivity(inte);
                }

                if (arrList.get(i).getCardtype().equals("cart")) {
                    Intent inte = new Intent(con, ProductInfo.class);
                    inte.putExtra("gallery", "cart");
                    inte.putExtra("itemId",Integer.parseInt(arrList.get(i).getitemid()));
                    inte.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    con.startActivity(inte);
                }

            }

        });



    }

    @Override
    public int getItemCount() {

        return arrList.size();
    }


}





