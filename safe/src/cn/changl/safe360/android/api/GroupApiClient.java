/**
 * UserApiClient.java
 *
 * Copyright 2014 cokosoft.com. All Rights Reserved.
 */
package cn.changl.safe360.android.api;

import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.Command;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.Friend;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.PageInfo;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.UserInfo;

/**
 * 用户Api客户端. User: Wangle<87292008@qq.com> Date: 2014/6/9 0009 Time: 18:07
 */
public class GroupApiClient extends ApiClient {

	/**
	 * 获取圈子信息
	 * 
	 * @param userId
	 * @param token
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public static Safe360Pb getGroupInfo(int userId, String token, int pageNo, int pageSize) {
		if (userId <= 0) {
			return null;
		}
		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
		builder.setCommand(Command.GROUP_INFO);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		builder.setUserInfo(ub);

		PageInfo.Builder pib = PageInfo.newBuilder();
		pib.setPageNo(pageNo);
		pib.setPageSize(pageSize);
		builder.setPageInfo(pib);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 邀请好友
	 * 
	 * @param userId
	 * @param token
	 * @param friendId
	 * @return
	 */
	public static Safe360Pb inviteFriend(int userId, String token, int friendId) {
		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
		builder.setCommand(Command.INVITE_FRIEND);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		builder.setUserInfo(ub);

		Friend.Builder fb = Friend.newBuilder();
		fb.setUserId(friendId);
		builder.setFriend(fb);

		return executeNetworkInvokeSimple(builder.build());
	}

	public static Safe360Pb inviteTempFriend(int userId, String token, String phone) {
		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
		builder.setCommand(Command.INVITE_TEMP_FRIEND);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		builder.setUserInfo(ub);

		Friend.Builder fb = Friend.newBuilder();
		fb.setPhone(phone);
		builder.setFriend(fb);

		return executeNetworkInvokeSimple(builder.build());
	}

	public static Safe360Pb getTempUserLocation(int userId, String token, String phone) {
		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
		builder.setCommand(Command.GET_TEMP_USER_LOCATION);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		builder.setUserInfo(ub);

		Friend.Builder fb = Friend.newBuilder();
		fb.setPhone(phone);
		builder.setFriend(fb);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 删除好友
	 * 
	 * @param userId
	 * @param token
	 * @param friendId
	 * @return
	 */
	public static Safe360Pb removeFriend(int userId, String token, int friendId) {
		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
		builder.setCommand(Command.REMOVE_FRIEND);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		builder.setUserInfo(ub);

		Friend.Builder fb = Friend.newBuilder();
		fb.setUserId(friendId);
		builder.setFriend(fb);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 加入圈子
	 * 
	 * @param userId
	 * @param token
	 * @param friendId
	 * @return
	 */
	public static Safe360Pb joinGroup(int userId, String token, int friendId) {
		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
		builder.setCommand(Command.JOIN_GROUP);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		builder.setUserInfo(ub);

		Friend.Builder fb = Friend.newBuilder();
		fb.setUserId(friendId);
		builder.setFriend(fb);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 退出圈子
	 * 
	 * @param userId
	 * @param token
	 * @return
	 */
	public static Safe360Pb exitGroup(int userId, String token) {
		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
		builder.setCommand(Command.EXIT_GROUP);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		builder.setUserInfo(ub);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 空闲好友
	 * 
	 * @param userId
	 * @param token
	 * @return
	 */
	public static Safe360Pb getidleFri(int userId, String token) {
		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
		builder.setCommand(Command.IDLE_FRIEND);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		builder.setUserInfo(ub);

		return executeNetworkInvokeSimple(builder.build());
	}

}
