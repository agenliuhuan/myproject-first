/**
 * CheckInApiClient.java
 *
 * Copyright 2014 cokosoft.com. All Rights Reserved.
 */
package mobi.dlys.android.familysafer.api;

import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.ClientVersion;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.Mobile;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.SystemType;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.UserInfo;

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
	public static FamilySaferPb checkVersion(int userId, String imei, int versionCode) {

		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(FamilySaferPb.Command.CHECK_VERSION);

		Mobile.Builder mb = Mobile.newBuilder();
		mb.setUserId(userId);
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
	public static FamilySaferPb refreshToken(int userId, String token, String password) {

		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(FamilySaferPb.Command.REFRESH_TOKEN);

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
	public static FamilySaferPb getServerDynamicIp(String imei, int versionCode) {
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(FamilySaferPb.Command.HOST_INFO);

		Mobile.Builder mb = Mobile.newBuilder();
		mb.setSystemType(SystemType.ANDROID);
		mb.setDn(imei);
		builder.setMobile(mb);

		ClientVersion.Builder cb = ClientVersion.newBuilder();
		cb.setVersionCode(versionCode);
		builder.setClientVersion(cb);

		return executeNetworkInvokeSimple(builder.build());
	}
}
