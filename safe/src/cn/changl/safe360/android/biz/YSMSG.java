package cn.changl.safe360.android.biz;

public class YSMSG {
	private static final int MSG_BASE = 100;

	/*********************************************************
	 * User相关消息
	 ********************************************************/
	// 注册验证码
	public static final int REQ_GET_REG_AUTH_CODE = MSG_BASE + 1;
	public static final int RESP_GET_REG_AUTH_CODE = MSG_BASE + 2;

	// 注册
	public static final int REQ_REG_USER_ACCOUNT = MSG_BASE + 3;
	public static final int RESP_REG_USER_ACCOUNT = MSG_BASE + 4;

	// 登录
	public static final int REQ_LOGIN = MSG_BASE + 5;
	public static final int RESP_LOGIN = MSG_BASE + 6;

	// 退出登录
	public static final int REQ_LOGOUT = MSG_BASE + 7;
	public static final int RESP_LOGOUT = MSG_BASE + 8;

	// 忘记密码验证码
	public static final int REQ_GET_LOST_PASSWORD_AUTH_CODE = MSG_BASE + 9;
	public static final int RESP_GET_LOST_PASSWORD_AUTH_CODE = MSG_BASE + 10;

	// 修改手机验证码
	public static final int REQ_GET_MODIFY_PHONE_AUTH_CODE = MSG_BASE + 11;
	public static final int RESP_GET_MODIFY_PHONE_AUTH_CODE = MSG_BASE + 12;

	// 校验验证码
	public static final int REQ_VERIFY_AUTH_CODE = MSG_BASE + 13;
	public static final int RESP_VERIFY_AUTH_CODE = MSG_BASE + 14;

	// 设置新密码
	public static final int REQ_SET_NEW_PASSWORD = MSG_BASE + 15;
	public static final int RESP_SET_NEW_PASSWORD = MSG_BASE + 16;

	// 修改密码
	public static final int REQ_MODIFY_USER_PASSWORD = MSG_BASE + 17;
	public static final int RESP_MODIFY_USER_PASSWORD = MSG_BASE + 18;

	// 获取用户信息
	public static final int REQ_GET_USER_INFO = MSG_BASE + 19;
	public static final int RESP_GET_USER_INFO = MSG_BASE + 20;

	// 修改用户信息
	public static final int REQ_MODIFY_USER_INFO = MSG_BASE + 21;
	public static final int RESP_MODIFY_USER_INFO = MSG_BASE + 22;

	// 修改手机号码 - 验证密码
	public static final int REQ_VERIFY_PWD_FOR_MODIFY_PHONE = MSG_BASE + 23;
	public static final int RESP_VERIFY_PWD_FOR_MODIFY_PHONE = MSG_BASE + 24;

	// 修改手机号码
	public static final int REQ_MODIFY_PHONE = MSG_BASE + 25;
	public static final int RESP_MODIFY_PHONE = MSG_BASE + 26;

	// 修改位置
	public static final int REQ_MODIFY_LOCATION = MSG_BASE + 27;
	public static final int RESP_MODIFY_LOCATION = MSG_BASE + 28;

	// 检测用户是否已经注册
	public static final int REQ_CHECK_USER_REGISTER = MSG_BASE + 29;
	public static final int RESP_CHECK_USER_REGISTER = MSG_BASE + 30;

	// 读取手机联系人列表
	public static final int REQ_READ_PHONE_CONTACTS_LIST = MSG_BASE + 31;
	public static final int RESP_READ_PHONE_CONTACTS_LIST = MSG_BASE + 32;

	// 匹配手机联系人（包括读取联系人和上传联系人）
	public static final int REQ_MATCH_PHONE_CONTACTS_LIST = MSG_BASE + 33;
	public static final int RESP_MATCH_PHONE_CONTACTS_LIST = MSG_BASE + 34;

	// 是否隐藏地理位置
	public static final int REQ_HIDE_LOCATION = MSG_BASE + 35;
	public static final int RESP_HIDE_LOCATION = MSG_BASE + 36;

	// 自动登录
	public static final int REQ_AUTO_LOGIN = MSG_BASE + 37;
	public static final int RESP_AUTO_LOGIN = MSG_BASE + 38;

	public static final int RESP_MATCH_PHONE_PROGRESS = MSG_BASE + 39;

	/*********************************************************
	 * 好友相关消息
	 ********************************************************/
	// 添加好友
	public static final int REQ_ADD_FRIEND = MSG_BASE + 301;
	public static final int RESP_ADD_FRIEND = MSG_BASE + 302;

	// 同意好友请求
	public static final int REQ_AGREE_FRIEND_REQUEST = MSG_BASE + 305;
	public static final int RESP_AGREE_FRIEND_REQUEST = MSG_BASE + 306;

