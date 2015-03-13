/**
 * CheckInApiClient.java
 *
 * Copyright 2014 cokosoft.com. All Rights Reserved.
 */
package mobi.dlys.android.familysafer.api;

import java.util.List;

import mobi.dlys.android.core.utils.StringUtils;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.ActionType;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.CheckIn;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.Event;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.EventType;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.Friend;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.UserInfo;

/**
 * CheckIn Api客户端. User: Wangle<87292008@qq.com> Date: 2014/6/9 0009 Time: 18:10
 */
public class CheckInApiClient extends ApiClient {

	/**
	 * 给所有人发通知
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
	public static FamilySaferPb checkIn2All(int userId, String token, String lng, String lat, String location, String msg) {
		return checkIn(userId, token, lng, lat, location, null, ActionType.AT_ALL, msg);
	}

	/**
	 * 给指定好友发通知
	 * 
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户session标识
	 * @param lng
	 *            经度
	 * @param lat
	 *            纬度
	 * @param friendIdList
	 *            好友ID列表
	 */
	public static FamilySaferPb checkIn2SomeFriend(int userId, String token, String lng, String lat, String location, List<Integer> friendIdList, String msg) {
		return checkIn(userId, token, lng, lat, location, friendIdList, ActionType.AT_CUSTOM, msg);
	}

	/**
	 * Check In
	 * 
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户session标识
	 * @param lng
	 *            经度
	 * @param lat
	 *            纬度
	 * @param friendIdList
	 *            好友ID列表
	 * @param actionType
	 *            动作类型
	 */
	private static FamilySaferPb checkIn(int userId, String token, String lng, String lat, String location, List<Integer> friendIdList, ActionType actionType,
			String msg) {
		if (StringUtils.isEmail(token)) {
			return null;
		}
		if (actionType.equals(ActionType.AT_CUSTOM) && (null == friendIdList || friendIdList.isEmpty())) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(FamilySaferPb.Command.CHECK_IN);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		ub.setLng(lng);
		ub.setLat(lat);
		ub.setLocation(location);
		builder.setUserInfo(ub);

		builder.setActionType(actionType);
		if (actionType.equals(ActionType.AT_CUSTOM)) {
			Friend.Builder fb;
			for (Integer friendId : friendIdList) {
				fb = Friend.newBuilder();
				fb.setUserId(friendId);
				builder.addFrds(fb);
			}
		}

		CheckIn.Builder cb = CheckIn.newBuilder();
		cb.setMsg(msg);
		builder.setCheckIn(cb);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 获取通知列表
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
	public static FamilySaferPb checkInMsgList(int userId, String token, int pageNo, int pageSize, boolean unread, List<EventType> eventTypeList) {
		if (StringUtils.isEmpty(token) || null == eventTypeList) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(FamilySaferPb.Command.EVENT_LIST);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		builder.setUserInfo(ub);

		FamilySaferPb.PageInfo.Builder pib = FamilySaferPb.PageInfo.newBuilder();
		pib.setPageNo(pageNo);
		pib.setPageSize(pageSize);
		builder.setPageInfo(pib);

		if (unread) {
			builder.setActionType(ActionType.AT_UNREAD_EVENT);
		} else {
			builder.setActionType(ActionType.AT_ALL_EVENT);
		}

		for (EventType eventType : eventTypeList) {
			builder.addEventTypes(eventType);
		}

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 获取通知数
	 * 
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户session标识
	 */
	public static FamilySaferPb checkInMsgNum(int userId, String token, long eventReceiveTime) {
		if (StringUtils.isEmpty(token)) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(FamilySaferPb.Command.TIP_NEW_NUM);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		builder.setUserInfo(ub);

		// RememberTime.Builder rb = RememberTime.newBuilder();
		// rb.setEventReceiveTime(eventReceiveTime);
		// builder.setRememberTime(rb);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 签到确认
	 * 
	 * @param userId
	 * @param token
	 * @param checkInId
	 * @return
	 */
	public static FamilySaferPb checkInConfirm(int userId, String token, int checkInId, int eventId) {
		if (StringUtils.isEmpty(token)) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(FamilySaferPb.Command.CHECK_IN_CONFIRM);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		builder.setUserInfo(ub);

		CheckIn.Builder cb = CheckIn.newBuilder();
		cb.setCheckInId(checkInId);
		builder.setCheckIn(cb);

		Event.Builder eb = Event.newBuilder();
		eb.setEventId(eventId);
		builder.setEvent(eb);

		return executeNetworkInvokeSimple(builder.build());
	}

}
