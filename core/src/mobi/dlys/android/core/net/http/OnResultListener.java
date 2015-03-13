package mobi.dlys.android.core.net.http;

import org.apache.http.Header;
import org.apache.http.client.HttpResponseException;

/**
 * Http请求结果对调对象， 根据不同的请求结果，请实现不同的方法. 这些方法都是在主线程中会掉的。
 * 
 * @author YangQiang
 * 
 */
public abstract class OnResultListener {

	/**
	 * 请求成功后，回调该方法。
	 * 
	 * @param responseCode
	 * @param headers
	 * @param result
	 *            返回的结果，根据你请求的类型返回不用Object
	 * @param flag
	 */
	public void onSuccess(int responseCode, Header[] headers, Object result,
			Object flag) {

	}

	/**
	 * Http请求失败时回调. Notice: This method is called on UI thread.
	 * 
	 * @param error
	 * @param flag
	 */
	public void onFailure(Throwable error, Object flag) {

	}

	public void onProgressChanged(long total, long loaded) {

	}

	/**
	 * 如果onFailure被回调，则可以在onFailure中调用该方法。
	 * 如果失败的原因是HttpResponseException，则可以调用该方法用于获取当前请求的响应码。
	 * 
	 * @param error
	 *            异常对象
	 * @return 返回响应码；如果返回-1，表示该异常不是HttpResponseException。
	 */
	public int getResponseCode(Throwable error) {
		if (error instanceof HttpResponseException) {
			HttpResponseException e = (HttpResponseException) error;
			return e.getStatusCode();
		} else {
			return -1;
		}
	}
}
