package mobi.dlys.android.familysafer.biz.bo;

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

	// 检查用户关系
	public static final int REQ_CHECK_USER_RELATION = MSG_BASE + 39;
	public static final int RESP_CHECK_USER_RELATION = MSG_BASE + 40;

	/*********************************************************
	 * CheckIn相关消息
	 ********************************************************/
	// 给所有人发通知
	public static final int REQ_CHECKIN_TO_ALL = MSG_BASE + 101;
	public static final int RESP_CHECKIN_TO_ALL = MSG_BASE + 102;

	// 给指定好友发通知
	public static final int REQ_CHECKIN_TO_SAME_ONE = MSG_BASE + 103;
	public static final int RESP_CHECKIN_TO_SAME_ONE = MSG_BASE + 104;

	// 获取通知列表
	public static final int REQ_GET_CHECKIN_MSG_LIST = MSG_BASE + 105;
	public static final int RESP_GET_CHECKIN_MSG_LIST = MSG_BASE + 106;

	// 获取通知数
	// public static final int REQ_GET_CHECKIN_MSG_COUNT = MSG_BASE + 107;
	// public static final int RESP_GET_CHECKIN_MSG_COUNT = MSG_BASE + 108;

	// 签到确认
	public static final int REQ_CHECK_IN_CONFIRM = MSG_BASE + 109;
	public static final int RESP_CHECK_IN_CONFIRM = MSG_BASE + 110;

	// 获取新的通知
	public static final int REQ_GET_NEW_EVENT_LIST = MSG_BASE + 111;
	public static final int RESP_GET_NEW_EVENT_LIST = MSG_BASE + 112;

	// 获取本地缓存通知列表
	public static final int REQ_GET_CACHE_CHECKIN_MSG_LIST = MSG_BASE + 113;
	public static final int RESP_GET_CACHE_CHECKIN_MSG_LIST = MSG_BASE + 114;
	/*********************************************************
	 * 线索相关消息
	 ********************************************************/
	// 拍线索
	public static final int REQ_PUSH_CLUE = MSG_BASE + 201;
	public static final int RESP_PUSH_CLUE = MSG_BASE + 202;

	// 我的线索列表
	public static final int REQ_GET_CLUE_LIST = MSG_BASE + 203;
	public static final int RESP_GET_CLUE_LIST = MSG_BASE + 204;

	// 获取本地缓存线索列表
	public static final int REQ_GET_CACHE_CLUE_LIST = MSG_BASE + 205;
	public static final int RESP_GET_CACHE_CLUE_LIST = MSG_BASE + 206;

	// 获取公共线索列表
	public static final int REQ_GET_PUBLIC_CLUE_LIST = MSG_BASE + 207;
	public static final int RESP_GET_PUBLIC_CLUE_LIST = MSG_BASE + 208;

	// 获取本地缓存的公共线索列表
	public static final int REQ_GET_CACHE_PUBLIC_CLUE_LIST = MSG_BASE + 209;
	public static final int RESP_GET_CACHE_PUBLIC_CLUE_LIST = MSG_BASE + 210;

	/*********************************************************
	 * 好友相关消息
	 ********************************************************/
	// 添加好友
	public static final int REQ_ADD_FRIEND = MSG_BASE + 301;
	public static final int RESP_ADD_FRIEND = MSG_BASE + 302;

	// 获取好友请求列表
	public static final int REQ_GET_FRIEND_REQUEST_LIST = MSG_BASE + 303;
	public static final int RESP_GET_FRIEND_REQUEST_LIST = MSG_BASE + 304;

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

	// 设置备注名
	public static final int REQ_SET_REMARK_NAME = MSG_BASE + 313;
	public static final int RESP_SET_REMARK_NAME = MSG_BASE + 314;

	// 显示我的位置
	public static final int REQ_SHOW_MY_LOCATION = MSG_BASE + 315;
	public static final int RESP_SHOW_MY_LOCATION = MSG_BASE + 316;

	// 获取新的提醒数
	public static final int REQ_GET_NEW_MSG_NUM = MSG_BASE + 317;
	public static final int RESP_GET_NEW_MSG_NUM = MSG_BASE + 318;

	// 获取本地缓存好友请求列表
	public static final int REQ_GET_CACHE_FRIEND_REQUEST_LIST = MSG_BASE + 319;
	public static final int RESP_GET_CACHE_FRIEND_REQUEST_LIST = MSG_BASE + 320;

	// 获取本地缓存好友列表
	public static final int REQ_GET_CACHE_FRIEND_LIST = MSG_BASE + 321;
	public static final int RESP_GET_CACHE_FRIEND_LIST = MSG_BASE + 322;
	/*********************************************************
	 * 定位相关消息
	 ********************************************************/
	// 获取用户位置
	public static final int REQ_GET_USER_LOCATION = MSG_BASE + 401;
	public static final int RESP_GET_USER_LOCATION = MSG_BASE + 402;

	// 获取Npc位置
	public static final int REQ_GET_NPC_LOCATION = MSG_BASE + 403;
	public static final int RESP_GET_NPC_LOCATION = MSG_BASE + 404;

	/*********************************************************
	 * 系统相关消息
	 ********************************************************/
	// 检测版本
	public static final int REQ_CHECK_VERSION = MSG_BASE + 501;
	public static final int RESP_CHECK_VERSION = MSG_BASE + 502;

	// 上传图片
	public static final int REQ_UPLOAD_IMAGE = MSG_BASE + 503;
	public static final int RESP_UPLOAD_IMAGE = MSG_BASE + 504;

	// 上传语音
	public static final int REQ_UPLOAD_RECORD = MSG_BASE + 505;
	public static final int RESP_UPLOAD_RECORD = MSG_BASE + 506;

	// 刷新token
	public static final int REQ_REFRESH_TOKEN = MSG_BASE + 507;
	public static final int RESP_REFRESH_TOKEN = MSG_BASE + 508;

	// 获取服务器动态Ip
	public static final int REQ_GET_SERVER_DYNAMIC_IP = MSG_BASE + 509;
	public static final int RESP_GET_SERVER_DYNAMIC_IP = MSG_BASE + 510;

	/*********************************************************
	 * 求救相关消息
	 ********************************************************/
	// 语音求救
	public static final int REQ_VOICE_SOS = MSG_BASE + 601;
	public static final int RESP_VOICE_SOS = MSG_BASE + 602;

	// 语音求救确认
	public static final int REQ_VOICE_SOS_CONFIRM = MSG_BASE + 603;
	public static final int RESP_VOICE_SOS_CONFIRM = MSG_BASE + 604;

	// 语音求救列表
	public static final int REQ_GET_VOICE_SOS_LIST = MSG_BASE + 605;
	public static final int RESP_GET_VOICE_SOS_LIST = MSG_BASE + 606;

	// 语音求救缓存列表
	public static final int REQ_GET_CACHE_VOICE_SOS_LIST = MSG_BASE + 607;
	public static final int RESP_GET_CACHE_VOICE_SOS_LIST = MSG_BASE + 608;

	/*********************************************************
	 * IM相关消息
	 ********************************************************/
	// 发送语音消息
	public static final int REQ_SEND_MSG = MSG_BASE + 701;
	public static final int RESP_SEND_MSG = MSG_BASE + 702;

	// 语音消息主题列表
	public static final int REQ_GET_MSG_TOPIC_LIST = MSG_BASE + 703;
	public static final int RESP_GET_MSG_TOPIC_LIST = MSG_BASE + 704;

	// 获取本地缓存语音消息主题列表
	public static final int REQ_GET_CACHE_MSG_TOPIC_LIST = MSG_BASE + 705;
	public static final int RESP_GET_CACHE_MSG_TOPIC_LIST = MSG_BASE + 706;

	// 语音消息列表
	public static final int REQ_GET_MSG_LIST = MSG_BASE + 707;
	public static final int RESP_GET_MSG_LIST = MSG_BASE + 708;

	// 获取本地缓存语音消息列表
	public static final int REQ_GET_CACHE_MSG_LIST = MSG_BASE + 709;
	public static final int RESP_GET_CACHE_MSG_LIST = MSG_BASE + 710;

	// 有新消息时，系统状态栏显示通知
	public static final int REQ_SHOW_NEW_MSG_NOTIFICATION = MSG_BASE + 711;
	public static final int RESP_SHOW_NEW_MSG_NOTIFICATION = MSG_BASE + 712;
}
