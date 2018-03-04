package top.skyrim.simpleim.model.bean;

/**
 * Created by wangxin on 2018/3/3.
 */

public class PickContactInfo {

    private UserInfo user;  //联系人
    private boolean isChecked;  //是否被标记

    public PickContactInfo() {
    }

    public PickContactInfo(UserInfo user, boolean isChecked) {
        this.user = user;
        this.isChecked = isChecked;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public String toString() {
        return "PickContactInfo{" +
                "user=" + user +
                ", isChecked=" + isChecked +
                '}';
    }
}
