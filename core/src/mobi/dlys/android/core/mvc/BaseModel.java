package mobi.dlys.android.core.mvc;

import android.os.Message;

public class BaseModel implements MessageOperator {

	@Override
	public void sendEmptyMessage(int what) {
		BaseController.getInstance().sendEmptyMessage(what);
	}

	@Override
	public void sendMessage(int what, int arg1, int arg2, Object obj) {
		BaseController.getInstance().sendMessage(what, arg1, arg2, obj);
	}

	@Override
	public void sendMessage(Message msg) {
		if (msg != null) {
			BaseController.getInstance().sendMessage(msg.what, msg.arg1,
					msg.arg2, msg.obj);
		}
	}

	public boolean handleMessage(Message msg) {
		return false;
	}

	@Override
	public void notifyOutboxHandlers(int what) {
		BaseController.getInstance().notifyOutboxHandlers(what);
	}

	@Override
	public void notifyOutboxHandlers(int what, int arg1, int arg2, Object obj) {
		BaseController.getInstance()
				.notifyOutboxHandlers(what, arg1, arg2, obj);
	}

	@Override
	public void notifyOutboxHandlers(Message message) {
		BaseController.getInstance().notifyOutboxHandlers(message);
	}
}
