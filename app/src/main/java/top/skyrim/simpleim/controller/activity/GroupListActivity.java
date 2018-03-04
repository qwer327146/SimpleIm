package top.skyrim.simpleim.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMContact;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import top.skyrim.simpleim.R;
import top.skyrim.simpleim.controller.adapter.GroupListAdapter;
import top.skyrim.simpleim.model.Model;

public class GroupListActivity extends Activity {

    private ListView lv_grouplist;
    private LinearLayout ll_grouplist;
    private GroupListAdapter groupListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        initView();

        initData();

        initListener();
    }

    private void initListener() {
        //群条目的点击事件
        lv_grouplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转到聊天页面
                Intent intent = new Intent(GroupListActivity.this, ChatActivity.class);
                //获取群ID
                String groupId = EMClient.getInstance().groupManager().getAllGroups().get(position - 1).getGroupId();
                intent.putExtra(EaseConstant.EXTRA_USER_ID, groupId);

                //保存聊天类型为群聊
                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
                startActivity(intent);
            }
        });

        //创建新群的点击事件
        ll_grouplist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupListActivity.this, NewGroupActivity.class);

                startActivity(intent);
            }
        });
    }

    private void initData() {
        //创建适配器
        groupListAdapter = new GroupListAdapter(this);

        lv_grouplist.setAdapter(groupListAdapter);

        //获取群组数据
        getGroupFromHxServer();

    }

    private void getGroupFromHxServer() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //从环信服务器获取群信息
                try {
                    final List<EMGroup> emGroups = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();

                    //刷新页面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupListActivity.this, "加载群信息成功", Toast.LENGTH_SHORT).show();

                            //刷新（从SDK数据库中获取群信息）
                            refresh();
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupListActivity.this, String.format("加载群信息失败 %s", e), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    //刷新页面
    private void refresh() {
        groupListAdapter.refresh(EMClient.getInstance().groupManager().getAllGroups());
    }

    private void initView() {
        lv_grouplist = (ListView) findViewById(R.id.lv_grouplist);

        //添加头布局
        View headerView = View.inflate(this, R.layout.header_grouplist, null);

        lv_grouplist.addHeaderView(headerView);

        //获取头布局中的元素
        ll_grouplist = headerView.findViewById(R.id.ll_grouplist);
    }

    @Override
    protected void onResume() {
        super.onResume();

        refresh();
    }
}
