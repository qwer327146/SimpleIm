package top.skyrim.simpleim.model.dao;

/**
 * Created by wangxin on 2018/3/3.
 */

public class ContactTable {

    public static final String TAB_NAME = "tab_contact";

    public static final String COL_NAME = "name"; // 用户名称
    public static final String COL_HXID = "hxid";// 环信id
    public static final String COL_NICK = "nick"; // 昵称
    public static final String COL_PHOTO = "photo";// 头像

    public static final String COL_IS_CONTACT = "is_contact"; // 是否是好友

    public static final String CREATE_TAB = "create table "
            + TAB_NAME + "("
            + COL_HXID + " text primary key,"
            + COL_NAME + " text, "
            + COL_NICK + " text, "
            + COL_PHOTO + " text, "
            + COL_IS_CONTACT + " integer);";
}
