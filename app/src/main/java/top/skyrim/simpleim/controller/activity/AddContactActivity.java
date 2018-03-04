package top.skyrim.simpleim.controller.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import top.skyrim.simpleim.R;
import top.skyrim.simpleim.model.Model;
import top.skyrim.simpleim.model.bean.UserInfo;

public class AddContactActivity extends Activity {

    private TextView tv_add_find;   //查找按钮
    private EditText et_add_name;   //用户名输入框

    private RelativeLayout rl_add;  //添加联系人模块
    private ImageView iv_add_photo; //头像
    private TextView tv_add_name;   //用户名
    private Button btn_add_add;     //添加按钮
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        initView();

        initListener();
    }

    private void initListener() {
        //查找按钮的点击事件
        tv_add_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                find();
            }
        });

        //添加按钮的点击事件
        btn_add_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add();
            }
        });
    }

    private void add() {
        //连接环信服务器添加好友
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().addContact(userInfo.getHxid(), "添加好友");

                    //更新UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddContactActivity.this, "发送添加好友邀请成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddContactActivity.this, String.format("发送添加好友邀请失败 %s", e), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void find() {
        //获取输入的用户名
        final String name = et_add_name.getText().toString();

        //校验输入的用户名
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "用户名不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }

        //模拟去环信服务器判断当前用户是否存在
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //模拟查找当前用户名是否存在
                userInfo = new UserInfo(name);

                //更新UI显示
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rl_add.setVisibility(View.VISIBLE);
                        tv_add_name.setText(userInfo.getName());
                    }
                });
            }
        });
    }

    private void initView() {
        tv_add_find = (TextView) findViewById(R.id.tv_add_find);
        et_add_name = (EditText) findViewById(R.id.et_add_name);
        rl_add = (RelativeLayout) findViewById(R.id.rl_add);
        iv_add_photo = (ImageView) findViewById(R.id.iv_add_photo);
        tv_add_name = (TextView) findViewById(R.id.tv_add_name);
        btn_add_add = (Button) findViewById(R.id.btn_add_add);
    }
}
