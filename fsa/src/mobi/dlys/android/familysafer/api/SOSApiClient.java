/**
 * CheckInApiClient.java
 *
 * Copyright 2014 cokosoft.com. All Rights Reserved.
 */
package mobi.dlys.android.familysafer.api;

import mobi.dlys.android.core.utils.StringUtils;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.ActionType;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.Event;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.PageInfo;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.UserInfo;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.VoiceSOS;

/**
 * SOS api客户端
 */
public class SOSApiClient extends ApiClient {

	/**
	 * 语音求救
	 * 
	 * @param userId
	 * @param token
	 * @param lng
	 * @param lat
	 * @param location
	 * @param voiceAddress
	 * @return
	 */
	public static FamilySaferPb voiceSOS(int userId, String token, String lng, String lat, String location, String voiceAddress, int duration) {

		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(FamilySaferPb.Command.VOICE_SOS);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		ub.setLng(lng);
		ub.setLat(lat);
		ub.setLocation(location);
		builder.setUserInfo(ub);

		VoiceSOS.Builder vb = VoiceSOS.newBuilder();
		vb.setVoice(voiceAddress);
		vb.setDuration(duration);
		builder.setVoiceSOS(vb);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 语音求救确认
	 * 
	 * @param userId
	 * @param token
	 * @param voiceSOSId
	 * @return
	 */
	public static FamilySaferPb voiceSOSConfirm(int userId, String token, int voiceSOSId, int eventId) {

		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(FamilySaferPb.Command.VOICE_SOS_CONFIRM);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		builder.setUserInfo(ub);

		VoiceSOS.Builder vb = VoiceSOS.newBuilder();
		vb.setVoiceSOSId(voiceSOSId);
		builder.setVoiceSOS(vb);

		Event.Builder eb = Event.newBuilder();
		eb.setEventId(eventId);
		builder.setEvent(eb);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 获取我的SOS列表
	 * 
	 * @param userId
	 * @param token
	 * @param actionType
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public static FamilySaferPb myVoiceSOS(int userId, String token, ActionType actionType, int pageNo, int pageSize) {
		if (StringUtils.isEmpty(token)) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(FamilySaferPb.Command.VOICE_SOS_LIST);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		builder.setUserInfo(ub);

		PageInfo.Builder pib = PageInfo.newBuilder();
		pib.setPageNo(pageNo);
		pib.setPageSize(pageSize);
		builder.setPageInfo(pib);

		builder.setActionType(actionType);
		return executeNetworkInvokeSimple(builder.build());
	}
}
