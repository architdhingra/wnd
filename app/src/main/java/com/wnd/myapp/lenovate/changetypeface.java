package com.wnd.myapp.lenovate;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Dhruv on 27-03-2016.
 */
public class changetypeface extends TextView
{
    public changetypeface(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        // Typeface.createFromAsset doesn't work in the layout editor. Skipping ...
        if (isInEditMode())
        {
            return;
        }

        TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.changetypeface);
        String fontName = styledAttrs.getString(R.styleable.changetypeface_typeface);
        //Log.d("typeface: ", fontName);
        styledAttrs.recycle();

        if (fontName != null)
        {
            Typeface typeface = Typeface.createFromAsset(context.getAssets(), "font/"+fontName);
            setTypeface(typeface);
        }
    }
}
