package com.txznet.music.keyevent;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.txznet.comm.remote.util.LogUtil;

/**
 * 绘制focusView
 * <p>
 * Created by Terry on 2017/3/22.
 */

public class FocusDrawer {

    private FocusView mFocusView;
    private View mViewCurrent;

//    private FocusDrawer(Activity activity, boolean enableSystemWindowParam) {
//        this.mContextProxy = new ActivityProxy(activity);
//        bSystemWindow = enableSystemWindowParam;
//        initFocusView();
//    }
//
//    private FocusDrawer(Dialog dialog, boolean enableSystemWindowParam) {
//        this.mContextProxy = new DialogProxy(dialog);
//        bSystemWindow = enableSystemWindowParam;
//        initFocusView();
//    }
//
//    private FocusDrawer(IContextProxy proxy, boolean enableSystemWindowParam) {
//        this.mContextProxy = proxy;
//        bSystemWindow = enableSystemWindowParam;
//        initFocusView();
//    }
//
//    public static FocusDrawer attach(Activity activity) {
//        return attach(activity, false);
//    }
//
//    public static FocusDrawer attach(Activity activity, boolean enableSystemWindowParam) {
//        return new FocusDrawer(activity, enableSystemWindowParam);
//    }
//
//    public static FocusDrawer attach(Dialog dialog) {
//        return attach(dialog, false);
//    }
//
//    public static FocusDrawer attach(Dialog dialog, boolean enableSystemWindowParam) {
//        return new FocusDrawer(dialog, enableSystemWindowParam);
//    }
//
//    public static FocusDrawer attach(IContextProxy proxy) {
//        return attach(proxy, false);
//    }
//
//    public static FocusDrawer attach(IContextProxy proxy, boolean enableSystemWindowParam) {
//        return new FocusDrawer(proxy, enableSystemWindowParam);
//    }

    public static FocusDrawer attach(Activity activity) {
        return new FocusDrawer(activity);
    }


    private FocusDrawer(Activity activity) {
        this.activity = activity;
        initFocusView();
    }

    private WindowManager mWinManager;
    private Activity activity;
    private int[] mArrLocationCache = new int[2];

    public void detach() {
        try {
            mWinManager.removeViewImmediate(mFocusView);
            activity = null;
            mViewCurrent = null;
        } catch (Exception e) {
            log("detach encountered error: " + e.toString());
        }
    }


    public void updateFocusView(View focusView) {
        mViewCurrent = focusView;
        updateFocusRect();
    }


    private void updateFocusRect() {
        if (null == mViewCurrent) {
            hideFocusView();
            return;
        }


        showFocusView();

        mViewCurrent.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mViewCurrent.getLocationOnScreen(mArrLocationCache);
                    int width = mViewCurrent.getWidth();
                    int height = mViewCurrent.getHeight();

                    log("update focus to view: " + mViewCurrent.toString() + " @ " + mArrLocationCache[0] + "," + mArrLocationCache[1] + "," + width + "," + height);
                    if (0 == width && 0 == height) {
                        hideFocusView();
                        return;
                    }

                    mFocusView.setFocusPosition(mArrLocationCache[0], mArrLocationCache[1], width, height);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void hideFocusView() {
        mFocusView.setVisibility(View.GONE);
    }

    private void showFocusView() {
        mFocusView.setVisibility(View.VISIBLE);
    }

    private void initFocusView() {
        mFocusView = new FocusView(activity);
        mWinManager = activity.getWindowManager();

        // add focus view to window
        WindowManager.LayoutParams layoutParam = new WindowManager.LayoutParams();
        layoutParam.width = LinearLayout.LayoutParams.MATCH_PARENT;
        layoutParam.height = LinearLayout.LayoutParams.MATCH_PARENT;
        layoutParam.format = PixelFormat.RGBA_8888;
        layoutParam.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
//        if (bSystemWindow) {
        layoutParam.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
//        }
        mWinManager.addView(mFocusView, layoutParam);
    }

    private boolean bLogEnabled = true;
    private String LOG_TAG = FocusDrawer.class.getSimpleName();

    private void log(String msg) {
        if (bLogEnabled) {
//            Log.i(LOG_TAG, msg);
            LogUtil.logd(LOG_TAG + ": " + msg);
        }
    }
}
