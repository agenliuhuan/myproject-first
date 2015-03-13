/**
 * LocationApiClient.java
 *
 * Copyright 2014 cokosoft.com. All Rights Reserved.
 */
package mobi.dlys.android.familysafer.api;

import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.Command;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.UserInfo;


/**
 * 地理位置Api客户端. User: Wangle<87292008@qq.com> Date: 2014/6/9 0009 Time: 18:11
 */
public class LocationApiClient extends ApiClient {

	/**
	 * 获取用户位置
	 * 
	 * @param userId
	 *            用户ID
	 */
	public static FamilySaferPb getUserPosition(int userId) {
		if (userId <= 0) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(Command.GET_USER_LOCATION);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		builder.setUserInfo(ub);

		return executeNetworkInvokeSimple(builder.build());
	}

}
