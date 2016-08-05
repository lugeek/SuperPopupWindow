package com.lugeek.superpopupwindow;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button button1;
    Button button2;
    RecyclerView recyclerView;
    List<TabModel> mData = new ArrayList<>();
    PopupAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(this);
        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        initData();
        adapter = new PopupAdapter(this, mData);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void initData() {
        for (int i = 0; i < 6; i++) {
            TabModel model = new TabModel();
            model.mName = "TAB" + i;
            model.mSelected = false;
            model.mValues = new ArrayList<>();
            for (int j = 0; j < 9; j++) {
                model.mValues.add("Value" + j);
            }
            mData.add(model);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button1) {
            showPopupWindow(MainActivity.this, view);
        } else if (view.getId() == R.id.button2) {
            showPopupWindow(MainActivity.this, view);
        }
    }

    public void showPopupWindow(Context context, View view) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_main, null, false);
        PopupWindow popupWindow = new PopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#77ff4965")));
        popupWindow.showAsDropDown(view);
    }
}
