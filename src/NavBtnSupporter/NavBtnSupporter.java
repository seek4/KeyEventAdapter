package com.txznet.txz.util.NavBtnSupporter;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.txznet.comm.remote.util.LogUtil;
import com.txznet.loader.AppLogicBase;
import com.txznet.txz.util.NavBtnSupporter.Strategy.ActivityProxy;
import com.txznet.txz.util.NavBtnSupporter.Strategy.DialogProxy;
import com.txznet.txz.util.NavBtnSupporter.Strategy.IContextProxy;
import com.txznet.txz.util.NavBtnSupporter.interfaces.INavView;
import com.txznet.txz.util.NavBtnSupporter.interfaces.INavOperationPresenter;
import com.txznet.txz.util.NavBtnSupporter.widgets.FocusView;
import com.txznet.txz.util.ThreadManager;

import java.util.List;

/**
 * Created by J on 2016/9/29.
 */

public class NavBtnSupporter {
    // supported nav buttons
    public static final int NAV_BTN_NEXT = 1001;
    public static final int NAV_BTN_PREV = 1002;
    public static final int NAV_BTN_CLICK = 1101;
    public static final int NAV_BTN_BACK = 1102;
    public static final int NAV_BTN_NONE = -1000;

    private boolean bLogEnabled = false;
    private static final String LOG_TAG = "NavBtnSupporter";

    private IContextProxy mContextProxy;
    private WindowManager mWinManager;
    private FocusView mFocusView;
    private View[] mViewList;

    // inner vars
    private int mViewCount;
    private int mCurrentIndex = -1;
    private View mViewCurrent; // current view on focus
    private int[] mArrLocationCache = new int[2];
    private boolean bSystemWindow;

    private NavBtnSupporter(Activity activity, boolean enableSystemWindowParam) {
        this.mContextProxy = new ActivityProxy(activity);
        bSystemWindow = enableSystemWindowParam;
        initFocusView();
    }

    private NavBtnSupporter(Dialog dialog, boolean enableSystemWindowParam) {
        this.mContextProxy = new DialogProxy(dialog);
        bSystemWindow = enableSystemWindowParam;
        initFocusView();
    }

    private NavBtnSupporter(IContextProxy proxy, boolean enableSystemWindowParam) {
        this.mContextProxy = proxy;
        bSystemWindow = enableSystemWindowParam;
        initFocusView();
    }

    public static NavBtnSupporter attach(Activity activity) {
        return attach(activity, false);
    }

    public static NavBtnSupporter attach(Activity activity, boolean enableSystemWindowParam) {
        return new NavBtnSupporter(activity, enableSystemWindowParam);
    }

    public static NavBtnSupporter attach(Dialog dialog) {
        return attach(dialog, false);
    }

    public static NavBtnSupporter attach(Dialog dialog, boolean enableSystemWindowParam) {
        return new NavBtnSupporter(dialog, enableSystemWindowParam);
    }

    public static NavBtnSupporter attach(IContextProxy proxy) {
        return attach(proxy, false);
    }

    public static NavBtnSupporter attach(IContextProxy proxy, boolean enableSystemWindowParam) {
        return new NavBtnSupporter(proxy, enableSystemWindowParam);
    }

    public void detach() {
        try {
            mWinManager.removeViewImmediate(mFocusView);
            mContextProxy = null;
            mViewCurrent = null;
            mViewList = null;
        } catch (Exception e) {
            log("detach encountered error: " + e.toString());
        }
    }

    /**
     * 设置焦点ViewList
     *
     * @param list
     * @return
     */
    public NavBtnSupporter setViewList(List<View> list) {
        return setViewList(list, true);
    }

    /**
     * 设置焦点ViewList
     *
     * @param list
     * @return
     */
    public NavBtnSupporter setViewList(View... list) {
        return setViewList(list, true);
    }

