/**
 * ClueApiClient.java
 *
 * Copyright 2014 cokosoft.com. All Rights Reserved.
 */
package mobi.dlys.android.familysafer.api;

import java.util.List;

import mobi.dlys.android.core.utils.StringUtils;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.ActionType;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.Clue;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.ClueImage;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.PageInfo;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.UserInfo;

/**
 * 线索Api客户端. User: Wangle<87292008@qq.com> Date: 2014/6/9 0009 Time: 18:09
 */
public class ClueApiClient extends ApiClient {

	/**
	 * 拍线索
	 * 
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户session标识
	 * @param lng
	 *            经度
	 * @param lat
	 *            纬度
	 * @param imageList
	 *            图片列表
	 * @param msg
	 *            文字信息
	 */
	public static FamilySaferPb pushClue(int userId, String token, String location, String lng, String lat, List<ClueImage> imageList, String msg,
			String phoneModel, boolean isEvent) {
		if (StringUtils.isEmail(token) || ((null == imageList || imageList.isEmpty()) && StringUtils.isEmpty(msg))) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(FamilySaferPb.Command.PUSH_CLUE);

		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setUserId(userId);
		ub.setToken(token);
		ub.setLng(lng);
		ub.setLat(lat);
		ub.setLocation(location);
		builder.setUserInfo(ub);

		Clue.Builder cb = Clue.newBuilder();
		cb.addAllImages(imageList);
		cb.setMsg(msg);
		cb.setPhoneModel(phoneModel);
		cb.setIsEvent(isEvent);
		builder.setClue(cb);

		return executeNetworkInvokeSimple(builder.build());
	}

	/**
	 * 线索列表
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
	public static FamilySaferPb clueList(int userId, String token, int pageNo, int pageSize, ActionType actionType) {
		if (StringUtils.isEmpty(token)) {
			return null;
		}
		FamilySaferPb.Builder builder = FamilySaferPb.newBuilder();
		builder.setCommand(FamilySaferPb.Command.CLUE_LIST);

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
