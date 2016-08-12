package com.lugeek.superpopupwindow;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ljm on 2016/8/5.
 */
public class PopupAdapter extends RecyclerView.Adapter<PopupAdapter.Viewholder>{

    private static final int NOT_CLICKED = -1;
    private Context mContext;
    private List<TabModel> mData;
    private int mSelectedPos = NOT_CLICKED;
    private SuperPopupWindow popupWindow;


    public PopupAdapter(Context context, List<TabModel> data) {
        this.mContext = context;
        this.mData = data;
        this.popupWindow = new SuperPopupWindow(context);
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(Viewholder holder, final int position) {
        holder.mBtnName.setText(mData.get(position).mName);
        if (mData.get(position).mSelected) {
            holder.mBtnName.setBackgroundResource(R.drawable.item2);
        }
        if (mSelectedPos == position) {
            holder.setClicked(true);
        } else {
            holder.setClicked(false);
        }
        holder.mLlName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedPos == position) {
                    mSelectedPos = NOT_CLICKED;
                    PopupAdapter.this.notifyItemChanged(position);
                    popupWindow.show(view);
                    popupWindow.update(mData.get(position).mValues);
                } else {
                    if (mSelectedPos == NOT_CLICKED) {
                        mSelectedPos = position;
                        PopupAdapter.this.notifyItemChanged(position);
                        popupWindow.show(view);
                        popupWindow.update(mData.get(position).mValues);
                    } else {
                        int temp = mSelectedPos;
                        mSelectedPos = position;
                        PopupAdapter.this.notifyItemChanged(temp);
                        PopupAdapter.this.notifyItemChanged(position);
                        popupWindow.show(view);
                        popupWindow.update(mData.get(position).mValues);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder{
        public TextView mBtnName;
        public LinearLayout mLlName;
        public LinearLayout mLlWhite;
        public Viewholder(View itemView) {
            super(itemView);
            mBtnName = (TextView) itemView.findViewById(R.id.name);
            mLlName = (LinearLayout) itemView.findViewById(R.id.name_box);
            mLlWhite = (LinearLayout) itemView.findViewById(R.id.ll_white);
        }

        public void setClicked(boolean clicked) {
            if (clicked) {
                mLlName.setBackgroundResource(R.drawable.item1_2);
                mLlWhite.setVisibility(View.VISIBLE);
            } else {
                mLlName.setBackgroundResource(R.drawable.item1_1);
                mLlWhite.setVisibility(View.INVISIBLE);
            }
        }
    }
}
