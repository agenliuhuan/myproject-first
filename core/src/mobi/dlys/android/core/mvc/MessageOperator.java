package mobi.dlys.android.core.mvc;

import android.os.Message;

public interface MessageOperator {

	// 发送请求数据消息
	public void sendMessage(Message msg);

	public void sendEmptyMessage(int what);

	public void sendMessage(int what, int arg1, int arg2, Object obj);

	// 通知刷新ui
	public void notifyOutboxHandlers(int what);

	public void notifyOutboxHandlers(int what, int arg1, int arg2, Object obj);

	public void notifyOutboxHandlers(Message message);
}
