package top.skyrim.simpleim.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

import top.skyrim.simpleim.R;
import top.skyrim.simpleim.controller.adapter.PickContactsAdapter;
import top.skyrim.simpleim.model.Model;
import top.skyrim.simpleim.model.bean.PickContactInfo;
import top.skyrim.simpleim.model.bean.UserInfo;
import top.skyrim.simpleim.utils.Constant;

public class PickContactsActivity extends Activity {

    private TextView tv_pick_save;
    private ListView lv_pick;
    private List<PickContactInfo> mPicks;
    private PickContactsAdapter pickContactsAdapter;
    private List<String> mExistingMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contacts);

        initView();

        getData();

        initData();

        initListener();
    }

    private void getData() {
        String groupId = getIntent().getStringExtra(Constant.GROUP_ID);

        if (groupId != null) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
            mExistingMembers = group.getMembers();
        }

        //防止空指针异常
        if (mExistingMembers == null) {
            mExistingMembers = new ArrayList<>();
        }
    }

    private void initListener() {
        lv_pick.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取当前item的checkbox对象
                CheckBox cb_pick = view.findViewById(R.id.cb_pick);

                //设置状态
                cb_pick.setChecked(!cb_pick.isChecked());

                //修改数据
                mPicks.get(position).setChecked(cb_pick.isChecked());

                //刷新页面
                pickContactsAdapter.notifyDataSetChanged();
            }
        });

        tv_pick_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> addMembers = pickContactsAdapter.getPickContacts();

                Intent intent = new Intent();
                intent.putExtra("members", addMembers.toArray(new String[0]));
                setResult(Activity.RESULT_OK, intent);

                finish();

            }
        });
    }

    private void initData() {
        //获取所有联系人数据
        List<UserInfo> contacts = Model.getInstance().getDbManager().getContactTableDao().getContacts();

        mPicks = new ArrayList<>();
        //校验
        if (contacts != null && contacts.size() >= 0) {
            for (UserInfo contact : contacts) {
                mPicks.add(new PickContactInfo(contact, false));
            }
        }

        //初始化ListView
        pickContactsAdapter = new PickContactsAdapter(this, mPicks, mExistingMembers);
        lv_pick.setAdapter(pickContactsAdapter);

    }

    private void initView() {
        tv_pick_save = (TextView) findViewById(R.id.tv_pick_save);
        lv_pick = (ListView) findViewById(R.id.lv_pick);
    }
}
