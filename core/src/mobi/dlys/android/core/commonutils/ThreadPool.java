package mobi.dlys.android.core.commonutils;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPool {

	private static ThreadPool mInstance;
	private static final Object lock = new Object();

	private ThreadPoolExecutor executor;
	private int index = 0;

	private ThreadPool() {
		executor = (ThreadPoolExecutor) Executors
				.newCachedThreadPool(new ThreadFactory() {
					@Override
					public Thread newThread(Runnable r) {
						Thread thread = new Thread(r);
						thread.setName("XLPool-Thread-" + index);
						index++;
						return thread;
					}
				});
	}

	public static ThreadPool getInstance() {
		if (null == mInstance) {
			synchronized (lock) {
				if (null == mInstance) {
					mInstance = new ThreadPool();
				}
			}
		}
		return mInstance;
	}

	public ThreadPoolExecutor getDefaultThreadPoolExecutor() {
		return executor;
	}

}
