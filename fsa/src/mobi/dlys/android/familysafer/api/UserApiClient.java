/**
 * UserApiClient.java
 *
 * Copyright 2014 cokosoft.com. All Rights Reserved.
 */
package mobi.dlys.android.familysafer.api;

import java.util.List;

import mobi.dlys.android.core.utils.StringUtils;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.ActionType;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.Command;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.UserInfo;

/**
 * 用户Api客户端. User: Wangle<87292008@qq.com> Date: 2014/6/9 0009 Time: 18:07
 */
public class UserApiClient extends ApiClient {

	/**
	 * 注册普通用户
	 * 
	 * @param phone
	 *            手机号
	 * @param password
	 *            密码
	 * @param authCode
	 *            验证码
	 * @param image
	 *            头像
	 * @param nickname
	 *            昵称
	 * @param lng
	 *            经度
	 * @param lat
	 *            纬度
	 */
	public static FamilySaferPb registCommon(String phone, String password, String authCode, String image, String nickname, String lng, String lat,
			String location) {
		return regist(ActionType.AT_REGIST, null, phone, password, authCode, image, nickname, lng, lat, location);
	}

	/**
	 * 注册
	 * 
	 * @param phone
	 *            手机号
	 * @param password
	 *            密码
	 * @param authCode
	 *            验证码
	 * @param image
	 *            头像
	 * @param nickname
	 *            昵称
	 * @param lng
	 *            经度
	 * @param lat
	 *            纬度
	 */
	private static FamilySaferPb regist(ActionType actionType, String policeNo, String phone, String password, String authCode, String image, String nickname,
			String lng, String lat, String location) {
		if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(password) || StringUtils.isEmpty(authCode)) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(Command.REGIST);

