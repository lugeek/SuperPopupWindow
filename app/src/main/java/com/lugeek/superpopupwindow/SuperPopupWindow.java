package com.lugeek.superpopupwindow;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
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

    private Context mContext;
    private RecyclerView mRvContent;
    private List<ValueModel> mData = new ArrayList<>();
    private ContentAdapter mAdapter;
    private View backView;
    private ValueClickListener mClickListener;

    public SuperPopupWindow(Context context) {
        this.mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.popup_window_view, null, false);
        mRvContent = (RecyclerView) view.findViewById(R.id.recyclerview);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
        mRvContent.setLayoutManager(layoutManager);
        mAdapter = new ContentAdapter(mData);
        mRvContent.setAdapter(mAdapter);
        setContentView(view);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setFocusable(false);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setAnimationStyle(R.style.SearchWindowAnimation);
        backView = new View(mContext);
        backView.setBackgroundColor(Color.parseColor("#669f9f9f"));
    }

    public interface ValueClickListener {
        void onClick(String value);
    }

    public void setClickListener(ValueClickListener listener) {
        this.mClickListener = listener;
    }

    public void show(View view, List<ValueModel> data) {
        if (data != null) {
            update(data);
        }
        if (!isShowing()) {
            showAsDropDown(view);
        }
    }



    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        addDimBackground(anchor);
        super.showAsDropDown(anchor, xoff, yoff, gravity);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        removeDimBackground();
    }

    public void close() {
        if (this.isShowing()) {
            dismiss();
        }
    }

    public void update(List<ValueModel> data) {
        mAdapter.update(data);
    }

    public void addDimBackground(View anchor) {
        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        p.gravity = Gravity.START | Gravity.TOP;
        p.token = anchor.getWindowToken();
        p.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
        p.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        p.format = PixelFormat.TRANSPARENT;
        int[] xy = new int[2];
        anchor.getLocationInWindow(xy);
        Rect rect = new Rect();
        anchor.getWindowVisibleDisplayFrame(rect);
        p.height = rect.bottom - xy[1] - anchor.getHeight();
        p.width = WindowManager.LayoutParams.MATCH_PARENT;
        p.x = xy[0];
        p.y = xy[1] + anchor.getHeight();
        p.packageName = mContext.getPackageName();
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        wm.addView(backView, p);
    }

    public void removeDimBackground() {
        if (backView != null) {
            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            wm.removeView(backView);
        }
    }


    class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.MyViewHolder> {

        private List<ValueModel> mData;

        public ContentAdapter(List<ValueModel> data) {
            this.mData = data;
        }

        public void update(List<ValueModel> data) {
            mData.clear();
            mData.addAll(data);
            this.notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.popup_window_view_itemview, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            holder.mTvName.setText(mData.get(position).mVname);
            holder.mTvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onClick(mData.get(position).mVname);
                }
            });
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
