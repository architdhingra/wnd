package com.wnd.myapp.lenovate.tester;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.wnd.myapp.lenovate.R;

/**
 * Created by Sachin Kharb on 8/30/2016.
 */
public class ExpandParentHolder extends ParentViewHolder{
    public TextView mCrimeTitleTextView;
    public ImageButton mParentDropDownArrow;

    public ExpandParentHolder(View itemView) {
        super(itemView);

        mCrimeTitleTextView = (TextView) itemView.findViewById(R.id.parent_list_item_crime_title_text_view);
        mParentDropDownArrow = (ImageButton) itemView.findViewById(R.id.parent_list_item_expand_arrow);
    }
}
