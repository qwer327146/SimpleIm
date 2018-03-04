package top.skyrim.simpleim.controller.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import top.skyrim.simpleim.R;
import top.skyrim.simpleim.controller.activity.LoginActivity;
import top.skyrim.simpleim.model.Model;

/**
 * Created by wangxin on 2018/3/3.
 * 设置fragment
 */

public class SettingFragment extends Fragment {

    private Button btn_setting_logout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_setting, null);

        initView(view);

        return view;
    }

    private void initView(View view) {
        btn_setting_logout = view.findViewById(R.id.btn_setting_logout);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData() {
        //在按钮上显示当前登录用户名
        btn_setting_logout.setText(String.format("退出登录（%s）", EMClient.getInstance().getCurrentUser()));

        btn_setting_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        //通知环信服务器退出
                        EMClient.getInstance().logout(true, new EMCallBack() {
                            //退出成功
                            @Override
                            public void onSuccess() {
                                //关闭数据库
                                Model.getInstance().getDbManager().close();

                                //弹出提示
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "退出成功", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                });
                            }

                            //退出失败
                            @Override
                            public void onError(int i, final String s) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), String.format("退出失败%s", s), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onProgress(int i, String s) {

                            }
                        });
                    }
                });
            }
        });
    }
}
