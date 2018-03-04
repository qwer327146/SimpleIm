package top.skyrim.simpleim.model;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import top.skyrim.simpleim.model.bean.UserInfo;
import top.skyrim.simpleim.model.dao.UserAccountDao;
import top.skyrim.simpleim.model.db.DBManager;

/**
 * Created by wangxin on 2018/3/3.
 * 全局数据处理类
 */

public class Model {

    private static Model model = new Model();
    private Context mContext;
    private ExecutorService executors = Executors.newCachedThreadPool();
    private UserAccountDao userAccountDao;
    private DBManager dbManager;

    private Model() {
    }

    /**
     * 获取数据处理类实例
     *
     * @return
     */
    public static Model getInstance() {
        return model;
    }

    /**
     * 初始化
     *
     * @param context 上下文
     */
    public void init(Context context) {
        mContext = context;

        //创建用户账号数据库的操作对象
        userAccountDao = new UserAccountDao(mContext);

        //开启全局监听
        EventListener eventListener = new EventListener(mContext);
    }

    /**
     * 创建全局线程池
     *
     * @return
     */
    public ExecutorService getGlobalThreadPool() {
        return executors;
    }

    /**
     * 获取用户账号数据库操作对象
     */
    public UserAccountDao getUserAccountDao() {
        return userAccountDao;
    }

    /**
     * 登录成功后的操作
     *
     * @param account
     */
    public void loginSuccess(UserInfo account) {
        //校验
        if (account == null) {
            return;
        }

        if (dbManager != null) {
            dbManager.close();
        }

        dbManager = new DBManager(mContext, account.getName());
    }

    public DBManager getDbManager() {
        return dbManager;
    }
}
