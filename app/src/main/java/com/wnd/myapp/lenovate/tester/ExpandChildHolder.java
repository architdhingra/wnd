package com.wnd.myapp.lenovate.tester;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.wnd.myapp.lenovate.R;

/**
 * Created by Sachin Kharb on 8/30/2016.
 */
public class ExpandChildHolder  extends ChildViewHolder {

    public TextView mCrimeDateText;
    public ImageView mCrimeSolvedCheckBox;

    public ExpandChildHolder(View itemView) {
        super(itemView);

        mCrimeDateText = (TextView) itemView.findViewById(R.id.category_name);
        mCrimeSolvedCheckBox = (ImageView) itemView.findViewById(R.id.category_photo);
    }
}