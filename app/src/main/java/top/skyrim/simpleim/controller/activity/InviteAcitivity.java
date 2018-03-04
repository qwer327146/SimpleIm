package top.skyrim.simpleim.controller.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ListView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import top.skyrim.simpleim.R;
import top.skyrim.simpleim.controller.adapter.InviteAdapter;
import top.skyrim.simpleim.model.Model;
import top.skyrim.simpleim.model.bean.InvitationInfo;
import top.skyrim.simpleim.utils.Constant;

public class InviteAcitivity extends Activity {

    private ListView lv_invite;
    private InviteAdapter inviteAdapter;
    private InviteAdapter.OnInviteListener mOnInviteListener = new InviteAdapter.OnInviteListener() {
        //接受好友邀请
        @Override
        public void onAccept(InvitationInfo invitationInfo) {

            //通知环信服务器接受好友邀请
            try {
                EMClient.getInstance().contactManager().acceptInvitation(invitationInfo.getUser().getHxid());

                //更新本地数据库
                invitationInfo.setStatus(InvitationInfo.InvitationStatus.INVITE_ACCEPT);

                Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

                //更新UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(InviteAcitivity.this, "接受了邀请", Toast.LENGTH_SHORT).show();

                        //刷新页面
                        refresh();
                    }
                });
            } catch (final HyphenateException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(InviteAcitivity.this, String.format("接受邀请失败 %s", e), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        //拒绝好友邀请
        @Override
        public void onReject(InvitationInfo invitationInfo) {
            //通知环信服务器拒绝邀请
            try {
                EMClient.getInstance().contactManager().declineInvitation(invitationInfo.getUser().getHxid());

                //更新本地数据库
                Model.getInstance().getDbManager().getInviteTableDao().removeInvitation(invitationInfo.getUser().getHxid());

                //更新UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(InviteAcitivity.this, "拒绝了邀请", Toast.LENGTH_SHORT).show();

                        //刷新页面
                        refresh();
                    }
                });
            } catch (final HyphenateException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(InviteAcitivity.this, String.format("拒绝失败 %s", e), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        //接受加群申请
        @Override
        public void onApplicationAccept(final InvitationInfo invitationInfo) {
            //通知环信服务器接受加群申请
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().acceptApplication(invitationInfo.getGroup().getGroupId(),
                                invitationInfo.getGroup().getInvitePerson());

                        //更新本地数据
                        invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION);
                        Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

                        //刷新UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteAcitivity.this, "接受加群申请成功", Toast.LENGTH_SHORT).show();

                                //刷新页面
                                refresh();
                            }
                        });

                    } catch (final HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteAcitivity.this, String.format("接受加群申请失败 %s", e), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

        //拒绝加群申请
        @Override
        public void onApplicationReject(final InvitationInfo invitationInfo) {
            //通知环信服务器拒绝加群申请
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().declineApplication(invitationInfo.getGroup().getGroupId(),
                                invitationInfo.getGroup().getGroupName(),
                                invitationInfo.getReason());

                        //更新本地数据
                        invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_REJECT_APPLICATION);
                        Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

                        //更新UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteAcitivity.this, "拒绝加群申请", Toast.LENGTH_SHORT).show();

                                //刷新页面
                                refresh();
                            }
                        });


                    } catch (final HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteAcitivity.this, String.format("拒绝加群申请失败 %s", e), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

        //接受加群邀请
        @Override
        public void onInviteAccept(final InvitationInfo invitationInfo) {
            //通知环信服务器接受加群邀请
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().acceptInvitation(invitationInfo.getGroup().getGroupId(), invitationInfo.getGroup().getInvitePerson());

                        //更新本地数据库
                        invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_ACCEPT_INVITE);
                        Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

                        //刷新页面
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteAcitivity.this, "接受加群邀请成功", Toast.LENGTH_SHORT).show();

                                //刷新页面
                                refresh();
                            }
                        });

                    } catch (final HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteAcitivity.this, String.format("拒绝加群邀请失败 %s", e), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

        //拒绝加群邀请
        @Override
        public void onInviteReject(final InvitationInfo invitationInfo) {
            //通知环信服务器拒绝加群邀请
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().declineInvitation(invitationInfo.getGroup().getGroupId(),
                                invitationInfo.getGroup().getInvitePerson(),
                                invitationInfo.getReason());

                        //更新本地数据库
                        invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_REJECT_INVITE);
                        Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

                        //刷新页面
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteAcitivity.this, "拒绝加群邀请成功", Toast.LENGTH_SHORT).show();

                                //刷新页面
                                refresh();
                            }
                        });
                    } catch (final HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteAcitivity.this, String.format("拒绝加群邀请失败 %s", e), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    };
    private LocalBroadcastManager mLBM;
    private BroadcastReceiver InviteChangeListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //刷新页面
            refresh();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        initView();

        initData();
    }

    private void initData() {
        inviteAdapter = new InviteAdapter(this, mOnInviteListener);
        lv_invite.setAdapter(inviteAdapter);

        //刷新方法
        refresh();

        //注册邀请信息变化的广播接收器
        mLBM = LocalBroadcastManager.getInstance(this);
        mLBM.registerReceiver(InviteChangeListener, new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
        mLBM.registerReceiver(InviteChangeListener, new IntentFilter(Constant.GROUP_INVITE_CHANGED));
    }

    private void refresh() {
        //获取数据库中所有邀请信息
        List<InvitationInfo> invitationInfos = Model.getInstance().getDbManager().getInviteTableDao().getInvitations();

        //刷新适配器
        inviteAdapter.refresh(invitationInfos);
    }

    private void initView() {
        lv_invite = (ListView) findViewById(R.id.lv_invite);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mLBM.unregisterReceiver(InviteChangeListener);
    }
}
