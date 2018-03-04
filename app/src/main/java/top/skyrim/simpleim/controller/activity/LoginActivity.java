package top.skyrim.simpleim.controller.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import top.skyrim.simpleim.R;
import top.skyrim.simpleim.model.Model;
import top.skyrim.simpleim.model.bean.UserInfo;

/**
 * 登录、注册页面
 */

public class LoginActivity extends Activity {

    private EditText et_login_name; //用户名
    private EditText et_login_pwd;  //密码
    private Button btn_login_regist;    //注册
    private Button btn_login_login; //登录

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //初始化
        initView();

        //初始化监听器
        initListener();
    }

    private void initListener() {
        //注册按钮监听
        btn_login_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regist();
            }
        });

        //登录按钮监听
        btn_login_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        //获取用户名和密码
        final String loginName = et_login_name.getText().toString();
        final String loginPwd = et_login_pwd.getText().toString();

        //校验
        if (TextUtils.isEmpty(loginName) || TextUtils.isEmpty(loginPwd)) {
            //弹出提示
            Toast.makeText(this, "用户名和密码不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }

        //显示进度条
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("正在登录,请稍等");
        pd.show();
        //去环信服务器登录
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                EMClient.getInstance().login(loginName, loginPwd, new EMCallBack() {

                    //登录成功
                    @Override
                    public void onSuccess() {
                        //对模型层数据处理
                        Model.getInstance().loginSuccess(new UserInfo(loginName));

                        //保存用户账号信息到本地数据库
                        Model.getInstance().getUserAccountDao().addAccount(new UserInfo(loginName));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //关闭进度条
                                pd.dismiss();

                                //提示登录成功
                                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

                                //跳转到主页面
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);

                                finish();
                            }
                        });
                    }

                    //登录失败
                    @Override
                    public void onError(int i, final String s) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //提示登录失败
                                Toast.makeText(LoginActivity.this, String.format("登录失败 %s", s), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
            }
        });

    }

    private void regist() {
        //获取用户名和密码
        final String registName = et_login_name.getText().toString();
        final String registPwd = et_login_pwd.getText().toString();

        //校验
        if (TextUtils.isEmpty(registName) || TextUtils.isEmpty(registPwd)) {
            //提示
            Toast.makeText(this, "用户名和密码不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }

        //去环信服务器注册
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(registName, registPwd);

                    //提示注册成功
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    //提示注册失败
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "注册失败" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void initView() {
        et_login_name = (EditText) findViewById(R.id.et_login_name);
        et_login_pwd = (EditText) findViewById(R.id.et_login_pwd);
        btn_login_regist = (Button) findViewById(R.id.btn_login_regist);
        btn_login_login = (Button) findViewById(R.id.btn_login_login);
    }
}
