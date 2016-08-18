package com.lugeek.superpopupwindow;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by lujiaming on 16/8/17.
 */
public class HeightCustomRecyclerView extends RecyclerView{
    public HeightCustomRecyclerView(Context context) {
        super(context);
    }

    public HeightCustomRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HeightCustomRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        heightSpec = MeasureSpec.makeMeasureSpec(720, MeasureSpec.AT_MOST);//TODO dp to px
        super.onMeasure(widthSpec, heightSpec);
    }
}
