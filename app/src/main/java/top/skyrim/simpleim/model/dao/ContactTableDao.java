package top.skyrim.simpleim.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import top.skyrim.simpleim.model.bean.UserInfo;
import top.skyrim.simpleim.model.db.DBHelper;

/**
 * Created by wangxin on 2018/3/3.
 * 联系人数据库的管理类
 */

public class ContactTableDao {

    private DBHelper mHelper;

    public ContactTableDao(DBHelper helper) {
        mHelper = helper;
    }

    // 获取所有联系人
    public List<UserInfo> getContacts() {
        //获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //执行查询
        String sql = String.format("select * from %s", ContactTable.TAB_NAME);
        Cursor cursor = db.rawQuery(sql, null);

        List<UserInfo> contacts = new ArrayList<>();
        while (cursor.moveToNext()) {
            UserInfo contact = new UserInfo();

            contact.setHxid(cursor.getString(cursor.getColumnIndex(ContactTable.COL_HXID)));
            contact.setName(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NAME)));
            contact.setNick(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NICK)));
            contact.setPhoto(cursor.getString(cursor.getColumnIndex(ContactTable.COL_PHOTO)));

            contacts.add(contact);
        }

        //关闭资源
        cursor.close();

        //返回结果
        return contacts;
    }

    // 通过环信id获取用户联系人信息
    public List<UserInfo> getContactsByHx(List<String> hxIds) {
        //获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //校验
        if (hxIds == null || hxIds.size() <= 0) {
            return null;
        }

        //执行查询
        List<UserInfo> contacts = new ArrayList<>();

        for (String hxid : hxIds) {
            //遍历查询
            String sql = String.format("select * from %s where %s = ?", ContactTable.TAB_NAME, ContactTable.COL_HXID);
            Cursor cursor = db.rawQuery(sql, new String[]{hxid});

            if (cursor.moveToNext()) {
                UserInfo contact = new UserInfo();

                contact.setHxid(cursor.getString(cursor.getColumnIndex(ContactTable.COL_HXID)));
                contact.setName(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NAME)));
                contact.setNick(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NICK)));
                contact.setPhoto(cursor.getString(cursor.getColumnIndex(ContactTable.COL_PHOTO)));

                contacts.add(contact);
            }

            //关闭资源
            cursor.close();
        }

        //返回数据
        return contacts;
    }

    // 通过环信id获取联系人单个信息
    public UserInfo getContactByHx(String hxId) {
        //校验
        if (hxId == null) {
            return null;
        }
        //获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //执行查询
        UserInfo contact = new UserInfo();

        String sql = String.format("select * from %s where %s = ?", ContactTable.TAB_NAME, ContactTable.COL_HXID);
        Cursor cursor = db.rawQuery(sql, new String[]{hxId});

        if (cursor.moveToNext()) {
            contact.setHxid(cursor.getString(cursor.getColumnIndex(ContactTable.COL_HXID)));
            contact.setName(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NAME)));
            contact.setNick(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NICK)));
            contact.setPhoto(cursor.getString(cursor.getColumnIndex(ContactTable.COL_PHOTO)));
        }

        //关闭资源
        cursor.close();

        //返回数据
        return contact;
    }

    // 保存单个联系人
    public void saveContact(UserInfo user, boolean isMyContact) {
        //获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //执行保存操作
        ContentValues values = new ContentValues();

        values.put(ContactTable.COL_HXID, user.getHxid());
        values.put(ContactTable.COL_NAME, user.getName());
        values.put(ContactTable.COL_NICK, user.getNick());
        values.put(ContactTable.COL_PHOTO, user.getPhoto());
        values.put(ContactTable.COL_IS_CONTACT, isMyContact ? 1 : 0);

        db.replace(ContactTable.TAB_NAME, null, values);
    }


    // 保存联系人信息
    public void saveContacts(List<UserInfo> contacts, boolean isMyContact) {
        //校验
        if (contacts == null || contacts.size() <= 0) {
            return;
        }

        //遍历执行保存操作
        for (UserInfo contact : contacts) {
            saveContact(contact, isMyContact);
        }

    }

    // 删除联系人信息
    public void deleteContactByHxId(String hxId) {
        //校验
        if (hxId == null) {
            return;
        }

        //获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //执行删除操作
        db.delete(ContactTable.TAB_NAME, String.format("%s = ?", ContactTable.COL_HXID), new String[]{hxId});
    }
}
