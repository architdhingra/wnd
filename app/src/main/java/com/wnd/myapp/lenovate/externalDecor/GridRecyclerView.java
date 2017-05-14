package com.wnd.myapp.lenovate.externalDecor;

/**
 * Created by Sachin Kharb on 8/29/2016.
 */import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * An extension of RecyclerView, focused more on resembling a GridView.
 * Unlike {@link android.support.v7.widget.RecyclerView}, this view can handle
 * {@code <gridLayoutAnimation>} as long as you provide it a
 * {@link android.support.v7.widget.GridLayoutManager} in
 * {@code setLayoutManager(LayoutManager layout)}.
 *
 * Created by Freddie (Musenkishi) Lust-Hed.
 */
public class GridRecyclerView extends RecyclerView {
    public boolean isAnimater() {
        return animater;
    }

    public void setAnimater(boolean animater) {
        this.animater = animater;
    }

    public boolean animater=true;
    public GridRecyclerView(Context context) {
        super(context);
    }

    public GridRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
/*
    @Override
    public void setLayoutManager(LayoutManager layout) {
        if (layout instanceof GridLayoutManager){
            super.setLayoutManager(layout);

           animater = false;
        } else {
            throw new ClassCastException("You should only use a GridLayoutManager with GridRecyclerView.");
        }
    }

    @Override
    protected void attachLayoutAnimationParameters(View child, ViewGroup.LayoutParams params, int index, int count) {

        if (getAdapter() != null && getLayoutManager() instanceof GridLayoutManager){

            GridLayoutAnimationController.AnimationParameters animationParams =
                    (GridLayoutAnimationController.AnimationParameters) params.layoutAnimationParameters;

            if (animationParams == null) {
                animationParams = new GridLayoutAnimationController.AnimationParameters();
                params.layoutAnimationParameters = animationParams;
            }

            int columns = ((GridLayoutManager) getLayoutManager()).getSpanCount();

            animationParams.count = count;
            animationParams.index = index;
            animationParams.columnsCount = columns;
            animationParams.rowsCount = count / columns;

            final int invertedIndex = count - 1 - index;
            animationParams.column = columns - 1 - (invertedIndex % columns);
            animationParams.row = animationParams.rowsCount - 1 - invertedIndex / columns;

        } else {
            super.attachLayoutAnimationParameters(child, params, index, count);
        }
    }*/
private boolean mScrollable;
@Override
public boolean dispatchTouchEvent(MotionEvent ev) {
    return !mScrollable || super.dispatchTouchEvent(ev);
}

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        for (int i = 0; i < getChildCount(); i++) {
            if(animater) {
                animate(getChildAt(i), i);
            }
            if (i == getChildCount() - 1) {
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mScrollable = true;
                    }
                }, i * 100);
            }
        }
    }

    private void animate(View view, final int pos) {
        view.animate().cancel();
        view.setTranslationY(50);
        view.setAlpha(0);
        view.animate().alpha(1.0f).translationY(0).setDuration(200).setStartDelay(pos * 50);
    }
}