    /**
     * 设置焦点ViewList
     *
     * @param list
     * @param keepCurrentFocus 是否保持当前焦点(仅当新旧ViewList中都包含当前焦点View有效)
     * @return
     */
    public NavBtnSupporter setViewList(List<View> list, boolean keepCurrentFocus) {
        if (null == list) {
            return this;
        }

        View[] tmpArr = new View[list.size()];
        list.toArray(tmpArr);
        return setViewList(tmpArr, keepCurrentFocus);
    }

    /**
     * 设置焦点ViewList
     *
     * @param list
     * @param keepCurrentFocus 是否保持当前焦点(仅当新旧ViewList中都包含当前焦点View有效)
     * @return
     */
    public NavBtnSupporter setViewList(View[] list, boolean keepCurrentFocus) {
        if (null == list || 0 == list.length) {
            clearViewList();
            return this;
        }

        this.mViewList = list;
        mViewCount = list.length;

        // try to maintain focus on current view if current view is contained in the new list
        if (keepCurrentFocus) {
            int pos = getViewIndex(mViewCurrent, list);
            if (pos != -1) {
                setCurrentFocusIndex(pos);
                return this;
            }
        }

        clearCurrentFocus();
        return this;
    }

    /**
     * 清除ViewList
     */
    public void clearViewList() {
        mViewList = null;
        mViewCount = 0;
        mViewCurrent = null;

        updateFocusRect();
    }

