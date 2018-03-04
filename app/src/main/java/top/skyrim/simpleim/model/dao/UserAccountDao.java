package top.skyrim.simpleim.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import top.skyrim.simpleim.model.bean.UserInfo;
import top.skyrim.simpleim.model.dao.UserAccountTable;
import top.skyrim.simpleim.model.db.UserAccountDB;

/**
 * Created by wangxin on 2018/3/3.
 * 用户信息数据库的操作类
 */

public class UserAccountDao {

    private UserAccountDB mHelper;

    public UserAccountDao(Context context) {
        mHelper = new UserAccountDB(context);
    }

    // 添加用户到数据库
    public void addAccount(UserInfo user) {
        //获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //添加数据
        ContentValues values = new ContentValues();

        values.put(UserAccountTable.COL_HXID, user.getHxid());
        values.put(UserAccountTable.COL_NAME, user.getName());
        values.put(UserAccountTable.COL_NICK, user.getNick());
        values.put(UserAccountTable.COL_PHOTO, user.getPhoto());

        db.replace(UserAccountTable.TAB_NAME, null, values);
    }

    // 根据环信id获取所有用户信息
    public UserInfo getAccountByHxId(String hxId) {
        //获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //执行查询
        String sql = String.format("select * from %s where %s = ?", UserAccountTable.TAB_NAME, UserAccountTable.COL_HXID);
        Cursor cursor = db.rawQuery(sql, new String[]{hxId});

        //转换为用户信息
        UserInfo account = null;
        if (cursor.moveToNext()) {
            account = new UserInfo();
            account.setHxid(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_HXID)));
            account.setName(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_NAME)));
            account.setNick(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_NICK)));
            account.setPhoto(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_PHOTO)));
        }

        //关闭资源
        cursor.close();

        //返回数据
        return account;
    }
}
