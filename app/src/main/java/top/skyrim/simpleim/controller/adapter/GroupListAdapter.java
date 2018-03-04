package top.skyrim.simpleim.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMGroup;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import top.skyrim.simpleim.R;

/**
 * Created by wangxin on 2018/3/3.
 */

public class GroupListAdapter extends BaseAdapter {

    private Context mContext;
    private List<EMGroup> mEMGroups = new ArrayList<>();

    public GroupListAdapter(Context context) {
        mContext = context;
    }

    //刷新方法
    public void refresh(List<EMGroup> emGroups) {
        //校验
        if (emGroups != null && emGroups.size() >= 0) {
            mEMGroups.clear();

            mEMGroups.addAll(emGroups);
        }

        //通知页面刷新
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mEMGroups == null ? 0 : mEMGroups.size();
    }

    @Override
    public Object getItem(int position) {
        return mEMGroups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //创建或得到ViewHolder
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_grouplist, null);

            holder.name = convertView.findViewById(R.id.tv_grouplist_name);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //获取当前item数据
        EMGroup emGroup = mEMGroups.get(position);

        //显示数据
        holder.name.setText(emGroup.getGroupName());

        //返回view
        return convertView;
    }

    private class ViewHolder {
        private TextView name;
    }
}
