package top.skyrim.simpleim.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

import top.skyrim.simpleim.R;
import top.skyrim.simpleim.controller.adapter.GroupDetailAdapter;
import top.skyrim.simpleim.model.Model;
import top.skyrim.simpleim.model.bean.UserInfo;
import top.skyrim.simpleim.utils.Constant;

public class GroupDetailActivity extends Activity {

    private GridView gv_group_detail;
    private Button btn_group_detail;
    private EMGroup mGroup;
    private LocalBroadcastManager mLBM;
    private GroupDetailAdapter groupDetailAdapter;
    private GroupDetailAdapter.OnGroupDetailListener mOnGroupDetailListener = new GroupDetailAdapter.OnGroupDetailListener() {
        @Override
        public void onAddMembers() {
            //跳转到选择联系人页面
            Intent intent = new Intent(GroupDetailActivity.this, PickContactsActivity.class);

            intent.putExtra(Constant.GROUP_ID, mGroup.getGroupId());

            startActivityForResult(intent, 2);
        }

        @Override
        public void onDeleteMembers(final UserInfo userInfo) {
            //通知环信服务器删除群成员
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().removeUserFromGroup(mGroup.getGroupId(), userInfo.getName());

                        //更新界面
                        getMembersFromHxServer();

                        //弹出提示
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "删除群成员成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (final HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, String.format("删除群成员失败 %s", e), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    };
    private List<UserInfo> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        initView();

        getData();

        initData();

        initListener();

    }

    private void initListener() {
        gv_group_detail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //判断是否是删除模式
                        if (groupDetailAdapter.ismIsDeleteMode()) {
                            //设为非删除模式
                            groupDetailAdapter.setmIsDeleteMode(false);

                            //刷新页面
                            groupDetailAdapter.notifyDataSetChanged();
                        }

                        break;
                }

                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 2) {
            final String[] members = data.getStringArrayExtra("members");

            //通知环信服务器添加成员
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().addUsersToGroup(mGroup.getGroupId(), members);

                        //刷新页面
                        getMembersFromHxServer();

                        //弹出提示
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "添加群成员成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (final HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, String.format("添加群成员失败 %s", e), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });


        }
    }

    private void initData() {
        initButtonDisplay();

        initGridView();

        getMembersFromHxServer();
    }

    private void getMembersFromHxServer() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //环信官方获取成员信息方法
                    List<String> memberList = new ArrayList<>();
                    EMCursorResult<String> result = null;
                    final int pageSize = 20;
                    do {
                        result = EMClient.getInstance().groupManager().fetchGroupMembers(mGroup.getGroupId(),
                                result != null ? result.getCursor() : "", pageSize);
                        memberList.addAll(result.getData());
                    }
                    while (!TextUtils.isEmpty(result.getCursor()) && result.getData().size() == pageSize);

                    //校验
                    if (memberList != null && memberList.size() >= 0) {
                        //转换
                        mUsers = new ArrayList<>();
                        for (String member : memberList) {
                            mUsers.add(new UserInfo(member));
                        }

                        //更新界面
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                groupDetailAdapter.refresh(mUsers);
                            }
                        });

                    }


                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupDetailActivity.this, String.format("获取群成员列表失败 %s", e), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void initGridView() {
        //判断是否是群主或是公开群，如果是，则可添加删除群成员
        boolean mIsCanModify = EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner()) || mGroup.isPublic();

        //创建适配器
        groupDetailAdapter = new GroupDetailAdapter(this, mIsCanModify, mOnGroupDetailListener);
        gv_group_detail.setAdapter(groupDetailAdapter);
    }

    private void initButtonDisplay() {
        //判断是否是群主
        if (EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner())) {  //是群主
            btn_group_detail.setText("解散群");

            btn_group_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //通知环信服务器解散群
                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EMClient.getInstance().groupManager().destroyGroup(mGroup.getGroupId());

                                //发送解散群广播
                                exitGroupBroadcast();

                                //更新UI
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "解散群成功", Toast.LENGTH_SHORT).show();

                                        //结束当前页面
                                        finish();
                                    }
                                });
                            } catch (final HyphenateException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, String.format("解散群失败 %s", e), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            });

        } else {    //不是群主
            btn_group_detail.setText("退出群");

            btn_group_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //通知环信服务器退群
                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EMClient.getInstance().groupManager().leaveGroup(mGroup.getGroupId());

                                //发送退群广播
                                exitGroupBroadcast();

                                //更新UI
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "退出群成功", Toast.LENGTH_SHORT).show();

                                        //结束当前页面
                                        finish();
                                    }
                                });
                            } catch (final HyphenateException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, String.format("退出群失败 %s", e), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
    }

    //发送解散群、退群广播
    private void exitGroupBroadcast() {
        mLBM = LocalBroadcastManager.getInstance(this);
        Intent intent = new Intent(Constant.EXIT_GROUP);

        intent.putExtra(Constant.GROUP_ID, mGroup.getGroupId());

        mLBM.sendBroadcast(intent);
    }

    private void getData() {
        String groupId = getIntent().getStringExtra(Constant.GROUP_ID);

        //校验
        if (groupId == null) {
            return;
        } else {
            mGroup = EMClient.getInstance().groupManager().getGroup(groupId);
        }
    }


    private void initView() {
        gv_group_detail = (GridView) findViewById(R.id.gv_group_detail);
        btn_group_detail = (Button) findViewById(R.id.btn_group_detail);
    }
}
