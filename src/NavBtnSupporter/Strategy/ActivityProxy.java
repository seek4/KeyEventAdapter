package com.txznet.txz.util.NavBtnSupporter.Strategy;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by J on 2016/11/13.
 */

public class ActivityProxy implements IContextProxy {
    private Activity mActivity;

    public ActivityProxy(Activity activity) {
        mActivity = activity;
    }

    @Override
    public View getViewById(int id) {
        return null;
    }

    @Override
    public WindowManager getWindowManager() {
        return (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    public void performBack() {
        mActivity.onBackPressed();
    }

    @Override
    public Context getContext() {
        return mActivity;
    }
}
