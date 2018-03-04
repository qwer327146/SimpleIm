package top.skyrim.simpleim.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import top.skyrim.simpleim.R;
import top.skyrim.simpleim.model.bean.PickContactInfo;

/**
 * Created by wangxin on 2018/3/3.
 */

public class PickContactsAdapter extends BaseAdapter {

    private Context mContext;
    private List<PickContactInfo> mPicks = new ArrayList<>();
    private List<String> mExistingMembers = new ArrayList<>();

    public PickContactsAdapter(Context context, List<PickContactInfo> picks, List<String> existingMembers) {
        mContext = context;
        //校验
        if (picks != null && picks.size() >= 0) {
            mPicks.clear();

            mPicks.addAll(picks);
        }

        mExistingMembers.clear();

        mExistingMembers.addAll(existingMembers);
    }

    @Override
    public int getCount() {
        return mPicks == null ? 0 : mPicks.size();
    }

    @Override
    public Object getItem(int position) {
        return mPicks.get(position);
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
            convertView = View.inflate(mContext, R.layout.item_pick, null);

            holder.tv_name = convertView.findViewById(R.id.tv_pick_name);
            holder.cb_checked = convertView.findViewById(R.id.cb_pick);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //获取当前item数据
        PickContactInfo pickContactInfo = mPicks.get(position);

        //显示数据
        holder.tv_name.setText(pickContactInfo.getUser().getName());
        holder.cb_checked.setChecked(pickContactInfo.isChecked());

        //判断
        if (mExistingMembers.contains(pickContactInfo.getUser().getName())) {
            holder.cb_checked.setChecked(true);
            pickContactInfo.setChecked(true);
        }

        //返回view
        return convertView;
    }

    public List<String> getPickContacts() {
        List<String> picks = new ArrayList<>();

        for (PickContactInfo pick : mPicks) {
            if (pick.isChecked()) {
                picks.add(pick.getUser().getName());
            }
        }

        return picks;
    }

    private class ViewHolder {
        private TextView tv_name;
        private CheckBox cb_checked;
    }
}
