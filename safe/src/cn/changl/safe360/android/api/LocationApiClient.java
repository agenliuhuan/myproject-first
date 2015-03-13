/**
 * UserApiClient.java
 *
 * Copyright 2014 cokosoft.com. All Rights Reserved.
 */
package cn.changl.safe360.android.api;

import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.Command;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.UserInfo;

/**
 * 用户Api客户端. User: Wangle<87292008@qq.com> Date: 2014/6/9 0009 Time: 18:07
 */
public class LocationApiClient extends ApiClient {

	/**
	 * 获取用户位置
	 * 
	 * @param userId
	 *            用户ID
	 */
	public static Safe360Pb getUserPosition(int userId) {
		if (userId <= 0) {
			return null;
		}
		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
		builder.setCommand(Command.GET_USER_LOCATION);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		builder.setUserInfo(ub);

		return executeNetworkInvokeSimple(builder.build());
	}

}