		builder.setActionType(actionType);
		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setPhone(phone);
		ub.setPassword(password);
		ub.setAuthCode(authCode);
		ub.setLng(lng);
		ub.setLat(lat);
		ub.setLocation(location);
		ub.setImage(image);
		ub.setNickname(nickname);
		builder.setUserInfo(ub);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 登录
	 * 
	 * @param phone
	 *            手机号
	 * @param password
	 *            密码
	 * @param lng
	 *            经度
	 * @param lat
	 *            纬度
	 */
	public static FamilySaferPb login(String phone, String password, String lng, String lat, String location) {
		if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(password)) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(Command.LOGIN);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setPhone(phone);
		ub.setPassword(password);
		ub.setLng(lng);
		ub.setLat(lat);
		ub.setLocation(location);
		builder.setUserInfo(ub);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 退出登录
	 * 
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户session标识
	 */
	public static FamilySaferPb logout(int userId, String token) {
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(Command.LOGOUT);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		builder.setUserInfo(ub);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 获取注册验证码
	 * 
	 * @param phone
	 *            手机号码
	 */
	public static FamilySaferPb getAuthCode4Regist(String phone) {
		return getAuthCode(phone, ActionType.AT_REGIST);
	}

	/**
	 * 获取忘记密码验证码
	 * 
	 * @param phone
	 *            手机号码
	 */
	public static FamilySaferPb getAuthCode4LostPwd(String phone) {
		return getAuthCode(phone, ActionType.AT_LOST_PWD);
	}

	/**
	 * 获取修改手机验证码
	 * 
	 * @param phone
	 *            手机号码
	 */
	public static FamilySaferPb getAuthCode4ModifyPhone(String phone) {
		return getAuthCode(phone, ActionType.AT_MODIFY_PHONE);
	}

	/**
	 * 获取验证码
	 * 
	 * @param phone
	 *            手机号码
	 * @param actionType
	 *            动作类型
	 */
	private static FamilySaferPb getAuthCode(String phone, ActionType actionType) {
		if (StringUtils.isEmpty(phone)) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(Command.GET_AUTH_CODE);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setPhone(phone);
		builder.setUserInfo(ub);

		builder.setActionType(actionType);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 核对验证码
	 * 
	 * @param phone
	 *            手机号码
	 * @param authCode
	 *            验证码
	 */
	public static FamilySaferPb verifyAuthCode(String phone, String authCode) {
		if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(authCode)) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(Command.VERIFY_AUTH_CODE);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setPhone(phone);
		ub.setAuthCode(authCode);
		builder.setUserInfo(ub);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 设置新密码
	 * 
	 * @param phone
	 *            手机号码
	 * @param password
	 *            密码
	 * @param authCode
	 *            验证码
	 */
	public static FamilySaferPb setNewPwd(String phone, String password, String authCode) {
		if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(password) || StringUtils.isEmpty(authCode)) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(Command.SET_NEW_PWD);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setPhone(phone);
		ub.setPassword(password);
		ub.setAuthCode(authCode);
		builder.setUserInfo(ub);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 修改密码
	 * 
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户session标识
	 * @param oldPassword
	 *            密码
	 * @param newPassword
	 *            重复密码
	 */
	public static FamilySaferPb modifyPwd(int userId, String token, String oldPassword, String newPassword) {
		if (StringUtils.isEmpty(token) || StringUtils.isEmpty(oldPassword)) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(Command.MODIFY_PWD);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		ub.setPassword(oldPassword);
		ub.setPassword2(newPassword);
		builder.setUserInfo(ub);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 获取用户信息
	 * 
	 * @param userId
	 *            用户ID
	 */
	public static FamilySaferPb userInfo(int userId, String token) {
		if (userId <= 0) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(Command.USER_INFO);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		builder.setUserInfo(ub);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 修改用户信息
	 * 
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户session标识
	 * @param nickname
	 *            昵称
	 * @param image
	 *            头像
	 */
	public static FamilySaferPb modifyUser(int userId, String token, String nickname, String image) {
		if (StringUtils.isEmpty(token)) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(Command.MODIFY_USER);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		ub.setNickname(nickname);
		ub.setImage(image);
		builder.setUserInfo(ub);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 修改是否隐藏自己的地理位置
	 * 
	 * @param userId
	 * @param token
	 * @param hide
	 * @return
	 */
	public static FamilySaferPb hideLocation(int userId, String token, boolean hide) {
		if (StringUtils.isEmpty(token)) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(Command.MODIFY_HIDE_LOCATION);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		ub.setHideLocation(hide);
		builder.setUserInfo(ub);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 修改手机号码 - 验证密码
	 * 
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户session标识
	 * @param password
	 *            密码
	 */
	public static FamilySaferPb verifyPwd4ModifyPhone(int userId, String token, String password) {
		if (StringUtils.isEmpty(token) || StringUtils.isEmpty(password)) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(Command.MODIFY_PHONE_VERIFY_PWD);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		ub.setPassword(password);
		builder.setUserInfo(ub);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 修改手机号码
	 * 
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户session标识
	 * @param password
	 *            密码
	 * @param phone
	 *            手机号码
	 * @param authCode
	 *            验证码
	 */
	public static FamilySaferPb modifyPhone(int userId, String token, String password, String phone, String authCode) {
		if (StringUtils.isEmpty(token) || StringUtils.isEmpty(password) || StringUtils.isEmpty(phone) || StringUtils.isEmpty(authCode)) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(Command.MODIFY_PHONE);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		ub.setPassword(password);
		ub.setPhone(phone);
		ub.setAuthCode(authCode);
		builder.setUserInfo(ub);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 修改位置
	 * 
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户session标识
	 * @param lng
	 *            经度
	 * @param lat
	 *            纬度
	 */
	public static FamilySaferPb modifyLocation(int userId, String token, String lng, String lat, String location) {
		if (StringUtils.isEmpty(token) || StringUtils.isEmpty(lng) || StringUtils.isEmpty(lat)) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(Command.MODIFY_LOCATION);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		ub.setLng(lng);
		ub.setLat(lat);
		ub.setLocation(location);
		builder.setUserInfo(ub);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 检查用户是否已经注册
	 * 
	 * @param phoneList
	 *            手机号码列表
	 */
	public static FamilySaferPb checkUserRegist(List<String> phoneList) {
		if (null == phoneList || phoneList.isEmpty()) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(Command.CHECK_USER_REGIST);

		UserInfo.Builder ub;
		for (String phone : phoneList) {
			ub = UserInfo.newBuilder();
			ub.setPhone(phone);
			builder.addUserInfos(ub);
		}

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 检查用户关系
	 * 
	 * @param userId
	 * @param token
	 * @param phoneList
	 *            手机号码列表
	 * @return
	 */
	public static FamilySaferPb checkUserRelation(int userId, String token, List<String> phoneList) {
		if (null == phoneList || phoneList.isEmpty()) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(Command.CHECK_USER_RELATION);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		builder.setUserInfo(ub);
		for (String phone : phoneList) {
			ub = UserInfo.newBuilder();
			ub.setPhone(phone);
			builder.addUserInfos(ub);
		}

		return executeNetworkInvokeSimple(builder.build());
	}

}
