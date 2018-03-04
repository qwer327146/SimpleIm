package top.skyrim.simpleim.utils;

import android.content.Context;
import android.content.SharedPreferences;

import top.skyrim.simpleim.IMApplication;

/**
 * Created by wangxin on 2018/3/3.
 */

public class SpUtils {

    private static SpUtils instance = new SpUtils();
    private static SharedPreferences mSp;
    public static final String IS_NEW_INViTE = "is_new_invite";    //新邀请标记

    private SpUtils() {
    }

    public static SpUtils getInstance() {

        if (mSp == null) {
            mSp = IMApplication.getGlobalApplication().getSharedPreferences("im", Context.MODE_PRIVATE);
        }
        return instance;
    }

    //保存
    public void save(String key, Object value) {
        if (value instanceof String) {
            mSp.edit().putString(key, (String) value).apply();
        } else if (value instanceof Integer) {
            mSp.edit().putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            mSp.edit().putBoolean(key, (Boolean) value);
        }
    }

    //获取String数据
    public String getString(String key, String defValue) {
        return mSp.getString(key, defValue);
    }

    //获取Integer数据
    public int getInt(String key, int defValue) {
        return mSp.getInt(key, defValue);
    }

    //获取Boolean数据
    public boolean getBoolean(String key, boolean defValue) {
        return mSp.getBoolean(key, defValue);
    }
}
