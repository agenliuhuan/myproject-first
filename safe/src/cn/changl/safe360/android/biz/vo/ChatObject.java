package cn.changl.safe360.android.biz.vo;

import mobi.dlys.android.core.mvc.BaseObject;

public class ChatObject extends BaseObject {
    private static final long serialVersionUID = -1992482498265280356L;

    public static final int MESSAGE_FROM_VOICE = 1;
    public static final int MESSAGE_TO_VOICE = 2;
    public static final int MESSAGE_TO_TEXT = 3;
    public static final int MESSAGE_FROM_TEXT = 4;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
