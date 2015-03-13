/**
 * FriendApiClient.java
 *
 * Copyright 2014 cokosoft.com. All Rights Reserved.
 */
package mobi.dlys.android.familysafer.api;

import mobi.dlys.android.core.utils.StringUtils;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.ActionType;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.Command;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.Friend;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.PageInfo;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.RememberTime;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.UserInfo;

/**
 * 好友Api客户端. User: Wangle<87292008@qq.com> Date: 2014/6/9 0009 Time: 18:08
 */
public class FriendApiClient extends ApiClient {

	/**
	 * 添加好友
	 * 
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户session标识
	 * @param friendId
	 *            好友ID
	 */
	public static FamilySaferPb addFriend(int userId, String token, int friendId) {
		if (StringUtils.isEmail(token) || friendId <= 0) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(Command.ADD_FRIEND);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		builder.setUserInfo(ub);

		Friend.Builder fb = Friend.newBuilder();
		fb.setUserId(friendId);
		builder.setFrd(fb);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 获取好友请求列表
	 * 
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户session标识
	 * @param pageNo
	 *            页码
	 * @param pageSize
	 *            页大小
	 */
	public static FamilySaferPb friendReqList(int userId, String token, int pageNo, int pageSize) {
		if (StringUtils.isEmpty(token)) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(Command.FRIEND_REQ_LIST);

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
	 * 同意好友请求
	 * 
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户session标识
	 * @param friendId
	 *            好友ID
	 */
	public static FamilySaferPb agreeFriendReq(int userId, String token, int friendId) {
		return optFriendReq(userId, token, friendId, ActionType.AT_AGREE);
	}

	/**
	 * 拒绝好友请求
	 * 
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户session标识
	 * @param friendId
	 *            好友ID
	 */
	public static FamilySaferPb refuseFriendReq(int userId, String token, int friendId) {
		return optFriendReq(userId, token, friendId, ActionType.AT_REFUSE);
	}

	/**
	 * 操作好友请求
	 * 
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户session标识
	 * @param friendId
	 *            好友ID
	 * @param actionType
	 *            动作类型
	 */
	private static FamilySaferPb optFriendReq(int userId, String token, int friendId, ActionType actionType) {
		if (StringUtils.isEmpty(token) || friendId <= 0) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(Command.OPT_FRIEND_REQ);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		builder.setUserInfo(ub);

		Friend.Builder fb = Friend.newBuilder();
		fb.setUserId(friendId);
		builder.setFrd(fb);

		builder.setActionType(actionType);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 获取好友列表
	 * 
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户session标识
	 * @param pageNo
	 *            页码
	 * @param pageSize
	 *            页大小
	 */
	public static FamilySaferPb friendList(int userId, String token, int pageNo, int pageSize) {
		if (StringUtils.isEmpty(token)) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(Command.FRIEND_LIST);

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
	 * 删除好友
	 * 
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户session标识
	 * @param friendId
	 *            好友ID
	 */
	public static FamilySaferPb delFriend(int userId, String token, int friendId) {
		if (StringUtils.isEmail(token) || friendId <= 0) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(Command.DEL_FRIEND);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		builder.setUserInfo(ub);

		Friend.Builder fb = Friend.newBuilder();
		fb.setUserId(friendId);
		builder.setFrd(fb);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 设置备注名
	 * 
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户session标识
	 * @param friendId
	 *            好友ID
	 * @param remarkName
	 *            备注名
	 */
	public static FamilySaferPb setRemarkName(int userId, String token, int friendId, String remarkName) {
		if (StringUtils.isEmail(token) || friendId <= 0) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(Command.SET_REMARK_NAME);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		builder.setUserInfo(ub);

		Friend.Builder fb = Friend.newBuilder();
		fb.setUserId(friendId);
		fb.setRemarkName(remarkName);
		builder.setFrd(fb);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 显示我的位置
	 * 
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户session标识
	 * @param friendId
	 *            好友ID
	 * @param showMyPosition
	 *            显示我的位置
	 */
	public static FamilySaferPb showMyPosition(int userId, String token, int friendId, boolean showMyPosition) {
		if (StringUtils.isEmail(token) || friendId <= 0) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(Command.SHOW_MY_POSITION);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		builder.setUserInfo(ub);

		Friend.Builder fb = Friend.newBuilder();
		fb.setUserId(friendId);
		fb.setShowMyPosition(showMyPosition);
		builder.setFrd(fb);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 获取新的好友请求数目
	 * 
	 * @param userId
	 * @param token
	 * @param friendReqReceiveTime
	 * @param eventReceiveTime
	 * @return
	 */
	public static FamilySaferPb checkFriendRequestNum(int userId, String token, long friendReqReceiveTime) {
		if (StringUtils.isEmpty(token)) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(FamilySaferPb.Command.TIP_NEW_NUM);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		builder.setUserInfo(ub);

		RememberTime.Builder rb = RememberTime.newBuilder();
		rb.setFriendReqReceiveTime(friendReqReceiveTime);
		builder.setRememberTime(rb);

		return executeNetworkInvokeSimple(builder.build());
	}
}
