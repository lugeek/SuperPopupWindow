package com.lugeek.superpopupwindow;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljm on 2016/8/15.
 */
public class DataRepository {

    private static DataRepository mInstance;

    private DataRepository(){}

    public static DataRepository getInstance() {
        if (mInstance == null) {
            synchronized (DataRepository.class) {
                if (mInstance == null) {
                    mInstance = new DataRepository();
                }
            }
        }
        return mInstance;
    }

    public interface OnLoadListener {
        void onSuccess(List<TabModel> result);
    }



    public List<TabModel> initData() {
        List<TabModel> list = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            TabModel model = new TabModel();
            model.mAttrName = "TAB" + i;
            model.mAttrId = i;
            model.mSelectedName = "";
            model.mValues = new ArrayList<>();
            for (int j = 0; j < 9; j++) {
                ValueModel value = new ValueModel();
                value.mVid = j;
                value.mVname = "TAB" + i + "-" + j;
                model.mValues.add(value);
            }
            list.add(model);
        }
        return list;
    }

    public void getData(final List<TabModel> data, final OnLoadListener listener) {
        if (data == null) return;
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                if (data.size() == 6) {
                    for (TabModel t : data) {
                        if (!t.mSelectedName.isEmpty()) continue;
                        ValueModel v = new ValueModel();
                        v.mVname = "new";
                        t.mValues.add(v);
                    }
                } else {
                    while (data.size() < 6) {
                        TabModel model = new TabModel();
                        model.mAttrName = "TABX";
                        model.mAttrId = 10;
                        model.mSelectedName = "";
                        model.mValues = new ArrayList<>();
                        for (int j = 0; j < 9; j++) {
                            ValueModel value = new ValueModel();
                            value.mVid = j;
                            value.mVname = "TABX" + "-" + j;
                            model.mValues.add(value);
                        }

                        data.add(model);
                    }
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return data;
            }

            @Override
            protected void onPostExecute(Object o) {
                if (listener != null) {
                    listener.onSuccess((List<TabModel>)o);
                }
            }
        };
        task.execute();
    }
}
