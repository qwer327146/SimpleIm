package top.skyrim.simpleim.controller.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.skyrim.simpleim.R;
import top.skyrim.simpleim.controller.activity.AddContactActivity;
import top.skyrim.simpleim.controller.activity.ChatActivity;
import top.skyrim.simpleim.controller.activity.GroupListActivity;
import top.skyrim.simpleim.controller.activity.InviteAcitivity;
import top.skyrim.simpleim.model.Model;
import top.skyrim.simpleim.model.bean.UserInfo;
import top.skyrim.simpleim.utils.Constant;
import top.skyrim.simpleim.utils.SpUtils;

/**
 * Created by wangxin on 2018/3/3.
 * 联系人列表fragment
 */

public class ContactListFragment extends EaseContactListFragment {

    private ImageView iv_contact_red;
    private LinearLayout ll_contact_invite;
    private LocalBroadcastManager mLBM;
    private BroadcastReceiver ContactInviteChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //更新红点显示
            iv_contact_red.setVisibility(View.VISIBLE);

            //保存红点状态
            SpUtils.getInstance().save(SpUtils.IS_NEW_INViTE, true);
        }
    };
    private BroadcastReceiver ContactChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //刷新数据和页面
            refreshContact();
        }
    };
    private BroadcastReceiver GroupInviteChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //更新红点显示
            iv_contact_red.setVisibility(View.VISIBLE);

            //保存红点状态
            SpUtils.getInstance().save(SpUtils.IS_NEW_INViTE, true);
        }
    };
    private String mHxid;
    private LinearLayout ll_contact_group;


    @Override
    protected void initView() {
        super.initView();

        //布局显示右上角加号
        titleBar.setRightImageResource(R.drawable.em_add);

        //添加头布局
        View headerView = View.inflate(getActivity(), R.layout.fragment_header_contact, null);

        listView.addHeaderView(headerView);

        //获取红点对象
        iv_contact_red = headerView.findViewById(R.id.iv_contact_red);

        //获取邀请信息条目的对象
        ll_contact_invite = headerView.findViewById(R.id.ll_contact_invite);

        //设置listview条目的点击事件
        setContactListItemClickListener(new EaseContactListItemClickListener() {
            @Override
            public void onListItemClicked(EaseUser user) {
                //校验
                if (user == null) {
                    return;
                }

                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername());
                startActivity(intent);

            }
        });

        //群组条目的点击事件
        ll_contact_group = headerView.findViewById(R.id.ll_contact_group);
        ll_contact_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GroupListActivity.class);
                getActivity().startActivity(intent);
            }
        });

    }

    @Override
    protected void setUpView() {
        super.setUpView();

        //加号的点击事件
        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //启动添加联系人页面
                Intent intent = new Intent(getActivity(), AddContactActivity.class);
                startActivity(intent);
            }
        });

        //初始化红点的显示
        boolean isNewInvite = SpUtils.getInstance().getBoolean(SpUtils.IS_NEW_INViTE, false);
        iv_contact_red.setVisibility(isNewInvite ? View.VISIBLE : View.GONE);

        //邀请信息条目的点击事件
        ll_contact_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //更新红点显示
                iv_contact_red.setVisibility(View.GONE);
                SpUtils.getInstance().save(SpUtils.IS_NEW_INViTE, false);

                //启动邀请信息列表界面
                Intent intent = new Intent(getActivity(), InviteAcitivity.class);
                getActivity().startActivity(intent);
            }
        });

        //注册广播接收器
        mLBM = LocalBroadcastManager.getInstance(getActivity());
        mLBM.registerReceiver(ContactInviteChangeReceiver, new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
        mLBM.registerReceiver(ContactChangeReceiver, new IntentFilter(Constant.CONTACT_CHANGED));
        mLBM.registerReceiver(GroupInviteChangeReceiver, new IntentFilter(Constant.GROUP_INVITE_CHANGED));

        //从环信服务器获取所有联系人信息
        getContactFromHxServer();

        //注册上下文菜单
        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;

        EaseUser easeUser = (EaseUser) listView.getItemAtPosition(position);

        mHxid = easeUser.getUsername();

        getActivity().getMenuInflater().inflate(R.menu.delete, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.contact_delete) {
            deleteContact();
            return true;
        }

        return super.onContextItemSelected(item);
    }

    private void deleteContact() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //从环信服务器删除联系人
                    EMClient.getInstance().contactManager().deleteContact(mHxid);

                    //从本地数据库删除联系人
                    Model.getInstance().getDbManager().getContactTableDao().deleteContactByHxId(mHxid);

                    //刷新页面
                    if (getActivity() == null) {
                        return;
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //刷新联系人列表和页面
                            refreshContact();

                            //弹出提示
                            Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (HyphenateException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), String.format("删除失败 %s", e), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getContactFromHxServer() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //从环信服务器获取所有联系人
                    List<String> hxids = EMClient.getInstance().contactManager().getAllContactsFromServer();

                    //校验
                    if (hxids != null || hxids.size() >= 0) {
                        List<UserInfo> contacts = new ArrayList<>();

                        //遍历转换
                        for (String hxid : hxids) {
                            contacts.add(new UserInfo(hxid));
                        }

                        //保存联系人信息到本地数据库
                        Model.getInstance().getDbManager().getContactTableDao().saveContacts(contacts, true);

                        //刷新页面
                        if (getActivity() == null) {
                            return;
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshContact();
                            }
                        });
                    }
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //刷新页面
    private void refreshContact() {
        //获取数据
        List<UserInfo> contacts = Model.getInstance().getDbManager().getContactTableDao().getContacts();

        //校验
        if (contacts != null && contacts.size() >= 0) {
            //遍历转换
            Map<String, EaseUser> contactsMap = new HashMap<>();
            EaseUser easeUser = null;

            for (UserInfo contact : contacts) {
                easeUser = new EaseUser(contact.getName());
                contactsMap.put(contact.getHxid(), easeUser);
            }

            //刷新数据
            setContactsMap(contactsMap);

            //刷新适配器
            refresh();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mLBM.unregisterReceiver(ContactInviteChangeReceiver);
        mLBM.unregisterReceiver(ContactChangeReceiver);
    }
}