    private int getViewIndex(final View view, View[] list) {
        if (null == list) {
            return -1;
        }

        for (int i = 0, len = list.length; i < len; i++) {
            if (view == list[i]) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 设置焦点到指定View
     *
     * @param v
     * @return
     */
    public boolean setCurrentFocusView(View v) {
        int pos = getViewIndex(v, mViewList);
        return setCurrentFocusIndex(pos);
    }

    /**
     * 获取当前焦点的view的索引
     * @return 当前焦点的view的索引
     */
    public int getCurrentFocusIndex() {
        return mCurrentIndex;
    }

    /**
     * 获取当前焦点View
     * @return View
     */
    public View getCurrentFocusView() {
        return mViewCurrent;
    }

    /**
     * 设置焦点到指定index
     *
     * @param index
     * @return
     */
    public boolean setCurrentFocusIndex(int index) {
        if (null == mViewList || 0 == mViewCount) {
            return false;
        }

        if (index >= 0 && index <= mViewCount - 1) {
            changeNavFocus(mCurrentIndex, index, NAV_BTN_NONE);

            updateFocusRect();
            return true;
        }

        return false;
    }

    /**
     * 清除当前焦点
     */
    public void clearCurrentFocus() {
        mViewCurrent = null;
        mCurrentIndex = -1;
        updateFocusRect();
    }

    /**
     * 设置焦点指示框的资源
     *
     * @param resId 资源id
     * @return
     */
    public NavBtnSupporter setFocusResource(int resId) {
        mFocusView.setFocusResource(resId);

        return this;
    }

    /**
     * 设置焦点指示框的资源
     *
     * @param drawable
     * @return
     */
    public NavBtnSupporter setFocusDrawable(Drawable drawable) {
        mFocusView.setFocusDrawable(drawable);

        return this;
    }

    private void initFocusView() {
        mFocusView = new FocusView(mContextProxy.getContext());
        mWinManager = mContextProxy.getWindowManager();

        // add focus view to window
        WindowManager.LayoutParams layoutParam = new WindowManager.LayoutParams();
        layoutParam.width = LinearLayout.LayoutParams.MATCH_PARENT;
        layoutParam.height = LinearLayout.LayoutParams.MATCH_PARENT;
        layoutParam.format = PixelFormat.RGBA_8888;
        layoutParam.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        if (bSystemWindow) {
            layoutParam.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        mWinManager.addView(mFocusView, layoutParam);
    }

    public void performNext() {
        log("performNext");
        if (0 == mViewCount) {
            return;
        }

        // dispatch nav event to focused view first
        if (!dispatchNavOperation(mViewCurrent, NAV_BTN_NEXT)) {
            int newIndex = (mCurrentIndex + 1) % mViewCount;
            changeNavFocus(mCurrentIndex, newIndex, NAV_BTN_NEXT);
        }

        updateFocusRect();
    }

    public void performPrev() {
        log("performPrev");
        if (0 == mViewCount) {
            return;
        }

        if (!dispatchNavOperation(mViewCurrent, NAV_BTN_PREV)) {
            int newIndex = mCurrentIndex == -1 ? mViewCount - 1 : (mCurrentIndex - 1 + mViewCount) % mViewCount;
            changeNavFocus(mCurrentIndex, newIndex, NAV_BTN_PREV);
        }

        updateFocusRect();
    }

    public void performClick() {
        log("performClick");
        if (0 == mViewCount || null == mViewCurrent) {
            return;
        }

        if (dispatchNavOperation(mViewCurrent, NAV_BTN_CLICK)) {
            updateFocusRect();
            return;
        }

        updateFocusRect();
        mViewCurrent.performClick();
    }

    public void performBack() {
        if (dispatchNavOperation(mViewCurrent, NAV_BTN_BACK)) {
            updateFocusRect();
            return;
        }

        updateFocusRect();
        mContextProxy.performBack();
    }

    private void updateFocusRect() {
        if (null == mViewCurrent) {
            hideFocusView();
            return;
        }

        if (mViewCurrent instanceof INavView) {
            INavView v = (INavView) mViewCurrent;
            if (v.showDefaultSelectIndicator()) {
                showFocusView();
            } else {
                hideFocusView();
                return;
            }
        } else {
            showFocusView();
        }

        mViewCurrent.post(new Runnable() {
            @Override
            public void run() {
                try{
                    mViewCurrent.getLocationOnScreen(mArrLocationCache);
                    int width = mViewCurrent.getWidth();
                    int height = mViewCurrent.getHeight();

                    log("update focus to view: " + mViewCurrent.toString() + " @ " + mArrLocationCache[0] + "," + mArrLocationCache[1] + "," + width + "," + height);
                    if (0 == width && 0 == height) {
                        hideFocusView();
                        return;
                    }

                    mFocusView.setFocusPosition(mArrLocationCache[0], mArrLocationCache[1], width, height);
                }catch(Exception e) {

                }
            }
        });

    }

    private boolean dispatchNavOperation(View v, int operation) {
        if (null != v && v instanceof INavOperationPresenter) {
            return ((INavOperationPresenter) v).onNavOperation(operation);
        }

        return false;
    }

    private void notifyLoseNavFocus(View target, View current, int operation) {
        if (target instanceof INavView) {
            ((INavView) target).onNavLoseFocus(current, operation);
        }
    }

    private void notifyGainNavFocus(View target, View raw, int operation) {
        if (target instanceof INavView) {
            ((INavView) target).onNavGainFocus(raw, operation);
        }
    }

    private void changeNavFocus(int rawIndex, int currentIndex, int operation) {
        if (rawIndex == currentIndex) {
            return;
        }

        View rawFocus = (rawIndex >= 0 && rawIndex < mViewList.length) ? mViewList[rawIndex] : null;
        mViewCurrent = mViewList[currentIndex];
        mCurrentIndex = currentIndex;

        notifyLoseNavFocus(rawFocus, mViewCurrent, operation);
        notifyGainNavFocus(mViewCurrent, rawFocus, operation);
    }

    private void hideFocusView() {
        mFocusView.setVisibility(View.GONE);
    }

    private void showFocusView() {
        mFocusView.setVisibility(View.VISIBLE);
    }

    public NavBtnSupporter setLogEnabled(boolean enable) {
        bLogEnabled = enable;
        return this;
    }

    private void log(String msg) {
        if (bLogEnabled) {
//            Log.i(LOG_TAG, msg);
            LogUtil.logd(LOG_TAG + ": " + msg);
        }
    }


}
