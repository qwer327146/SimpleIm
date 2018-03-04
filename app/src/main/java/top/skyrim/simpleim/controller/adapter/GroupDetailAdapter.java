package top.skyrim.simpleim.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import top.skyrim.simpleim.R;
import top.skyrim.simpleim.model.bean.UserInfo;


/**
 * Created by wangxin on 2018/3/4.
 */

public class GroupDetailAdapter extends BaseAdapter {

    private Context mContext;
    private boolean mIsCanModify;   //是否可以添加/删除成员
    private boolean mIsDeleteMode;  //是否是删除模式 true:是 false:否
    private List<UserInfo> mUsers = new ArrayList<>();
    private OnGroupDetailListener mOnGroupDetailListener;

    public GroupDetailAdapter(Context context, boolean isCanModify, OnGroupDetailListener onGroupDetailListener) {
        mContext = context;
        mIsCanModify = isCanModify;
        mOnGroupDetailListener = onGroupDetailListener;
    }

    public void refresh(List<UserInfo> users) {
        //校验
        if (users != null && users.size() >= 0) {
            mUsers.clear();

            initUsers();

            mUsers.addAll(0, users);
        }

        //刷新页面
        notifyDataSetChanged();
    }

    private void initUsers() {
        UserInfo add = new UserInfo("add");
        UserInfo delete = new UserInfo("delete");

        mUsers.add(delete);
        mUsers.add(0, add);
    }

    public boolean ismIsDeleteMode() {
        return mIsDeleteMode;
    }

    public void setmIsDeleteMode(boolean mIsDeleteMode) {
        this.mIsDeleteMode = mIsDeleteMode;
    }

    @Override
    public int getCount() {
        return mUsers == null ? 0 : mUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return mUsers.get(position);
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
            convertView = View.inflate(mContext, R.layout.item_groupdetail, null);

            holder.photo = convertView.findViewById(R.id.iv_groupdetail_photo);
            holder.delete = convertView.findViewById(R.id.iv_groupdetail_delete);
            holder.name = convertView.findViewById(R.id.tv_groupdetail_name);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //获取当前item数据
        final UserInfo user = mUsers.get(position);

        //显示数据
        if (mIsCanModify) {   //可以增减成员
            if (position == getCount() - 1) { //减号

                if (mIsDeleteMode) {    //删除模式

                    convertView.setVisibility(View.INVISIBLE);
                } else {    //普通模式

                    convertView.setVisibility(View.VISIBLE);

                    holder.photo.setImageResource(R.drawable.em_smiley_minus_btn_pressed);
                    holder.name.setVisibility(View.GONE);
                    holder.delete.setVisibility(View.GONE);
                }
            } else if (position == getCount() - 2) { //加号

                if (mIsDeleteMode) {  //删除模式

                    convertView.setVisibility(View.INVISIBLE);
                } else {    //普通模式

                    convertView.setVisibility(View.VISIBLE);
                    holder.photo.setImageResource(R.drawable.em_smiley_add_btn_pressed);
                    holder.name.setVisibility(View.GONE);
                    holder.delete.setVisibility(View.GONE);
                }
            } else {    //群成员

                convertView.setVisibility(View.VISIBLE);

                holder.photo.setVisibility(View.VISIBLE);
                holder.photo.setImageResource(R.drawable.em_default_avatar);

                holder.name.setVisibility(View.VISIBLE);
                holder.name.setText(user.getName());

                //删除模式判断
                if (mIsDeleteMode) {
                    holder.delete.setVisibility(View.VISIBLE);
                } else {
                    holder.delete.setVisibility(View.GONE);
                }
            }

            //点击事件
            if (position == getCount() - 1) { //减号

                holder.photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mIsDeleteMode) {
                            //进入删除模式
                            mIsDeleteMode = true;

                            notifyDataSetChanged();
                        }
                    }
                });
            } else if (position == getCount() - 2) { //加号
                holder.photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnGroupDetailListener.onAddMembers();
                    }
                });
            } else {    //群成员
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnGroupDetailListener.onDeleteMembers(user);
                    }
                });
            }

        } else {    //不可增减成员

            if (position == getCount() - 1 || position == getCount() - 2) {
                convertView.setVisibility(View.GONE);
            } else {
                convertView.setVisibility(View.VISIBLE);

                holder.name.setText(user.getName());

                holder.photo.setImageResource(R.drawable.em_default_avatar);

                holder.delete.setVisibility(View.GONE);
            }
        }

        //返回view
        return convertView;
    }

    private class ViewHolder {
        private ImageView photo;
        private ImageView delete;
        private TextView name;
    }

    public interface OnGroupDetailListener {
        //添加成员
        void onAddMembers();

        //删除成员
        void onDeleteMembers(UserInfo userInfo);
    }
}
