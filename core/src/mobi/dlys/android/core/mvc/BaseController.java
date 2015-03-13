package mobi.dlys.android.core.mvc;

import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.core.utils.LogUtils;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

public class BaseController {
	private static final String TAG = BaseController.class.getSimpleName();

	private static BaseController mController = null;
	private BaseModel model = null;

	private final HandlerThread inboxHandlerThread;
	private final Handler inboxHandler;
	private final List<Handler> outboxHandlers = new ArrayList<Handler>();

	/*
	 * 获取模型单例
	 */
	public static BaseController getInstance() {
		if (null == mController) {
			mController = new BaseController();
		}

		return mController;
	}

	private BaseController() {
		this.model = null;
		inboxHandlerThread = new HandlerThread("BaseController Inbox");
		if (inboxHandlerThread != null) {
			inboxHandlerThread.start();

			inboxHandler = new Handler(inboxHandlerThread.getLooper()) {
				@Override
				public void handleMessage(Message msg) {
					BaseController.this.handleMessage(msg);
				}
			};
		} else {
			inboxHandler = null;
		}
	}

	public void setModel(BaseModel model) {
		this.model = model;
	}

	public final void dispose() {
		// ask the inbox thread to exit gracefully
		if (inboxHandlerThread != null) {
			inboxHandlerThread.getLooper().quit();
		}
	}

	private final Handler getInboxHandler() {
		return inboxHandler;
	}

	public final void addOutboxHandler(Handler handler) {
		if (outboxHandlers != null && !outboxHandlers.contains(handler)) {
			outboxHandlers.add(handler);
		}
	}

	public final void removeOutboxHandler(Handler handler) {
		if (outboxHandlers != null) {
			outboxHandlers.remove(handler);
		}
	}

	public void sendMessage(int what, int arg1, int arg2, Object obj) {
		Message msg = Message.obtain(inboxHandler, what, arg1, arg2, obj);
		if (msg != null) {
			msg.sendToTarget();
		}
	}

	public void sendEmptyMessage(int what) {
		if (inboxHandler != null) {
			inboxHandler.sendEmptyMessage(what);
		}
	}

	public void removeOutboxMessages(int what) {
		if (outboxHandlers == null) {
			return;
		}

		if (outboxHandlers.isEmpty()) {
		} else {
			for (Handler handler : outboxHandlers) {
				if (handler.hasMessages(what)) {
					handler.removeMessages(what);
				}
			}
		}
	}

	public final void notifyOutboxHandlers(int what) {
		if (outboxHandlers == null) {
			return;
		}

		if (outboxHandlers.isEmpty()) {
			LogUtils.d(TAG, String.format("No outbox handler to handle outgoing message (%d)", what));
		} else {
			for (Handler handler : outboxHandlers) {
				Message msg = Message.obtain(handler, what, 0, 0, null);
				msg.sendToTarget();
			}
		}
	}

	public final void notifyOutboxHandlers(int what, int arg1, int arg2, Object obj) {
		if (outboxHandlers == null) {
			return;
		}

		if (outboxHandlers.isEmpty()) {
			LogUtils.d(TAG, String.format("No outbox handler to handle outgoing message (%d)", what));
		} else {
			for (Handler handler : outboxHandlers) {
				Message msg = Message.obtain(handler, what, arg1, arg2, obj);
				msg.sendToTarget();
			}
		}
	}

	public final void notifyOutboxHandlers(Message message) {
		if (outboxHandlers == null) {
			return;
		}

		if (outboxHandlers.isEmpty()) {
			LogUtils.d(TAG, String.format("No outbox handler to handle outgoing message (%d)", message.what));
		} else {
			for (Handler handler : outboxHandlers) {
				Message msg = Message.obtain(handler, message.what, message.arg1, message.arg2, message.obj);
				msg.sendToTarget();
			}
		}
	}

	private void handleMessage(Message msg) {
		if (model == null) {
			return;
		}

		if (!model.handleMessage(msg)) {
			LogUtils.d(TAG, "Unknown message: " + msg);
		}
	}

	final BaseModel getModel() {
		return model;
	}

}
