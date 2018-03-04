package top.skyrim.simpleim.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.exceptions.HyphenateException;

import top.skyrim.simpleim.R;
import top.skyrim.simpleim.model.Model;

public class NewGroupActivity extends Activity {

    private EditText et_newgroup_name;  //群组名称
    private EditText et_newgroup_desc;  //群组简介
    private CheckBox cb_newgroup_public;    //是否公开
    private CheckBox cb_newgroup_invite;    //是否开发邀请
    private Button btn_newgroup_create; //创建按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        initView();

        initListener();
    }

    private void initListener() {
        btn_newgroup_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择联系人
                Intent intent = new Intent(NewGroupActivity.this, PickContactsActivity.class);

                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            //创建群
            createGroup(data.getStringArrayExtra("members"));
        }
    }

    private void createGroup(final String[] members) {
        //群名称
        final String groupName = et_newgroup_name.getText().toString();
        //群简介
        final String groupDesc = et_newgroup_desc.getText().toString();

        //去环信服务器创建群
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //参数：群名称、群描述、群成员、原因、参数设置

                EMGroupOptions options = new EMGroupOptions();
                //设置人数
                options.maxUsers = 200;

                EMGroupManager.EMGroupStyle groupStyle = null;


                if (cb_newgroup_public.isChecked()) { //公开
                    if (cb_newgroup_invite.isChecked()) { //开放邀请
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
                    } else {    //不可邀请
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;
                    }
                } else {    //非公开
                    if (cb_newgroup_invite.isChecked()) {//开放邀请
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
                    } else {//不可邀请
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
                    }
                }

                options.style = groupStyle;

                //邀请对方进群需要同意
                options.inviteNeedConfirm = true;

                try {

                    EMClient.getInstance().groupManager().createGroup(groupName, groupDesc, members, "创建群", options);

                    //更新UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewGroupActivity.this, String.format("创建群 %s 成功", groupName), Toast.LENGTH_SHORT).show();

                            finish();
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewGroupActivity.this, String.format("创建群 %s 失败，%s", groupName, e), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void initView() {
        et_newgroup_name = (EditText) findViewById(R.id.et_newgroup_name);
        et_newgroup_desc = (EditText) findViewById(R.id.et_newgroup_desc);
        cb_newgroup_public = (CheckBox) findViewById(R.id.cb_newgroup_public);
        cb_newgroup_invite = (CheckBox) findViewById(R.id.cb_newgroup_invite);
        btn_newgroup_create = (Button) findViewById(R.id.btn_newgroup_create);
    }
}
