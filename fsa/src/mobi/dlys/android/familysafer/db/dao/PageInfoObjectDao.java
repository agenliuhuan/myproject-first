package mobi.dlys.android.familysafer.db.dao;

import mobi.dlys.android.familysafer.biz.vo.PageInfoObject;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.EventType;

public class PageInfoObjectDao extends AbstractDao<PageInfoObject> {
	public static final int ID_FRIEND = 101; // 好友列表
	public static final int ID_FRIEND_REQUEST = 102; // 好友请求
	// public static final int ID_EVENT = 103; // 所有通知（已弃用）
	public static final int ID_MY_CLUE = 104; // 我的线索
	public static final int ID_PUBLIC_CLUE = 105; // 公共线索
	public static final int ID_IM_MSG = 106; // 消息列表
	public static final int ID_IM_MSG_TOPIC = 107; // 消息主题
	public static final int ID_MY_SOS = 108; // 我的求救列表
	public static final int ID_EVENT_FRIEND_SOS = 109; // 好友SOS通知
	public static final int ID_EVENT_FRIEND_CHECKIN = 110; // 好友Checkin通知
	public static final int ID_EVENT_CONFIRM = 111; // 已确认的通知
	public static final int ID_EVENT_CLUE = 112; // 拍照留证的通知

	public static final int FRIEND_PAGE_SIZE = 30;
	public static final int FRIEND_REQUEST_PAGE_SIZE = 30;
	public static final int EVENT_PAGE_SIZE = 10;
	public static final int CLUE_PAGE_SIZE = 10;
	public static final int PUBLIC_CLUE_PAGE_SIZE = 10;
	public static final int IM_MSG_COUNT = 20;
	public static final int IM_MSG_TOPIC_COUNT = 20;
	public static final int MY_SOS_COUNT = 20;

	public static void setPageInfo(int typeId, PageInfoObject pageInfo) {
		if (null != pageInfo) {
			pageInfo.setTypeId(typeId);
			new PageInfoObjectDao().updateMsgId(pageInfo);
		}
	}

	public static PageInfoObject getNextCachePageNo(int typeId) {
		PageInfoObjectDao pageInfoDao = new PageInfoObjectDao();
		PageInfoObject pageInfo = pageInfoDao.findById(typeId);
		if (pageInfo != null && !(pageInfo.isLastPage() && pageInfo.getPageNo() == pageInfo.getReadCachePageNo())) {
			pageInfo.setReadCachePageNo(pageInfo.getReadCachePageNo() + 1);
		}
		return pageInfo;
	}

	public static PageInfoObject getPageInfo(int typeId) {
		PageInfoObject pageInfoObject = new PageInfoObjectDao().findById(typeId);
		if (null == pageInfoObject) {
			pageInfoObject = new PageInfoObject();
			pageInfoObject.setTypeId(typeId);
		}
		return pageInfoObject;
	}

	public static PageInfoObject getFirstPageInfo(int typeId) {
		PageInfoObject pageInfoObject = new PageInfoObjectDao().findById(typeId);
		if (null == pageInfoObject) {
			pageInfoObject = new PageInfoObject();
			pageInfoObject.setTypeId(typeId);
		}
		pageInfoObject.setReadCachePageNo(1);
		return pageInfoObject;
	}

	public static int getCurrentPageNo(int typeId) {
		PageInfoObject pageInfo = new PageInfoObjectDao().findById(typeId);
		if (pageInfo != null) {
			return pageInfo.getPageNo();
		}
		return 1;
	}

	public static boolean isLastPage(int typeId) {
		PageInfoObject pageInfo = new PageInfoObjectDao().findById(typeId);
		if (pageInfo != null) {
			return pageInfo.isLastPage();
		}
		return false;
	}

