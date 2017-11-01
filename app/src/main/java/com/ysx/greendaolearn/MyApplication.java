package com.ysx.greendaolearn;

import android.app.Application;

import com.ysx.greendaolearn.manager.GreenDaoManager;

/**
 * @author ysx
 * @date 2017/11/1
 * @description
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        GreenDaoManager.getInstance().init(getApplicationContext());
    }
}
