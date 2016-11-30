package com.lugeek.superpopupwindow;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljm on 2016/8/4.
 */
public class SuperPopupWindow extends PopupWindow{

    private static final int ANIMATE_DURATION = 200;
    private Context mContext;
    private View mAnchorView;
    //RecyclerView
    private RecyclerView mRvContent;
    private ContentAdapter mAdapter;
    //蒙层
    private FrameLayout mBackContainer;
    private View mBackView;
    //RecyclerView中Item点击事件
    private ItemClickListener mItemClickListener;
    //点击外部关闭回调
    private OutsideClickListener mOutsideClickListener;
    //是否是切换PopupWindow内部的数据，如果是切换，则不关闭蒙层
    private boolean isSwitch = false;

    //防止拖动
    private int oldX = -1;
    private int oldY = -1;

    public SuperPopupWindow(Context context, View anchorView) {
        this.mContext = context;
        this.mAnchorView = anchorView;
        initView();
        initParams();
    }

    public void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.popup_window_view, null, false);
        mRvContent = (RecyclerView) view.findViewById(R.id.recyclerview);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 2);
        mRvContent.setLayoutManager(layoutManager);
        mAdapter = new ContentAdapter();
        mRvContent.setAdapter(mAdapter);
        setContentView(view);

        mBackContainer = new FrameLayout(mContext);
        mBackView = new View(mContext);
        mBackView.setBackgroundColor(Color.parseColor("#669f9f9f"));
    }

    public void initParams() {
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setFocusable(false);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//OutsideTouchable为true时需添加Background保证兼容性
        setAnimationStyle(R.style.SearchWindowAnimation);
    }

    public void setItemClickListener(ItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void setOutsideClickListener(final OutsideClickListener listener) {
        this.mOutsideClickListener = listener;
        this.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int x = (int) event.getX();
                final int y = (int) event.getY();
                //拦截所有点击外部的事件,设置关闭.
                if (event.getAction() == MotionEvent.ACTION_DOWN && ((x < 0) || (x >= v.getWidth()) || (y < 0) || (y >= v.getHeight()))) {
                    if (y < 0 - mAnchorView.getHeight() || y >= v.getHeight()) {
                        listener.onClicked();
                    }
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    if (y < 0 - mAnchorView.getHeight() || y >= v.getHeight()) {
                        listener.onClicked();
                    }
                    return true;
                }
                return false;
            }
        });
        mAnchorView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    listener.onClicked();
                }
                return false;
            }
        });
    }

    public void show(List<? extends SingleChoiceItem> data) {
        if (isShowing()) {
            switchShow(data);
            return;
        }
        mAdapter.update(data);
        showAsDropDown(mAnchorView);
    }

    public void switchShow(final List<? extends SingleChoiceItem> data) {
        isSwitch = true;
        dismiss();
        mAdapter.update(data);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showAsDropDown(mAnchorView);
            }
        }, 100);
    }

    public void showAsDropDown(View anchor) {
        if (!isSwitch) {
            addDimBackground(anchor);
        }
        isSwitch = false;
        super.showAsDropDown(anchor);
    }

    @Override
    public void dismiss() {
        if (!isSwitch) {
            removeDimBackground();
        }
        oldX = -1;
        oldY = -1;
        super.dismiss();
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
        if (mBackContainer.getWindowToken() == null) {
            wm.addView(mBackContainer, p);
            mBackContainer.addView(mBackView);
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in);
            animation.setDuration(ANIMATE_DURATION);
            mBackView.startAnimation(animation);
        }
    }

    public void removeDimBackground() {
        if (mBackContainer != null) {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.fade_out);
            animation.setDuration(ANIMATE_DURATION);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mBackContainer.removeAllViews();
                    WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                    wm.removeView(mBackContainer);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mBackView.startAnimation(animation);
        }
    }

    @Override
    public void update(int x, int y, int width, int height, boolean force) {
        if (oldX == -1 && oldY == -1) {
            oldX = x;
            oldY = y;
        } else if (oldX != x || oldY != y && isShowing()){
            mOutsideClickListener.onClicked();
        }
        super.update(x, y, width, height, force);
    }


    class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.MyViewHolder> {

        private List<SingleChoiceItem> mData;

        public ContentAdapter() {
            this.mData = new ArrayList<>();
        }

        public void update(List<? extends SingleChoiceItem> data) {
            if (data == null || data.isEmpty()) {
                return;
            }
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
            holder.mTvName.setText(mData.get(position).getName());
            holder.mTvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onClick(mData.get(position).getName());
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

    public interface ItemClickListener {
        void onClick(String value);
    }

    public interface OutsideClickListener {
        void onClicked();
    }

    public interface SingleChoiceItem {
        String getName();
        boolean getIsSelected();
    }
}
