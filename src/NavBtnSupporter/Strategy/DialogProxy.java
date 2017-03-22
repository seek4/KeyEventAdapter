package com.txznet.txz.util.NavBtnSupporter.Strategy;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by J on 2016/11/13.
 */

public class DialogProxy implements IContextProxy {
    private Dialog mDialog;

    public DialogProxy(Dialog dialog) {
        this.mDialog = dialog;
    }

    @Override
    public View getViewById(int id) {
        return null;
    }

    @Override
    public WindowManager getWindowManager() {
        return (WindowManager) mDialog.getContext().getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    public void performBack() {
        mDialog.onBackPressed();
    }

    @Override
    public Context getContext() {
        return mDialog.getContext();
    }
}
