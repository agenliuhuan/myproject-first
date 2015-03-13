package mobi.dlys.android.familysafer.biz.vo;

import mobi.dlys.android.core.mvc.BaseObject;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.FriendRequest;
import mobi.dlys.android.familysafer.utils.AES;

import com.j256.ormlite.field.DatabaseField;

/**
 * 好友请求类
 * 
 * @author rocksen
 * 
 */
public class FriendRequestObject extends BaseObject {
	private static final long serialVersionUID = 396229952724316006L;

	@DatabaseField(generatedId = true)
	private int id;

	// 用户Id
	@DatabaseField
	private int userId;

	// 用户头像
	@DatabaseField
	private String image;

	// 用户昵称
	@DatabaseField
	private String nickname;

	// 是否接收的请求
	@DatabaseField
	private boolean isReceive;

	// 请求状态 0.等待 1.已通过 2.拒绝
	@DatabaseField
	private int status;

	// 手机号码
	@DatabaseField
	private String phone;

	// 插入数据库时间
	@DatabaseField
	private long insertTime;

	public static FriendRequestObject createFromPb(FriendRequest friend) {
		FriendRequestObject friendObject = new FriendRequestObject();
		if (friend != null) {
			friendObject.setUserId(friend.getUserInfo().getUserId());
			friendObject.setImage(friend.getUserInfo().getImage());
			friendObject.setNickname(friend.getUserInfo().getNickname());
			friendObject.setReceive(friend.getIsReceive());
			friendObject.setStatus(friend.getStatus());
			friendObject.setPhone(friend.getUserInfo().getPhone());
			friendObject.setInsertTime(System.nanoTime());
		}

		return friendObject;
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
		this.nickname = AES.encrypt(name);
	}

	public String getNickname() {
		return AES.decrypt(this.nickname);

	}

	public void setReceive(boolean receive) {
		this.isReceive = receive;
	}

	public boolean getReceive() {
		return this.isReceive;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return this.status;
	}

	public String getPhone() {
		return AES.decrypt(this.phone);
	}

	public void setPhone(String phone) {
		this.phone = AES.encrypt(phone);
	}

	public long getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(long insertTime) {
		this.insertTime = insertTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
