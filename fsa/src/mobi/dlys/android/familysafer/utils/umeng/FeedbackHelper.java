package mobi.dlys.android.familysafer.utils.umeng;

import android.content.Context;

import com.umeng.fb.FeedbackAgent;

public class FeedbackHelper {

	private static FeedbackHelper mInstance = null;

	FeedbackAgent mAgent;

	public static FeedbackHelper getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new FeedbackHelper(context);
		}

		return mInstance;
	}

	public void sync() {
		// to sync the default conversation. If there is new reply, there
		// will be notification in the status bar. If you do not want
		// notification, you can use
		// agent.getDefaultConversation().sync(listener);
		// instead.

		mAgent.sync();
	}

	/**
	 * 调用友盟反馈组件
	 * 
	 * @param context
	 */
	public void startFeedback() {
		mAgent.startFeedbackActivity();
	}

	private FeedbackHelper(Context context) {
		mAgent = new FeedbackAgent(context);
	}

}
