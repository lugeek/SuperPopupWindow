package com.lugeek.superpopupwindow;

import android.content.Context;

/**
 * Created by ljm on 2016/8/19.
 */

public class Utils {
    //dp转px
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }    //dp转px
}
