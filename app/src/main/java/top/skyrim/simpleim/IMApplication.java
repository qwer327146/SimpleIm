package top.skyrim.simpleim;

import android.app.Application;
import android.content.Context;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseUI;

import top.skyrim.simpleim.model.Model;

/**
 * Created by wangxin on 2018/3/3.
 * 初始化全局数据
 */

public class IMApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化EaseUI
        EMOptions options = new EMOptions();
        options.setAcceptInvitationAlways(false);
        options.setAutoAcceptGroupInvitation(false);

        EaseUI.getInstance().init(this, options);

        //初始化模型层数据
        Model.getInstance().init(this);

        //初始化全局上下文对象
        mContext = this;
    }

    public static Context getGlobalApplication() {
        return mContext;
    }
}
