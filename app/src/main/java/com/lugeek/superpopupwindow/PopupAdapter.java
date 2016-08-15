package com.lugeek.superpopupwindow;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljm on 2016/8/5.
 */
public class PopupAdapter extends RecyclerView.Adapter<PopupAdapter.Viewholder> implements DataRepository.OnLoadListener{

    private static final int NOT_CLICKED = -1;
    private Context mContext;
    private List<TabModel> mData;
    private int mSelectedPos = NOT_CLICKED;
    private SuperPopupWindow mPopupWindow;
    private View mAnchorView;


    public PopupAdapter(Context context, List<TabModel> data, View anchorView) {
        this.mContext = context;
        this.mData = data;
        this.mAnchorView = anchorView;
        this.mPopupWindow = new SuperPopupWindow(context);
        this.mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int x = (int) event.getX();
                final int y = (int) event.getY();
                //拦截所有点击外部的事件,设置关闭.
                if ((x < 0) || (x >= v.getWidth()) || (y < 0) || (y >= v.getHeight())) {
                    if (y < 0 - mAnchorView.getHeight() || y >= v.getHeight()) {
                        invokePopup(NOT_CLICKED);
                    }
                    return true;
                }
                return false;
            }
        });
        this.mPopupWindow.setClickListener(new SuperPopupWindow.ValueClickListener() {
            @Override
            public void onClick(String value) {
                int from = mSelectedPos;
                invokePopup(NOT_CLICKED);
                ((RecyclerView)mAnchorView).smoothScrollToPosition(0);
                TabModel selectedModel = mData.get(from);
                selectedModel.mSelectedName = value;
                mData.remove(from);
                int targetPos = addData(selectedModel);
                notifyItemMoved(from, targetPos);
//                PopupAdapter.this.notifyItemRemoved(from);
//                PopupAdapter.this.notifyItemInserted(addData(tempModel));
                DataRepository.getInstance().getData(mData, PopupAdapter.this);
            }
        });
    }

    @Override
    public void onSuccess(List<TabModel> result) {
        notifyDataSetChanged();
    }

    public int addData(TabModel model) {
        if (mData == null) {
            mData = new ArrayList<>();
            mData.add(model);
            return 0;
        }
        if (mData.isEmpty()) {
            mData.add(model);
            return 0;
        }
        int pos = mData.size();
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).mSelectedName.isEmpty()) {
                pos = i;
                break;
            }
        }
        mData.add(pos, model);
        return pos;
    }

    public void invokePopup(int targetPosition) {
        if (mSelectedPos == NOT_CLICKED) {      //PopupWindow未打开
            if (targetPosition == NOT_CLICKED) return;
            mSelectedPos = targetPosition;
            PopupAdapter.this.notifyItemChanged(targetPosition);
            mPopupWindow.show(mAnchorView, mData.get(targetPosition).mValues);
        } else {                                //PopupWindow已打开
            if (targetPosition == NOT_CLICKED) {              //点击外部空白,关闭
                int temp = mSelectedPos;
                mSelectedPos = NOT_CLICKED;
                PopupAdapter.this.notifyItemChanged(temp);
                mPopupWindow.close();
            } else {
                if (mSelectedPos == targetPosition) {         //点击已打开的位置,则关闭
                    mSelectedPos = NOT_CLICKED;
                    PopupAdapter.this.notifyItemChanged(targetPosition);
                    mPopupWindow.close();
                } else {                                //点击未打开的位置,则切换
                    int temp = mSelectedPos;
                    mSelectedPos = targetPosition;
                    PopupAdapter.this.notifyItemChanged(temp);
                    PopupAdapter.this.notifyItemChanged(targetPosition);
                    mPopupWindow.show(mAnchorView, mData.get(targetPosition).mValues);
                }
            }

        }
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(final Viewholder holder, int position) {
        holder.mBtnName.setText(mData.get(position).mSelectedName.isEmpty() ? mData.get(position).mAttrName : mData.get(position).mSelectedName);
        holder.mBtnName.setTextColor(mData.get(position).mSelectedName.isEmpty() ? Color.BLACK : Color.RED);
        if (mSelectedPos == position) {
            holder.mLlName.setBackgroundResource(R.drawable.item1_2);
            holder.mLlWhite.setVisibility(View.VISIBLE);
        } else {
            holder.mLlName.setBackgroundResource(R.drawable.item1_1);
            holder.mLlWhite.setVisibility(View.INVISIBLE);
        }
        holder.mLlName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                if (!mData.get(pos).mSelectedName.isEmpty()) {
                    invokePopup(NOT_CLICKED);
                    mData.remove(pos);
                    notifyItemRemoved(pos);
                    DataRepository.getInstance().getData(mData, PopupAdapter.this);
                } else {
                    invokePopup(pos);
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
