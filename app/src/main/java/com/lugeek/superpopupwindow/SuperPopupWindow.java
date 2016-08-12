package com.lugeek.superpopupwindow;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljm on 2016/8/4.
 */
public class SuperPopupWindow extends PopupWindow{

    private RecyclerView mRvContent;
    private List<String> mData = new ArrayList<>();
    private ContentAdapter mAdapter;

    public SuperPopupWindow(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.popup_window_view, null, false);
        mRvContent = (RecyclerView) view.findViewById(R.id.recyclerview);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
        mRvContent.setLayoutManager(layoutManager);
        mAdapter = new ContentAdapter(mData);
        mRvContent.setAdapter(mAdapter);
        setContentView(view);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        setFocusable(false);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setAnimationStyle(R.style.SearchWindowAnimation);
    }

    public void show(View view) {
        if (!isShowing()) {
            showAsDropDown(view);
        }
    }

    public void close() {
        dismiss();
    }

    public void update(List<String> data) {
        mAdapter.update(data);
    }

    class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.MyViewHolder> {

        private List<String> mData;

        public ContentAdapter(List<String> data) {
            this.mData = data;
        }

        public void update(List<String> data) {
            mData.clear();
            mData.addAll(data);
            this.notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.popup_window_view_itemview, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.mTvName.setText(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView mTvName;
            public MyViewHolder(View itemView) {
                super(itemView);
                mTvName = (TextView)itemView;
            }
        }
    }
}
