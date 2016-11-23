package com.lugeek.superpopupwindow;

/**
 * Created by ljm on 2016/8/15.
 */
public class ValueModel implements SuperPopupWindow.SingleChoiceItem{
    public int mVid;
    public String mVname;
    public boolean mIsSelected;

    @Override
    public String getName() {
        return mVname;
    }

    @Override
    public boolean getIsSelected() {
        return mIsSelected;
    }
}
