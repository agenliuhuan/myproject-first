package mobi.dlys.android.familysafer.biz.vo;

import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.core.mvc.BaseObject;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduLoc;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.Clue;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.ClueImage;

import com.j256.ormlite.field.DatabaseField;

/**
 * 公共线索类
 * 
 * @author rocksen
 * 
 */
public class PublicClueObject extends BaseObject {
	private static final long serialVersionUID = 2873759740589964676L;

	// 用户Id
	@DatabaseField
	private int userId;

	// 头像
	@DatabaseField
	private String image;

	// 昵称
	@DatabaseField
	private String nickname;

	// 线索ID
	@DatabaseField(id = true)
	private int clueId;

	// 图片列表
	private ArrayList<ClueImageObject> imageList;

	// 文字信息
	@DatabaseField
	private String msg;

	// 经度
	@DatabaseField
	private String lng;

	// 纬度
	@DatabaseField
	private String lat;

	// 位置描述
	@DatabaseField
	private String location;

	// 线索时间
	@DatabaseField
	private String createTime;

	// 手机型号
	@DatabaseField
	private String model;

	// 插入数据库时间
	@DatabaseField
	private long insertTime;

	public static PublicClueObject createFromPb(Clue clue) {
		PublicClueObject clueObject = new PublicClueObject();
		if (clue != null) {
			clueObject.setClueId(clue.getClueId());
			if (clue.getImagesList() != null && clue.getImagesCount() > 0) {
				ArrayList<ClueImageObject> imageList = new ArrayList<ClueImageObject>();
				for (ClueImage clueImage : clue.getImagesList()) {
					ClueImageObject imageObject = new ClueImageObject();
					imageObject.setClueId(clue.getClueId());
					imageObject.setImage(clueImage.getImage());
					imageObject.setWidth(clueImage.getWidth());
					imageObject.setHeight(clueImage.getHeight());
					imageList.add(imageObject);
				}
				clueObject.setImageList(imageList);
			}
			clueObject.setMessage(clue.getMsg());
			clueObject.setLng(clue.getLng());
			clueObject.setLat(clue.getLat());
			clueObject.setLocation(clue.getLocation());
			clueObject.setCreateTime(clue.getCreateTime());
			clueObject.setModel(clue.getPhoneModel());
			if (clue.hasUserInfo() && clue.getUserInfo() != null) {
				clueObject.setUserId(clue.getUserInfo().getUserId());
				clueObject.setImage(clue.getUserInfo().getImage());
				clueObject.setNickname(clue.getUserInfo().getNickname());
			}
			clueObject.setInsertTime(System.nanoTime());
		}

		return clueObject;
	}

	public PublicClueObject() {
		imageList = new ArrayList<ClueImageObject>();
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getUserId() {
		return userId;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getImage() {
		return this.image;
	}

	public void setNickname(String name) {
		this.nickname = name;
	}

	public String getNickname() {
		return this.nickname;
	}

	public void setClueId(int clueId) {
		this.clueId = clueId;
	}

	public int getClueId() {
		return clueId;
	}

	public void setImageList(List<ClueImageObject> imageList) {
		this.imageList.clear();
		if (imageList != null) {
			this.imageList.addAll(imageList);
		}
	}

	public ArrayList<ClueImageObject> getImageList() {
		return imageList;
	}

	public ArrayList<String> getImageUrlList() {
		ArrayList<String> imageUrlList = new ArrayList<String>();
		for (ClueImageObject clueImage : imageList) {
			imageUrlList.add(clueImage.getImage());
		}
		return imageUrlList;
	}

	public void setMessage(String msg) {
		this.msg = msg;
	}

	public String getMessage() {
		return this.msg;
	}

	public String getLng() {
		return lng;
	}
	
	public double getLng2() {		
		double value = 0.0;
		try {
			value = Double.valueOf(lng);
		} catch (Exception e) {

		}
		return value;
	}

	public void setLng(String lng) {
		if (BaiduLoc.isLngValid(lng)) {
			this.lng = lng;
		}
	}

	public String getLat() {
		return lat;
	}
	
	public double getLat2() {		
		double value = 0.0;
		try {
			value = Double.valueOf(lat);
		} catch (Exception e) {

		}
		return value;
	}

	public void setLat(String lat) {
		if (BaiduLoc.isLatValid(lat)) {
			this.lat = lat;
		}
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocation() {
		if (null == location) {
			return "";
		}
		return this.location;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCreateTime() {
		return this.createTime;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getModel() {
		return model;
	}

	/*
	 * 判断经纬度是否有效
	 */
	public boolean isLocationValid() {
		return BaiduLoc.isLocationValid(getLng(), getLat());
	}

	public long getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(long insertTime) {
		this.insertTime = insertTime;
	}
}
