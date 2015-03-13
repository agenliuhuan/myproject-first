/**
 * UserApiClient.java
 *
 * Copyright 2014 cokosoft.com. All Rights Reserved.
 */
package mobi.dlys.android.familysafer.api;

import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.Command;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.Friend;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.Msg;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.PageInfo;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.UserInfo;
import android.text.TextUtils;

/**
 * IM Api客户端. User: Wangle<87292008@qq.com> Date: 2014/6/9 0009 Time: 18:07
 */
public class ImApiClient extends ApiClient {

	/**
	 * 发送语音聊天消息
	 * 
	 * @param userId
	 * @param token
	 * @param friendId
	 * @param voiceUri
	 * @param duration
	 * @return
	 */
	public static FamilySaferPb sendMsg(int userId, String token, int friendId, String voiceUri, int duration) {
		if (TextUtils.isEmpty(voiceUri)) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(Command.SEND_MSG);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		builder.setUserInfo(ub);

		Friend.Builder fb = Friend.newBuilder();
		fb.setUserId(friendId);
		builder.setFrd(fb);

		Msg.Builder mb = Msg.newBuilder();
		mb.setVoice(voiceUri);
		mb.setDuration(duration);
		builder.setMsg(mb);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 获取语音消息主题列表
	 * 
	 * @param userId
	 * @param token
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public static FamilySaferPb msgTopicList(int userId, String token, int pageNo, int pageSize) {
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(Command.MSG_TOPIC_LIST);

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
	 * 获取语音消息列表
	 * 
	 * @param userId
	 * @param token
	 * @param pageNo
	 * @param pageSize
	 * @param friendId
	 * @param msgId
	 * @return
	 */
	public static FamilySaferPb msgList(int userId, String token, int pageNo, int pageSize, int friendId, int msgId) {
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(Command.MSG_LIST);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		builder.setUserInfo(ub);

		PageInfo.Builder pib = PageInfo.newBuilder();
		pib.setPageNo(pageNo);
		pib.setPageSize(pageSize);
		builder.setPageInfo(pib);

		Friend.Builder fb = Friend.newBuilder();
		fb.setUserId(friendId);
		builder.setFrd(fb);

		Msg.Builder mb = Msg.newBuilder();
		mb.setMsgId(msgId);
		builder.setMsg(mb);

		return executeNetworkInvokeSimple(builder.build());
	}
}
