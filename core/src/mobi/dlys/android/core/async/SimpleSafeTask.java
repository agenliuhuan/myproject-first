package mobi.dlys.android.core.async;

/**
 * 简单的安全异步任务，仅仅指定返回结果的类型，不可输入参数
 * 
 * @author 2014-2-23下午8:57:55
 */
public abstract class SimpleSafeTask<T> extends SafeTask<Object, Object, T> {
	protected abstract T doInBackground() throws Exception;

	@Override
	protected void onPreExecuteSafely() throws Exception {
	}

	@Override
	protected T doInBackgroundSafely(Object... params) throws Exception {
		return doInBackground();
	}

	@Override
	protected void onPostExecuteSafely(T result, Exception e) throws Exception {
	}
}
