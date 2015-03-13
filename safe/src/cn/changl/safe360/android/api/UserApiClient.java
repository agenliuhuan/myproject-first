/**
 * UserApiClient.java
 *
 * Copyright 2014 cokosoft.com. All Rights Reserved.
 */
package cn.changl.safe360.android.api;

import java.util.List;

import mobi.dlys.android.core.utils.StringUtils;
import android.text.TextUtils;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.ActionType;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.Command;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.Location;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.PushInfo;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.Trip;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.UserInfo;

/**
 * 用户Api客户端. User: Wangle<87292008@qq.com> Date: 2014/6/9 0009 Time: 18:07
 */
public class UserApiClient extends ApiClient {

	/**
	 * 游客
	 * 
	 * @param userId
	 *            可选
	 * @param phone
	 *            可选
	 * @param lng
	 * @param lat
	 * @param addrName
	 * @param location
	 * @return
	 */
	public static Safe360Pb guest(int userId, String phone, String lng, String lat, String addrName, String location) {
		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
		builder.setCommand(Command.GUEST);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setPhone(phone);

		Location.Builder lb = Location.newBuilder();
		lb.setLng(lng);
		lb.setLat(lat);
		lb.setName(addrName);
		lb.setAddress(location);
		ub.setLocation(lb);

		builder.setUserInfo(ub);

		return executeNetworkInvokeSimple(builder.build());
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
	public static Safe360Pb regist(String phone, String password, String authCode, String image, String nickname, int gender, String lng, String lat,
			String addrName, String location, String baiduChannelId, String baiduUserId) {

		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
		builder.setCommand(Command.REGIST);

		if (TextUtils.isEmpty(addrName)) {
			addrName = "";
		}

		Location.Builder lb = Location.newBuilder();
		lb.setLng(lng);
		lb.setLat(lat);
		lb.setName(addrName);
		lb.setAddress(location);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setPhone(phone);
		ub.setPassword(password);
		ub.setAuthCode(authCode);
		ub.setLocation(lb);
		ub.setImage(image);
		ub.setNickname(nickname);
		ub.setGender(gender);
		builder.setUserInfo(ub);

		PushInfo.Builder pb = PushInfo.newBuilder();
		pb.setBChannelId(baiduChannelId);
		pb.setBUserId(baiduUserId);
		builder.setPushInfo(pb);

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
	public static Safe360Pb login(String phone, String password, String lng, String lat, String addrName, String location, String baiduChannelId,
			String baiduUserId, String token) {
		if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(password)) {
			return null;
		}
		if (TextUtils.isEmpty(addrName)) {
			addrName = "";
		}
		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
		builder.setCommand(Command.LOGIN);

		Location.Builder lb = Location.newBuilder();
		lb.setLng(lng);
		lb.setLat(lat);
		lb.setName(addrName);
		lb.setAddress(location);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setPhone(phone);
		ub.setPassword(password);
		ub.setLocation(lb);
		if (!TextUtils.isEmpty(token)) {
			ub.setToken(token);
		}
		builder.setUserInfo(ub);

		PushInfo.Builder pb = PushInfo.newBuilder();
		pb.setBChannelId(baiduChannelId);
		pb.setBUserId(baiduUserId);
		builder.setPushInfo(pb);

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
	public static Safe360Pb logout(int userId, String token) {
		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
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
	public static Safe360Pb getAuthCode4Regist(String phone) {
		return getAuthCode(phone, ActionType.AT_REGIST);
	}

	/**
	 * 获取忘记密码验证码
	 * 
	 * @param phone
	 *            手机号码
	 */
	public static Safe360Pb getAuthCode4LostPwd(String phone) {
		return getAuthCode(phone, ActionType.AT_LOST_PWD);
	}

	/**
	 * 获取验证码
	 * 
	 * @param phone
	 *            手机号码
	 * @param actionType
	 *            动作类型
	 */
	private static Safe360Pb getAuthCode(String phone, ActionType actionType) {
		if (StringUtils.isEmpty(phone)) {
			return null;
		}
		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
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
	public static Safe360Pb verifyAuthCode(String phone, String authCode) {
		if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(authCode)) {
			return null;
		}
		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
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
	public static Safe360Pb setNewPwd(String phone, String password, String authCode) {
		if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(password) || StringUtils.isEmpty(authCode)) {
			return null;
		}
		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
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
	public static Safe360Pb modifyPwd(int userId, String token, String oldPassword, String newPassword) {
		if (StringUtils.isEmpty(token) || StringUtils.isEmpty(oldPassword)) {
			return null;
		}
		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
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
	public static Safe360Pb userInfo(int userId, String token) {
		if (userId <= 0) {
			return null;
		}
		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
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
	public static Safe360Pb modifyUser(int userId, String token, int gender, String nickname, String image) {
		if (StringUtils.isEmpty(token)) {
			return null;
		}
		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
		builder.setCommand(Command.MODIFY_USER);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		ub.setNickname(nickname);
		ub.setImage(image);
		ub.setGender(gender);
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
	public static Safe360Pb hideLocation(int userId, String token, boolean hideLocation) {
		if (StringUtils.isEmpty(token)) {
			return null;
		}
		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
		builder.setCommand(Command.MODIFY_HIDE_LOCATION);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		ub.setHideLocation(hideLocation);
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
	 * @param addrName
	 *            建筑名称
	 * @param location
	 *            地址
	 * @param power
	 *            电量
	 * @return
	 */
	public static Safe360Pb modifyLocation(int userId, String token, String lng, String lat, String addrName, String location, int power, int tripid) {
		if (StringUtils.isEmpty(token) || StringUtils.isEmpty(lng) || StringUtils.isEmpty(lat)) {
			return null;
		}
		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
		builder.setCommand(Command.MODIFY_LOCATION);

		Location.Builder lb = Location.newBuilder();
		lb.setLng(lng);
		lb.setLat(lat);
		if (!TextUtils.isEmpty(addrName)) {
			lb.setName(addrName);
		}
		lb.setAddress(location);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		ub.setLocation(lb);
		ub.setPower(power);
		builder.setUserInfo(ub);

		if (tripid != 0) {
			Trip.Builder tb = Trip.newBuilder();
			tb.setTripId(tripid);
			builder.setTrip(tb.build());
		}
		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 检查用户是否已经注册
	 * 
	 * @param phoneList
	 *            手机号码列表
	 */
	public static Safe360Pb checkUserRegist(List<String> phoneList) {
		if (null == phoneList || phoneList.isEmpty()) {
			return null;
		}
		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
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
	 * 获取用户位置
	 * 
	 * @param userId
	 * @return
	 */
	public static Safe360Pb getUserLocation(int userId) {
		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
		builder.setCommand(Command.GET_USER_LOCATION);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		builder.setUserInfo(ub);

		return executeNetworkInvokeSimple(builder.build());
	}

	public static Safe360Pb sendSOS(int userId, String token, String lng, String lat, String addrName, String location) {
		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
		builder.setCommand(Command.SOS);

		Location.Builder lb = Location.newBuilder();
		lb.setLng(lng);
		lb.setLat(lat);
		lb.setName(addrName);
		lb.setAddress(location);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		ub.setLocation(lb);
		builder.setUserInfo(ub);

		return executeNetworkInvokeSimple(builder.build());
	}

}
