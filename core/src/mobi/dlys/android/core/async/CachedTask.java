package mobi.dlys.android.core.async;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import mobi.dlys.android.core.utils.LogUtils;
import android.content.Context;

/**
 * <p>
 * Cached AsyncTask 缓存异步任务
 * <p>
 * 它主要用于获取网络数据，给它一个缓存时间，只要未超时，它将先从本地获取，仅当超时或本地获取失败时才去真正联网完成。
 * <b>每个Task都必须有唯一标示：key，</b>它唯一标示一个缓存任务，不同的任务绝对不能一样，否则会混淆超时时间。 <b>
 * {@link CachedTask#Result} 需要序列化</b>否则不能或者不能完整的读取缓存。
 * 
 * @author 2014-2-23下午8:57:55
 */
public abstract class CachedTask<Params, Progress, Result extends Serializable>
		extends AsyncTask<Params, Progress, Result> {
	private static final String TAG = CachedTask.class.getSimpleName();
	private static final String DEFAULT_PATH = "/cachedtask_private";
	private long expiredTime = 0;
	private static String cachePath;
	private String key;
	private static ConcurrentHashMap<String, Long> cachedTimeMap = new ConcurrentHashMap<String, Long>();

	public static void cleanCacheFiles(Context context) {
		cachedTimeMap.clear();
		cachePath = context.getFilesDir().getAbsolutePath() + DEFAULT_PATH;
		File file = new File(cachePath);
		final File[] fileList = file.listFiles();
		if (fileList != null) {
			TaskExecutor.start(new Runnable() {
				@Override
				public void run() {
					for (File f : fileList) {
						if (f.isFile())
							f.delete();
					}
				}
			});

		}
	}

	/**
	 * @param context
	 *            app context
	 * @param key
	 *            identify label, each single cachedtask should not be the same.
	 * @param cacheTime
	 *            expired time
	 * @param unit
	 *            if timeunit is null, see cacheTime as millisecond.
	 */
	public CachedTask(Context context, String key, long cacheTime, TimeUnit unit) {
		if (context == null)
			throw new RuntimeException(
					"CachedTask Initialized Must has Context");
		cachePath = context.getFilesDir().getAbsolutePath() + DEFAULT_PATH;
		if (key == null)
			throw new RuntimeException("CachedTask Must Has Key for Search ");
		this.key = key;
		if (unit != null)
			expiredTime = unit.toMillis(cacheTime);
		else
			expiredTime = cacheTime;
	}

	protected abstract Result doConnectNetwork(Params... params);

	@Override
	protected final Result doInBackground(Params... params) {
		Result res = null;
		try {
			Long time = cachedTimeMap.get(key);
			long lastTime = time == null ? 0 : time;
			if (System.currentTimeMillis() - lastTime >= expiredTime) {
				res = doConnectNetwork(params);
				if (res != null) {
					LogUtils.d(TAG, "doConnectNetwork: sucess");
					cachedTimeMap.put(key, System.currentTimeMillis());
					saveResultToCache(res);
				} else {
					LogUtils.d(TAG, "doConnectNetwork: false");
					res = getResultFromCache();
				}
			} else {
				res = getResultFromCache();
				if (res == null) {
					res = doConnectNetwork(params);
					if (res != null) {
						LogUtils.d(TAG, "doConnectNetwork: sucess");
						cachedTimeMap.put(key, System.currentTimeMillis());
						saveResultToCache(res);
					} else {
						LogUtils.d(TAG, "doConnectNetwork: false");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	private Result getResultFromCache() {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(new File(cachePath,
					key)));
			Object obj = ois.readObject();

			if (obj != null) {
				LogUtils.d(TAG, "getResultFromCache: sucess");
				return (Result) obj;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ois != null)
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		LogUtils.d(TAG, "getResultFromCache: fail ");
		return null;
	}

	private boolean saveResultToCache(Result res) {
		ObjectOutputStream oos = null;
		try {
			File dir = new File(cachePath);
			if (!dir.exists())
				dir.mkdirs();
			oos = new ObjectOutputStream(new FileOutputStream(
					new File(dir, key)));
			oos.writeObject(res);
			LogUtils.d(TAG, "saveResultToCache: success");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (oos != null)
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		LogUtils.d(TAG, "saveResultToCache: fail");
		return false;
	}
}
