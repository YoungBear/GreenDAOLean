package com.ysx.greendaolearn.manager;

import android.content.Context;

import com.ysx.greendaolearn.entity.DaoMaster;
import com.ysx.greendaolearn.entity.DaoSession;

/**
 * GreenDaoManager单例类，用来集中操作数据库
 *
 * @author ysx
 * @date 2017/11/1
 * @description
 */

public class GreenDaoManager {
    private static final String TAG = "GreenDaoManager";

    private static final String DATABASE_NAME = "players.db";
    /**
     * 全局保持一个DaoSession
     */
    private DaoSession daoSession;

    private boolean isInited;

    private static final class GreenDaoManagerHolder {
        private static final GreenDaoManager sInstance = new GreenDaoManager();
    }

    public static GreenDaoManager getInstance() {
        return GreenDaoManagerHolder.sInstance;
    }

    private GreenDaoManager() {

    }

    /**
     * 初始化DaoSession
     *
     * @param context
     */
    public void init(Context context) {
        if (!isInited) {
            DaoMaster.OpenHelper openHelper = new DaoMaster.DevOpenHelper(
                    context.getApplicationContext(), DATABASE_NAME, null);
            DaoMaster daoMaster = new DaoMaster(openHelper.getWritableDatabase());
            daoSession = daoMaster.newSession();
            isInited = true;
        }
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
