/**
 * CheckInApiClient.java
 *
 * Copyright 2014 cokosoft.com. All Rights Reserved.
 */
package cn.changl.safe360.android.api;

import java.util.ArrayList;

import cn.changl.safe360.android.utils.PreferencesUtils;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.ClientVersion;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.Mobile;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.PushInfo;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.SystemType;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.UserInfo;

/**
 * System Api客户端
 */
public class SysApiClient extends ApiClient {

	/**
	 * 检测新版本
	 * 
	 * @param userId
	 * @param imei
	 * @param versionCode
	 * @return
	 */
	public static Safe360Pb checkVersion(int userId, String imei, int versionCode) {

		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
		builder.setCommand(Safe360Pb.Command.CHECK_VERSION);

		Mobile.Builder mb = Mobile.newBuilder();
		if (userId != -1) {
			mb.setUserId(userId);
		}
		mb.setDn(imei);
		mb.setSystemType(SystemType.ANDROID);

		ClientVersion.Builder cb = ClientVersion.newBuilder();
		cb.setVersionCode(versionCode);

		builder.setMobile(mb);
		builder.setClientVersion(cb);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 刷新token
	 * 
	 * @param userId
	 * @param token
	 * @param password
	 * @return
	 */
	public static Safe360Pb refreshToken(int userId, String token, String password) {

		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
		builder.setCommand(Safe360Pb.Command.REFRESH_TOKEN);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		ub.setPassword(password);
		builder.setUserInfo(ub);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 获取服务器动态ip
	 * 
	 * @param imei
	 * @param versionCode
	 * @return
	 */
	public static Safe360Pb getServerDynamicIp(String imei, int versionCode) {
		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
		builder.setCommand(Safe360Pb.Command.HOST_INFO);

		Mobile.Builder mb = Mobile.newBuilder();
		mb.setSystemType(SystemType.ANDROID);
		mb.setDn(imei);
		builder.setMobile(mb);

		ClientVersion.Builder cb = ClientVersion.newBuilder();
		cb.setVersionCode(versionCode);
		builder.setClientVersion(cb);

		return executeNetworkInvokeSimple(builder.build());
	}

	public static Safe360Pb pushMessage(ArrayList<Integer> useridlist, String title, String desc, String data) {
		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
		builder.setCommand(Safe360Pb.Command.PUSH_MESSAGE);

		for (int userid : useridlist) {
			UserInfo.Builder ub = UserInfo.newBuilder();
			ub.setUserId(userid);
			builder.addUserInfos(ub);
		}

		PushInfo.Builder push = PushInfo.newBuilder();
		push.setTitle(title);
		push.setDescription(desc);
		push.setData(data);
		push.setBChannelId(PreferencesUtils.getBaiduChannelId());
		push.setBUserId(PreferencesUtils.getBaiduUserId());
		builder.setPushInfo(push);

		return executeNetworkInvokeSimple(builder.build());
	}
}
