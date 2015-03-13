package mobi.dlys.android.familysafer.db.dao;

import java.util.List;

import mobi.dlys.android.familysafer.biz.vo.EventObjectEx;
import mobi.dlys.android.familysafer.biz.vo.event.EventContent;

public class EventContentDao extends AbstractDao<EventContent> {

	public EventContentDao() {
		super();
	}

	public static void getEventContent(List<EventObjectEx> list) {
		if (null == list) {
			return;
		}

		EventContentDao eventContentDao = new EventContentDao();
		SOSVoiceDao sosVoiceDao = new SOSVoiceDao();
		EventObjectEx eventObject;
		EventContent eventContent;

		if (null != list) {
			for (int i = 0; i < list.size(); i++) {
				eventObject = list.get(i);
				eventContent = eventContentDao.findById(eventObject.getContentId());
				if (null != eventContent) {
					eventContent.setSOSVoice(sosVoiceDao.findById(eventContent.getVoiceSosId()));
					eventObject.setContent(eventContent);
				}
			}
		}
	}

}
