package com.txznet.txz.util.NavBtnSupporter.Strategy;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by J on 2016/11/13.
 */

public interface IContextProxy {
    Context getContext();
    View getViewById(int id);
    WindowManager getWindowManager();
    void performBack();
}
