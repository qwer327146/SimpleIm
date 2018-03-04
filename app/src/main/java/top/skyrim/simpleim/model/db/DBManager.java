package top.skyrim.simpleim.model.db;

import android.content.Context;

import top.skyrim.simpleim.model.dao.ContactTableDao;
import top.skyrim.simpleim.model.dao.InviteTableDao;

/**
 * Created by wangxin on 2018/3/3.
 * 联系人数据库和邀请信息数据库的管理类
 */

public class DBManager {

    private final DBHelper dbHelper;
    private final ContactTableDao contactTableDao;
    private final InviteTableDao inviteTableDao;

    public DBManager(Context context, String name) {
        //创建数据库
        dbHelper = new DBHelper(context, name);

        //创建两个数据表的操作类
        contactTableDao = new ContactTableDao(dbHelper);
        inviteTableDao = new InviteTableDao(dbHelper);
    }

    //获取联系人表的操作对象
    public ContactTableDao getContactTableDao() {
        return contactTableDao;
    }

    //获取邀请信息表的操作对象
    public InviteTableDao getInviteTableDao() {
        return inviteTableDao;
    }

    //关闭数据库
    public void close() {
        dbHelper.close();
    }
}
