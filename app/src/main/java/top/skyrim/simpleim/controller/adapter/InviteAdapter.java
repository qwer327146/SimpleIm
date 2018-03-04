package top.skyrim.simpleim.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import top.skyrim.simpleim.R;
import top.skyrim.simpleim.model.bean.InvitationInfo;
import top.skyrim.simpleim.model.bean.UserInfo;

/**
 * Created by wangxin on 2018/3/3.
 * 邀请信息列表的适配器
 */

public class InviteAdapter extends BaseAdapter {

    private Context mContext;
    private List<InvitationInfo> mInvitationInfos = new ArrayList<>();
    private OnInviteListener mOnInviteListener;
    private InvitationInfo invitationInfo;

    public InviteAdapter(Context context, OnInviteListener onInviteListener) {
        mContext = context;

        mOnInviteListener = onInviteListener;
    }

    public void refresh(List<InvitationInfo> invitationInfos) {
        //校验
        if (invitationInfos != null && invitationInfos.size() >= 0) {

            mInvitationInfos.clear();

            mInvitationInfos.addAll(invitationInfos);
        }

        //通知刷新
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mInvitationInfos == null ? 0 : mInvitationInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mInvitationInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //创建或获取ViewHolder
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_invite, null);

            holder.name = convertView.findViewById(R.id.tv_invite_name);
            holder.reason = convertView.findViewById(R.id.tv_invite_reason);

            holder.accept = convertView.findViewById(R.id.btn_invite_accpet);
            holder.reject = convertView.findViewById(R.id.btn_invite_reject);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //获取当前item的数据
        invitationInfo = mInvitationInfos.get(position);
        UserInfo user = invitationInfo.getUser();
        //显示数据
        //判断是联系人邀请或群邀请
        if (user != null) {   //联系人邀请
            //显示名称
            holder.name.setText(user.getName());

            holder.accept.setVisibility(View.GONE);
            holder.reject.setVisibility(View.GONE);

            if (invitationInfo.getStatus() == InvitationInfo.InvitationStatus.NEW_INVITE) {   //新邀请
                //显示原因
                if (invitationInfo.getReason() == null) {
                    holder.reason.setText("添加好友");
                } else {
                    holder.reason.setText(invitationInfo.getReason());
                }

                holder.accept.setVisibility(View.VISIBLE);
                holder.reject.setVisibility(View.VISIBLE);

            } else if (invitationInfo.getStatus() == InvitationInfo.InvitationStatus.INVITE_ACCEPT) { //接受邀请

                if (invitationInfo.getReason() == null) {
                    holder.reason.setText("接受邀请");
                } else {
                    holder.reason.setText(invitationInfo.getReason());
                }

            } else if (invitationInfo.getStatus() == InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER) { //邀请被接受

                if (invitationInfo.getReason() == null) {
                    holder.reason.setText("邀请被接受");
                } else {
                    holder.reason.setText(invitationInfo.getReason());
                }
            }

            //接受按钮的处理
            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnInviteListener.onAccept(invitationInfo);
                }
            });

            //拒绝按钮的处理
            holder.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnInviteListener.onReject(invitationInfo);
                }
            });

        } else {    //群邀请
            holder.name.setText(invitationInfo.getGroup().getInvitePerson());

            holder.accept.setVisibility(View.GONE);
            holder.reject.setVisibility(View.GONE);

            switch (invitationInfo.getStatus()) {
                // 您的群申请请已经被接受
                case GROUP_APPLICATION_ACCEPTED:
                    holder.reason.setText("您的群申请请已经被接受");

                    break;
                //  您的群邀请已经被接收
                case GROUP_INVITE_ACCEPTED:
                    holder.reason.setText("您的群邀请已经被接收");
                    break;

                // 你的群申请已经被拒绝
                case GROUP_APPLICATION_DECLINED:
                    holder.reason.setText("你的群申请已经被拒绝");
                    break;

                // 您的群邀请已经被拒绝
                case GROUP_INVITE_DECLINED:
                    holder.reason.setText("您的群邀请已经被拒绝");
                    break;

                // 您收到了群邀请
                case NEW_GROUP_INVITE:
                    holder.reason.setText("您收到了群邀请");

                    holder.accept.setVisibility(View.VISIBLE);
                    holder.reject.setVisibility(View.VISIBLE);

                    //接受按钮的点击事件
                    holder.accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onInviteAccept(invitationInfo);
                        }
                    });

                    //拒绝按钮的点击事件
                    holder.reject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onInviteReject(invitationInfo);
                        }
                    });

                    break;

                // 您收到了群申请
                case NEW_GROUP_APPLICATION:
                    holder.reason.setText("您收到了群申请");

                    holder.accept.setVisibility(View.VISIBLE);
                    holder.reject.setVisibility(View.VISIBLE);

                    //接受按钮的点击事件
                    holder.accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onApplicationAccept(invitationInfo);
                        }
                    });

                    //拒绝按钮的点击事件
                    holder.reject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onApplicationReject(invitationInfo);
                        }
                    });
                    break;

                // 你接受了群邀请
                case GROUP_ACCEPT_INVITE:
                    holder.reason.setText("接受了群邀请");
                    break;

                // 您批准了群加入
                case GROUP_ACCEPT_APPLICATION:
                    holder.reason.setText("您批准了群加入");
                    break;
            }
        }

        //返回view
        return convertView;
    }

    private class ViewHolder {
        private TextView name;
        private TextView reason;

        private Button accept;
        private Button reject;
    }

    public interface OnInviteListener {

        //接受联系人邀请
        void onAccept(InvitationInfo invitationInfo);

        //拒绝联系人邀请
        void onReject(InvitationInfo invitationInfo);

        //接受群申请
        void onApplicationAccept(InvitationInfo invitationInfo);

        //拒绝群申请
        void onApplicationReject(InvitationInfo invitationInfo);

        //接受群邀请
        void onInviteAccept(InvitationInfo invitationInfo);

        //拒绝群邀请
        void onInviteReject(InvitationInfo invitationInfo);
    }
}
