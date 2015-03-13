package mobi.dlys.android.familysafer.biz.vo;

import mobi.dlys.android.core.mvc.BaseObject;

import com.j256.ormlite.field.DatabaseField;

public class ClueImageObject extends BaseObject {
    private static final long serialVersionUID = -5522810610054256859L;

    // 线索ID
    @DatabaseField
    private int clueId;

    // 图像地址
    @DatabaseField(id = true)
    private String imageUrl;

    // 图像宽度
    @DatabaseField
    private int width;

    // 图像高度
    @DatabaseField
    private int height;

    // 插入数据库时间
    @DatabaseField
    private long insertTime;

    public int getClueId() {
        return clueId;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setClueId(int clueId) {
        this.clueId = clueId;
    }

    public String getImage() {
        return imageUrl;
    }

    public void setImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(long insertTime) {
        this.insertTime = insertTime;
    }
}
