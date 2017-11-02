package com.ysx.greendaolearn.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.github.yuweiguocn.library.greendao.MigrationHelper;
import com.ysx.greendaolearn.entity.DaoMaster;
import com.ysx.greendaolearn.entity.DaoSession;
import com.ysx.greendaolearn.entity.PlayerDao;

import org.greenrobot.greendao.database.Database;

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
            /**
             * 使用自定义的OpenHelper对象
             */
            MigrationHelper.DEBUG = true;
            MyOpenHelper openHelper = new MyOpenHelper(
                    context.getApplicationContext(), DATABASE_NAME, null);
            DaoMaster daoMaster = new DaoMaster(openHelper.getWritableDatabase());
            daoSession = daoMaster.newSession();
            isInited = true;
        }
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    /**
     * 定义一个MySQLiteOpenHelper类，用来处理数据库升级
     */
    static class MyOpenHelper extends DaoMaster.OpenHelper {

        public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {
            Log.d(TAG, "onUpgrade: old: " + oldVersion + ", new: " + newVersion);
            if (oldVersion <= 1) {
                MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
                    @Override
                    public void onCreateAllTables(Database db, boolean ifNotExists) {
                        DaoMaster.createAllTables(db, ifNotExists);
                    }

                    @Override
                    public void onDropAllTables(Database db, boolean ifExists) {
                        DaoMaster.dropAllTables(db, ifExists);
                    }
                }, PlayerDao.class);

            }

        }
    }
}
