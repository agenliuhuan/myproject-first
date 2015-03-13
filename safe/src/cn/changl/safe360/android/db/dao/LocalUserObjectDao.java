package cn.changl.safe360.android.db.dao;

import java.util.ArrayList;
import java.util.List;

import cn.changl.safe360.android.biz.vo.LocalUserObject;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.UserInfo;

public class LocalUserObjectDao extends AbstractDao<LocalUserObject> {

	public LocalUserObjectDao() {
		super();
	}

	public List<UserInfo> getLocalUserInfoList() {

		List<UserInfo> userInfoList = new ArrayList<UserInfo>();
		if (count() > 0) {
			try {
				List<LocalUserObject> userList = findAll();
				for (LocalUserObject userObject : userList) {
					userInfoList.add(userObject.toUserInfo());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return userInfoList;
	}

	public LocalUserObject getLocalUserByPhone(String phone) {
		try {
			List<LocalUserObject> userList = findAll();
			for (LocalUserObject userObject : userList) {
				if (phone.equals(userObject.getPhone())) {
					return userObject;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
