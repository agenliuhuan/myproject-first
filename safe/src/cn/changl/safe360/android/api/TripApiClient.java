/**
 * UserApiClient.java
 *
 * Copyright 2014 cokosoft.com. All Rights Reserved.
 */
package cn.changl.safe360.android.api;

import java.util.List;

import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.Command;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.Location;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.Trip;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.UserInfo;

/**
 * 用户Api客户端. User: Wangle<87292008@qq.com> Date: 2014/6/9 0009 Time: 18:07
 */
public class TripApiClient extends ApiClient {

	/**
	 * 获取护航信息
	 * 
	 * @param userId
	 * @param token
	 * @param tripId
	 * @return
	 */
	public static Safe360Pb getTripInfo(int userId, String token, int tripId) {
		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
		builder.setCommand(Command.TRIP_INFO);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		builder.setUserInfo(ub);

		Trip.Builder tb = Trip.newBuilder();
		tb.setTripId(tripId);
		builder.setTrip(tb);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 开始导航
	 * 
	 * @param userId
	 * @param token
	 * @param lng
	 * @param lat
	 * @param location
	 * @param userList
	 * @return
	 */
	public static Safe360Pb startTrip(int userId, String token, String lng, String lat, String location, List<UserInfo> userList) {
		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
		builder.setCommand(Command.START_TRIP);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		builder.setUserInfo(ub);

		Trip.Builder tb = Trip.newBuilder();

		Location.Builder lb = Location.newBuilder();
		lb.setLng(lng);
		lb.setLat(lat);
		lb.setAddress(location);
		tb.setBeginLocation(lb);

		for (int i = 0; i < userList.size(); i++) {
			UserInfo.Builder ub1 = UserInfo.newBuilder();
			UserInfo info = userList.get(i);
			tb.addUserInfos(info);

		}
		builder.setTrip(tb);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 结束护航
	 * 
	 * @param userId
	 * @param token
	 * @param tripId
	 * @param lng
	 * @param lat
	 * @param location
	 * @return
	 */
	public static Safe360Pb finishTrip(int userId, String token, int tripId, String lng, String lat, String location) {
		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
		builder.setCommand(Command.FINISH_TRIP);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		builder.setUserInfo(ub);

		Trip.Builder tb = Trip.newBuilder();
		// if (tripId != 0) {
		// }
		tb.setTripId(tripId);

		Location.Builder lb = Location.newBuilder();
		lb.setLng(lng);
		lb.setLat(lat);
		lb.setAddress(location);
		tb.setEndLocation(lb);

		builder.setTrip(tb);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 退出护航
	 * 
	 * @param userId
	 * @param token
	 * @param tripId
	 * @return
	 */
	public static Safe360Pb exitTrip(int userId, String token, int tripId) {
		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
		builder.setCommand(Command.EXIT_TRIP);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		builder.setUserInfo(ub);

		Trip.Builder tb = Trip.newBuilder();
		if (tripId != 0) {
			tb.setTripId(tripId);
		}

		builder.setTrip(tb);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 修改护航
	 * 
	 * @param userId
	 * @param token
	 * @param tripId
	 * @param lat
	 * @param lng
	 * @param address
	 * @return
	 */
	public static Safe360Pb modifyTrip(int userId, String token, int tripId, String lat, String lng, String address) {
		Safe360Pb.Builder builder = Safe360Pb.newBuilder();
		builder.setCommand(Command.MODIFY_TRIP);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		builder.setUserInfo(ub);

		Trip.Builder tb = Trip.newBuilder();
		if (tripId != 0) {
			tb.setTripId(tripId);

			Location.Builder lb = Location.newBuilder();
			lb.setLat(lat);
			lb.setLng(lng);
			lb.setAddress(address);
			tb.setEndLocation(lb);
		}

		builder.setTrip(tb);

		return executeNetworkInvokeSimple(builder.build());
	}

}
