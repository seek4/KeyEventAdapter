package com.txznet.txz.util.NavBtnSupporter.interfaces;

import android.view.View;

/**
 * 需要自定焦点展示的View可以继承此接口
 * Created by J on 2016/10/26.
 */

public interface INavView {
	
    /**
     * 是否显示默认的焦点选择框（仅针对该View生效）
     * @return
     */
    boolean showDefaultSelectIndicator();

    /**
     * 获得焦点
     * @param rawFocus 上一个焦点
     * @param operation 最后一个方控操作
     */
    void onNavGainFocus(View rawFocus, int operation);
    /**
     * 失去焦点
     * @param newFocus 新焦点
     * @param operation 最后一个方控操作
     */
    void onNavLoseFocus(View newFocus, int operation);
}