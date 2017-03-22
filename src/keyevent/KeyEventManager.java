package com.txznet.music.keyevent;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;

import com.txznet.music.ui.SplashActivity;
import com.txznet.txz.util.NavBtnSupporter.NavBtnSupporter;

import java.util.List;

/**
 * manage key event and dispatch focus to view
 *
 * Created by Terry on 2017/3/22.
 */

public class KeyEventManager {

    private List<View> mFocusViews;

    private  static  KeyEventManager sInstance = new KeyEventManager();

    private NavBtnSupporter mNavBtnSupporter;

    private  KeyEventManager(){
    }

    public  static  KeyEventManager getInstance(){
        return  sInstance;
    }

    public void init(Activity activity){
        mNavBtnSupporter = NavBtnSupporter.attach(activity);
    }



    public  boolean onKeyEvent(int keyCode){

        switch (keyCode){
            case  KeyEvent.KEYCODE_DPAD_UP:

                return  true;
            case  KeyEvent.KEYCODE_DPAD_DOWN:

                return  true;
            case  KeyEvent.KEYCODE_DPAD_LEFT:

                return  true;
            case  KeyEvent.KEYCODE_DPAD_RIGHT:

                return  true;
            case  KeyEvent.KEYCODE_DPAD_CENTER:

                return  true;
        }
        return  false;
    }


    public  boolean onKeyEvent(int keyCode, KeyEvent event){
        return  onKeyEvent(keyCode);
    }


    private void updateFocusViews(List<List<View>> focusViews){

    }

    /**
     * 进入到首页
     */
    public void enterHome(){

    }

    public  void enterPlay(){

    }


}