	// 拒绝好友请求
	public static final int REQ_REFUSE_FRIEND_REQUEST = MSG_BASE + 307;
	public static final int RESP_REFUSE_FRIEND_REQUEST = MSG_BASE + 308;

	// 获取好友列表
	public static final int REQ_GET_FRIEND_LIST = MSG_BASE + 309;
	public static final int RESP_GET_FRIEND_LIST = MSG_BASE + 310;

	// 删除好友
	public static final int REQ_DEL_FRIEND = MSG_BASE + 311;
	public static final int RESP_DEL_FRIEND = MSG_BASE + 312;

	/*********************************************************
	 * 定位相关消息
	 ********************************************************/
	// 获取用户位置
	public static final int REQ_GET_USER_LOCATION = MSG_BASE + 401;
	public static final int RESP_GET_USER_LOCATION = MSG_BASE + 402;

	/*********************************************************
	 * 系统相关消息
	 ********************************************************/
	// 检测版本
	public static final int REQ_CHECK_VERSION = MSG_BASE + 501;
	public static final int RESP_CHECK_VERSION = MSG_BASE + 502;

	// 上传图片
	public static final int REQ_UPLOAD_IMAGE = MSG_BASE + 503;
	public static final int RESP_UPLOAD_IMAGE = MSG_BASE + 504;

	// 刷新token
	public static final int REQ_REFRESH_TOKEN = MSG_BASE + 507;
	public static final int RESP_REFRESH_TOKEN = MSG_BASE + 508;

	// 获取服务器动态Ip
	public static final int REQ_GET_SERVER_DYNAMIC_IP = MSG_BASE + 509;
	public static final int RESP_GET_SERVER_DYNAMIC_IP = MSG_BASE + 510;

	/*********************************************************
	 * 圈子相关消息
	 ********************************************************/
	// 获取圈子信息
	public static final int REQ_GET_GROUP_INFO = MSG_BASE + 601;
	public static final int RESP_GET_GROUP_INFO = MSG_BASE + 602;

	// 邀请好友
	public static final int REQ_INVITE_FRIEND = MSG_BASE + 603;
	public static final int RESP_INVITE_FRIEND = MSG_BASE + 604;

	// 删除好友
	public static final int REQ_REMOVE_FRIEND = MSG_BASE + 605;
	public static final int RESP_REMOVE_FRIEND = MSG_BASE + 606;

	// 加入圈子
	public static final int REQ_JOIN_GROUP = MSG_BASE + 607;
	public static final int RESP_JOIN_GROUP = MSG_BASE + 608;

	// 加入圈子
	public static final int REQ_EXIT_GROUP = MSG_BASE + 609;
	public static final int RESP_EXIT_GROUP = MSG_BASE + 610;

	// 邀请临时好友
	public static final int REQ_INVITE_TEMP_FRIEND = MSG_BASE + 611;
	public static final int RESP_INVITE_TEMP_FRIEND = MSG_BASE + 612;

	// 获取临时好友位置
	public static final int REQ_GET_TEMP_FRIEND_LOCATION = MSG_BASE + 613;
	public static final int RESP_GET_TEMP_FRIEND_LOCATION = MSG_BASE + 614;
	
	// 获取空闲好友
	public static final int REQ_GET_IDLE_FRIEND = MSG_BASE + 615;
	public static final int RESP_GET_IDLE_FRIEND = MSG_BASE + 616;

	/*********************************************************
	 * 护航相关消息
	 ********************************************************/
	// 获取护航信息
	public static final int REQ_GET_TRIP_INFO = MSG_BASE + 701;
	public static final int RESP_GET_TRIP_INFO = MSG_BASE + 702;

	// 开始导航
	public static final int REQ_START_TRIP = MSG_BASE + 703;
	public static final int RESP_START_TRIP = MSG_BASE + 704;

	// 结束护航
	public static final int REQ_FINISH_TRIP = MSG_BASE + 705;
	public static final int RESP_FINISH_TRIP = MSG_BASE + 706;

	// 退出护航
	public static final int REQ_EXIT_TRIP = MSG_BASE + 707;
	public static final int RESP_EXIT_TRIP = MSG_BASE + 708;

	// 护航
	public static final int REQ_REFRESH_TRIP_FROM_MAIN = MSG_BASE + 709;
	public static final int REQ_FINISH_TRIP_FROM_MAIN = MSG_BASE + 710;

	// 修改护航目的地
	public static final int REQ_MODIFY_TRIP = MSG_BASE + 711;
	public static final int RESP_MODIFY_TRIP = MSG_BASE + 712;

	/*********************************************************
	 * 其他
	 ********************************************************/
	// 推送消息
	public static final int REQ_PUSH_MSG = MSG_BASE + 801;
	public static final int RESP_PUSH_MSG = MSG_BASE + 802;

	// 求救
	public static final int REQ_SEND_SOS = MSG_BASE + 803;
	public static final int RESP_SEND_SOS = MSG_BASE + 804;
}
