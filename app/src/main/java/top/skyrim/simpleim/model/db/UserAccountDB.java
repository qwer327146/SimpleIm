package top.skyrim.simpleim.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import top.skyrim.simpleim.model.dao.UserAccountTable;

/**
 * Created by wangxin on 2018/3/3.
 * 用户账号信息数据库
 */

public class UserAccountDB extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;

    public UserAccountDB(Context context) {
        super(context, "account.db", null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserAccountTable.CREATE_TAB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
