package mobi.dlys.android.core.async;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import android.content.Context;

/**
 * 简单的异步任务，仅仅指定返回结果的类型，不可输入参数
 * 
 * @author 2014-2-23下午8:57:55
 */
public abstract class SimpleCachedTask<T extends Serializable> extends
		CachedTask<Object, Object, T> {
	public SimpleCachedTask(Context context, String key, long cacheTime,
			TimeUnit unit) {
		super(context, key, cacheTime, unit);
	}

	@Override
	protected T doConnectNetwork(Object... params) {
		return doConnectNetwork();
	}

	protected abstract T doConnectNetwork();
}
