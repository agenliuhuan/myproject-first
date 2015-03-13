package mobi.dlys.android.core.mvc;

import mobi.dlys.android.core.utils.HandlerUtils.MessageListener;
import mobi.dlys.android.core.utils.HandlerUtils.StaticHandler;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;

public class BaseFragment extends Fragment implements MessageListener, MessageOperator {
	protected StaticHandler mHandler = null;

	protected View mRootView = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mHandler = new StaticHandler(this);
		BaseController.getInstance().addOutboxHandler(mHandler);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		BaseController.getInstance().removeOutboxHandler(mHandler);
	}

	protected View findViewById(int id) {
		View v = null;
		if (null != mRootView) {
			v = mRootView.findViewById(id);
		}
		return v;
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
