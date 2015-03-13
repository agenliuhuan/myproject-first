package cn.changl.safe360.android.biz.vo;

import mobi.dlys.android.core.mvc.BaseObject;

public class HelperObject extends BaseObject {
    private static final long serialVersionUID = -1992448298265280356L;
    String title;
    String content;
    boolean isShow;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean isShow) {
        this.isShow = isShow;
    }
}
