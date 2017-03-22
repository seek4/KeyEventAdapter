package com.txznet.music.keyevent;

import android.view.KeyEvent;
import android.view.View;

/**
 * 上下左右键分发处理
 * <p>
 * Created by Terry on 2017/3/22.
 */

public class NavBtnDispatcher implements IBtnDispatcher {

    // supported nav buttons
    public static final int NAV_BTN_UP = KeyEvent.KEYCODE_DPAD_UP;
    public static final int NAV_BTN_DOWN = KeyEvent.KEYCODE_DPAD_DOWN;
    public static final int NAV_BTN_LEFT = KeyEvent.KEYCODE_DPAD_LEFT;
    public static final int NAV_BTN_RIGHT = KeyEvent.KEYCODE_DPAD_RIGHT;
    public static final int NAV_BTN_CENTER = KeyEvent.KEYCODE_DPAD_CENTER;


    private View[][] mCurFocusableViews;
    private boolean[] mIsRowLoop;
    private int mCurRow = 0;
    private int mCurColumn = 0;
    private String mName = null;

    public NavBtnDispatcher(String name) {
        this.mName = name;
    }

    public NavBtnDispatcher() {
    }

    public void reset(){
        mCurFocusableViews = null;
        mIsRowLoop = null;
        mCurRow = 0;
        mCurColumn = 0;
    }

    /**
     * KeyEvent的反馈
     */
    public class KeyEventBack {
        public static final int ACTION_OBTAIN_FOCUS = 1;
        public static final int ACTION_SEEK_MORE_UP = 2; // 已是最上仍然按上
        public static final int ACTION_SEEK_MORE_DOWN = 3;
        public static final int ACTION_SEEK_MORE_LEFT = 4;
        public static final int ACTION_SEEK_MORE_RIGHT = 5;

        int action;
        View targetView;
        int row;
        int column;
    }


    /**
     * @return KeyEventBack
     */
    @Override
    public KeyEventBack onKeyEvent(int keycode) {
        if (mCurFocusableViews == null || mCurFocusableViews.length == 0) {
            return null;
        }
        return null;
    }


    /**
     * 更新当前可获取焦点的View列表
     *
     * @param views
     * @param isRowLoop 当前行焦点是否需要循环
     * @param reset     是否重置当前焦点位置
     */
    public void updateFocusableViews(View[][] views, boolean[] isRowLoop, int defaultRow, int defaultColumn, boolean reset) {
        this.mCurFocusableViews = views;
        this.mIsRowLoop = isRowLoop;
    }

    public void updateFocusableViews(View[][] views) {
        updateFocusableViews(views, null, 0, 0, false);
    }


    @Override
    public String toString() {
        return "NavBtnDispatcher:" + mName;
    }
}
