package top.skyrim.simpleim.controller.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.hyphenate.chat.EMClient;

import top.skyrim.simpleim.R;
import top.skyrim.simpleim.model.Model;
import top.skyrim.simpleim.model.bean.UserInfo;

/**
 * 欢迎页面
 */

public class SplashActivity extends Activity {

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //如果按了返回键，则停止发送消息
            if (isFinishing()) {
                return;
            }

            toMainOrLogin();
        }
    };

    private void toMainOrLogin() {

        //去环信服务器判断是否当前用户已经登录
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() { //登陆过

                if (EMClient.getInstance().isLoggedInBefore()) {
                    //获取用户信息
                    UserInfo account = Model.getInstance().getUserAccountDao().getAccountByHxId(EMClient.getInstance().getCurrentUser());

                    if (account == null) {
                        //登录页面
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        //登录成功后的方法
                        Model.getInstance().loginSuccess(account);

                        //主页面
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                } else {    //未登陆过

                    //登录页面
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler.sendMessageDelayed(Message.obtain(), 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
