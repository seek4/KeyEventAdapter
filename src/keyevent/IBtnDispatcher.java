package com.txznet.music.keyevent;

/**
 * 消费并分发keycode
 * Created by Terry on 2017/3/22.
 */

public interface IBtnDispatcher {


    /**
     *
     * @param keycode
     * @return
     */
    public Object onKeyEvent(int keycode);
}
