package mobi.dlys.android.core.utils;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;

/**
 * Handler工具 1.生成一个唯一的ID，App全局唯一，这样可以防止可能发生的消息ID重复而导致的各种问题。 2.静态Handler接口，
 * 用来消除Handle可能导致的泄漏。
 * 
 */
public class HandlerUtils {

	private static int mId = 0x1000000;

	public interface MessageListener {
		public void handleMessage(Message msg);
	}

	/**
	 * 生成一个唯一的Message ID，App全局唯一，这样可以防止可能发生的消息ID重复而导致的各种问题。
	 * 
	 * @return
	 */
	public static final int generateId() {
		return ++mId;
	}

	/**
	 * 用来消除Handle可能导致的泄漏
	 * 
	 * @author
	 * 
	 */
	public static class StaticHandler extends Handler {
		WeakReference<MessageListener> listener;

		/**
		 * 
		 * @param listener
		 *            必读: 此listener必须由Activity实现该接口(推荐)或者是宿主Activity的类成员 :
		 *            这里是弱引用, 不会增加变量的引用计数, 使用匿名变量会导致listener过早释放(请参考此类的引用示例)
		 */
		public StaticHandler(MessageListener listener) {
			this.listener = new WeakReference<MessageListener>(listener);
		}

		public StaticHandler() {
		}

		@Override
		public void handleMessage(Message msg) {
			MessageListener listener = this.listener.get();
			if (listener != null) {
				listener.handleMessage(msg);
			}
		}
	}

}