	public static void initAllPageInfo() {
		// 初始化家人列表pageInfo
		PageInfoObject friendPageInfo = new PageInfoObject();
		friendPageInfo.setTypeId(ID_FRIEND);
		friendPageInfo.setPageNo(0);
		friendPageInfo.setReadCachePageNo(0);
		friendPageInfo.setPageSize(FRIEND_PAGE_SIZE);
		friendPageInfo.setLastPage(false);
		new PageInfoObjectDao().insert(friendPageInfo);

		// 初始化家人请求列表pageInfo
		PageInfoObject friendRequestPageInfo = new PageInfoObject();
		friendRequestPageInfo.setTypeId(ID_FRIEND_REQUEST);
		friendRequestPageInfo.setPageNo(0);
		friendRequestPageInfo.setReadCachePageNo(0);
		friendRequestPageInfo.setPageSize(FRIEND_REQUEST_PAGE_SIZE);
		friendRequestPageInfo.setLastPage(false);
		new PageInfoObjectDao().insert(friendRequestPageInfo);

		// 初始化我的线索列表pageInfo
		PageInfoObject myCluePageInfo = new PageInfoObject();
		myCluePageInfo.setTypeId(ID_MY_CLUE);
		myCluePageInfo.setPageNo(0);
		myCluePageInfo.setReadCachePageNo(0);
		myCluePageInfo.setPageSize(CLUE_PAGE_SIZE);
		myCluePageInfo.setLastPage(false);
		new PageInfoObjectDao().insert(myCluePageInfo);

		// 初始化公共线索列表pageInfo
		PageInfoObject publicCluePageInfo = new PageInfoObject();
		publicCluePageInfo.setTypeId(ID_PUBLIC_CLUE);
		publicCluePageInfo.setPageNo(0);
		publicCluePageInfo.setReadCachePageNo(0);
		publicCluePageInfo.setPageSize(PUBLIC_CLUE_PAGE_SIZE);
		publicCluePageInfo.setLastPage(false);
		new PageInfoObjectDao().insert(publicCluePageInfo);

		// 初始化语音消息列表pageInfo
		PageInfoObject msgPageInfo = new PageInfoObject();
		msgPageInfo.setTypeId(ID_IM_MSG);
		msgPageInfo.setPageNo(0);
		msgPageInfo.setReadCachePageNo(0);
		msgPageInfo.setPageSize(IM_MSG_COUNT);
		msgPageInfo.setLastPage(false);
		new PageInfoObjectDao().insert(msgPageInfo);

		// 初始化语音消息主题列表pageInfo
		PageInfoObject msgTopicPageInfo = new PageInfoObject();
		msgTopicPageInfo.setTypeId(ID_IM_MSG_TOPIC);
		msgTopicPageInfo.setPageNo(0);
		msgTopicPageInfo.setReadCachePageNo(0);
		msgTopicPageInfo.setPageSize(IM_MSG_TOPIC_COUNT);
		msgTopicPageInfo.setLastPage(false);
		new PageInfoObjectDao().insert(msgTopicPageInfo);

		// 初始化我的求救列表pageInfo
		PageInfoObject mySOSPageInfo = new PageInfoObject();
		mySOSPageInfo.setTypeId(ID_MY_SOS);
		mySOSPageInfo.setPageNo(0);
		mySOSPageInfo.setReadCachePageNo(0);
		mySOSPageInfo.setPageSize(MY_SOS_COUNT);
		mySOSPageInfo.setLastPage(false);
		new PageInfoObjectDao().insert(mySOSPageInfo);

		// 初始化家人的求救列表pageInfo
		PageInfoObject friendSOSPageInfo = new PageInfoObject();
		friendSOSPageInfo.setTypeId(ID_EVENT_FRIEND_SOS);
		friendSOSPageInfo.setPageNo(0);
		friendSOSPageInfo.setReadCachePageNo(0);
		friendSOSPageInfo.setPageSize(EVENT_PAGE_SIZE);
		friendSOSPageInfo.setLastPage(false);
		friendSOSPageInfo.setEventType(EventType.ET_VOICE_SOS.getNumber());
		new PageInfoObjectDao().insert(friendSOSPageInfo);

		// 初始化家人的Checkin列表pageInfo
		PageInfoObject friendCheckinPageInfo = new PageInfoObject();
		friendCheckinPageInfo.setTypeId(ID_EVENT_FRIEND_CHECKIN);
		friendCheckinPageInfo.setPageNo(0);
		friendCheckinPageInfo.setReadCachePageNo(0);
		friendCheckinPageInfo.setPageSize(EVENT_PAGE_SIZE);
		friendCheckinPageInfo.setLastPage(false);
		friendCheckinPageInfo.setEventType(EventType.ET_CHECK_IN.getNumber());
		new PageInfoObjectDao().insert(friendCheckinPageInfo);

		// 初始化已确认通知列表pageInfo
		PageInfoObject confirmPageInfo = new PageInfoObject();
		confirmPageInfo.setTypeId(ID_EVENT_CONFIRM);
		confirmPageInfo.setPageNo(0);
		confirmPageInfo.setReadCachePageNo(0);
		confirmPageInfo.setPageSize(EVENT_PAGE_SIZE);
		confirmPageInfo.setLastPage(false);
		confirmPageInfo.setEventType(-1);
		new PageInfoObjectDao().insert(confirmPageInfo);

        // 初始化拍照留证列表pageInfo
        PageInfoObject eventCluePageInfo = new PageInfoObject();
        eventCluePageInfo.setTypeId(ID_EVENT_CLUE);
        eventCluePageInfo.setPageNo(0);
        eventCluePageInfo.setReadCachePageNo(0);
        eventCluePageInfo.setPageSize(EVENT_PAGE_SIZE);
        eventCluePageInfo.setLastPage(false);
        eventCluePageInfo.setEventType(EventType.ET_CLUE.getNumber());
        new PageInfoObjectDao().insert(eventCluePageInfo);

    }
}
