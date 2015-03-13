/**
 * BasicApiClient.java
 *
 * Copyright 2014 cokosoft.com. All Rights Reserved.
 */
package mobi.dlys.android.familysafer.api;

import mobi.dlys.android.core.utils.StringUtils;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.ClientVersion;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.Command;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.Mobile;

/**
 * 基础Api客户端. User: Wangle<87292008@qq.com> Date: 2014/6/9 0009 Time: 17:38
 */
public class BasicApiClient extends ApiClient {

	/**
	 * 检查版本
	 * 
	 * @param dn
	 *            手机唯一标识(安卓手机取imei号)
	 * @param versionCode
	 *            版本号
	 */
	public static FamilySaferPb checkVersion(String dn, int versionCode) {
		if (StringUtils.isEmpty(dn) || versionCode <= 0) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(Command.CHECK_VERSION);

		Mobile.Builder mb = Mobile.newBuilder();
		mb.setDn(dn);
		builder.setMobile(mb);

		ClientVersion.Builder cvb = ClientVersion.newBuilder();
		cvb.setVersionCode(versionCode);
		builder.setClientVersion(cvb);

		return executeNetworkInvokeSimple(builder.build());
	}

}
