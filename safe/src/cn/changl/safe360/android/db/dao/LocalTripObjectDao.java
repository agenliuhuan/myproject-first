package cn.changl.safe360.android.db.dao;

import java.util.ArrayList;
import java.util.List;

import cn.changl.safe360.android.biz.vo.LocalTripObject;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.UserInfo;

public class LocalTripObjectDao extends AbstractDao<LocalTripObject> {

	public LocalTripObjectDao() {
		super();
	}

	public List<UserInfo> getLocalTripList() {

		List<UserInfo> userInfoList = new ArrayList<UserInfo>();
		if (count() > 0) {
			try {
				List<LocalTripObject> userList = findAll();
				for (LocalTripObject userObject : userList) {
					userInfoList.add(userObject.toUserInfo());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return userInfoList;
	}

	public boolean hasLocalTrip() {
		if (count() > 0) {
			return true;
		} else {
			return false;
		}
	}
}
