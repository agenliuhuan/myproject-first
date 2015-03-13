package mobi.dlys.android.core.mvc;

import mobi.dlys.android.core.utils.HandlerUtils.MessageListener;
import mobi.dlys.android.core.utils.HandlerUtils.StaticHandler;
import android.app.Activity;
import android.os.Bundle;
import android.os.Message;

public class BaseActivity extends Activity implements MessageListener, MessageOperator {
	protected Activity mActivity = null;
	protected StaticHandler mHandler = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mActivity = this;
		mHandler = new StaticHandler(this);
		BaseController.getInstance().addOutboxHandler(mHandler);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BaseController.getInstance().removeOutboxHandler(mHandler);
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onPause() {
		super.onPause();

	}

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
			BaseController.getInstance().sendMessage(msg.what, msg.arg1, msg.arg2, msg.obj);
		}
	}

	public void removeMessage(int what) {
		BaseController.getInstance().removeOutboxMessages(what);
	}

	@Override
	public void handleMessage(Message msg) {

	}

	@Override
	public void notifyOutboxHandlers(int what) {
		BaseController.getInstance().notifyOutboxHandlers(what);
	}

	@Override
	public void notifyOutboxHandlers(int what, int arg1, int arg2, Object obj) {
		BaseController.getInstance().notifyOutboxHandlers(what, arg1, arg2, obj);
	}

	@Override
	public void notifyOutboxHandlers(Message message) {
		BaseController.getInstance().notifyOutboxHandlers(message);
	}
}
