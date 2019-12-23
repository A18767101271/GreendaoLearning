package greendao.wislie.com.greendaolearning;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import greendao.wislie.com.greendaolearning.gen.DaoMaster;
import greendao.wislie.com.greendaolearning.gen.DaoSession;

/**
 * author : wislie
 * e-mail : 254457234@qq.com
 * date   : 2019/12/2310:03 AM
 * desc   : 全局Application
 * version: 1.0
 */
public class GlobalApp extends Application {

    private static GlobalApp mApplication;
    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        ////////////////////////////////////// GreenDao //////////////////////////////////////
        setGreenDaoDatabase();
    }

    //设置GreenDao数据库表
    private void setGreenDaoDatabase() {
        DaoMaster.OpenHelper mHelper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        DaoMaster mDaoMaster = new DaoMaster(db);
        daoSession = mDaoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public static GlobalApp getApp() {
        return mApplication;
    }
}
