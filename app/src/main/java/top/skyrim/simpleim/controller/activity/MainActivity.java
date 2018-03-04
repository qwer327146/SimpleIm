package top.skyrim.simpleim.controller.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import top.skyrim.simpleim.R;
import top.skyrim.simpleim.controller.fragment.ChatFragment;
import top.skyrim.simpleim.controller.fragment.ContactListFragment;
import top.skyrim.simpleim.controller.fragment.SettingFragment;

public class MainActivity extends FragmentActivity {

    private FrameLayout fl_main;
    private RadioGroup rg_main;
    private ChatFragment mChatFragment;
    private ContactListFragment mContactListFragment;
    private SettingFragment mSettingFragment;
    private static final int REQUEST_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initData();

        initListener();

        //获取权限
        getPermissions();
    }

    private void initListener() {
        rg_main.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                Fragment fragment = null;

                switch (checkedId) {
                    case R.id.rb_main_chat: //会话
                        fragment = mChatFragment;
                        break;

                    case R.id.rb_main_contact:  //联系人
                        fragment = mContactListFragment;
                        break;

                    case R.id.rb_main_setting:  //设置
                        fragment = mSettingFragment;
                        break;
                }

                //加载到选中的fragment
                switchFragment(fragment);
            }
        });
        //设置默认选中项
        rg_main.check(R.id.rb_main_chat);
    }

    private void switchFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fl_main, fragment).commit();
    }

    private void initData() {
        mChatFragment = new ChatFragment();
        mContactListFragment = new ContactListFragment();
        mSettingFragment = new SettingFragment();
    }

    private void initView() {
        fl_main = (FrameLayout) findViewById(R.id.fl_main);
        rg_main = (RadioGroup) findViewById(R.id.rg_main);
    }

    //使用谷歌EasyPermissions获取权限
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_PERMISSIONS)
    public void getPermissions(){
        String[] params = {Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.LOCATION_HARDWARE};

        if(!EasyPermissions.hasPermissions(this, params)){
            EasyPermissions.requestPermissions(this, "请求权限", REQUEST_PERMISSIONS, params);
        }
    }
}
