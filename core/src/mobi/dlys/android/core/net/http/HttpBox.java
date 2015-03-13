package mobi.dlys.android.core.net.http;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.core.net.extendcmp.ojm.OJM;
import mobi.dlys.android.core.net.extendcmp.ojm.OJMFactory;
import mobi.dlys.android.core.net.extendcmp.ojm.TypedToken;
import mobi.dlys.android.core.net.http.client.AsyncHttpClient;
import mobi.dlys.android.core.net.http.handler.AsyncHttpResponseHandler;
import mobi.dlys.android.core.net.http.handler.BinaryHttpResponseHandler;
import mobi.dlys.android.core.net.http.handler.DownloadHttpResponseHandler;
import mobi.dlys.android.core.net.http.request.PersistentCookieStore;
import mobi.dlys.android.core.net.http.request.RequestParams;
import mobi.dlys.android.core.utils.AndroidConfig;
import mobi.dlys.android.core.utils.LogUtils;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;

import android.os.Handler;
import android.text.TextUtils;

/**
 * 
 * HTTP异步请求框架对外接口.
 * 通过getInstance获取HttpBox实例，然后调用getxxx方法发送请求，然后等待回调即可，所有的回调都在主线程中进行。
 * 
 * 关于TypedToken，查看其注释。
 * 
 * 已加入的公共请求参数：productId，peerId，imei，cv等信息。
 * 如果不想让框架加入这些默认的参数，请将param参数设为null，或者put一个key为{@link HttpBox.KEY_NO_DEFAULT}
 * 的Item。
 * 
 * params.put("productId", AndroidConfig.getProductId());
 * 
 * params.put("peerId", AndroidConfig.getPeerid());
 * 
 * params.put("imei", AndroidConfig.getIMEI());
 * 
 * params.put("cv", AndroidConfig.getVersion());//客户端版本名
 * 
 * params.put("cvc", String.valueOf(AndroidConfig.getVersionCode())); //客户端版本号
 * 
 * params.put("ov", String.valueOf(AndroidConfig.getAndroidVersion()));
 * //OS版本、API level
 * 
 * params.put("device", AndroidConfig.getDeviceInfo()); //手机品牌和型号
 * 
 * params.put("ts", String.valueOf(System.currentTimeMillis())); //当前时间戳
 * 
 * params.put("channelId", AndroidConfig.getPartnerId()); //渠道ID
 * 
 * @author YangQiang
 * 
 */
public class HttpBox {

	public static final String CONTENT_TYPE_JSON = "application/json";
	public static final String CONTENT_TYPE_TEXT = "";

	private static final String KEY_NO_DEFAULT = "key_no_default_param";
	protected static final String TAG = HttpBox.class.getSimpleName();

	private static HttpBox mInstance = new HttpBox();

	private AsyncHttpClient mClient;
	private PersistentCookieStore cookieStore;

	private final int DEFAULT_CONNECTION_TIMEOUT = 60 * 1000;
	private final int DEFAULT_SOTIMEOUT = 120 * 1000;

	private HttpBox() {
		try {
			mClient = new AsyncHttpClient();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		cookieStore = new PersistentCookieStore(AndroidConfig.getContext());
		mClient.setCookieStore(cookieStore);
	}

	public static HttpBox getInstance() {
		return mInstance;
	}

	public static void release() {
		mInstance.close();
		mInstance = null;
	}

	private void close() {
		mClient = null;
	}

	// public void getString(String url, RequestParams params, Header[] headers,
	// Object flag, final OnResultListener listener) {
	// params = initRequestParams(params);
	// url = rebuildURL(url);
	// LogUtils.d(TAG, "GET String URL:" + url + " params:" + (null != params ?
	// params.getParamString() : ""));
	// mClient.get(null, url, headers, params,
	// buildAsyncHttpResponseHandler(flag, listener));
	// }

	public void getBytes(String url, RequestParams params, Header[] headers,
			Object flag, final OnResultListener listener) {
		params = initRequestParams(params);
		url = rebuildURL(url);
		LogUtils.d(TAG, "GET Bytes URL:" + url + " params:"
				+ (null != params ? params.getParamString() : ""));
		mClient.get(null, url, headers, params,
				buildBinaryHttpResponseHandler(flag, listener));
	}

	public void getJson(String url, final TypedToken token,
			RequestParams params, Header[] headers, Object flag,
			final OnResultListener listener) {
		params = initRequestParams(params);
		url = rebuildURL(url);
		LogUtils.d(TAG, "GET Json URL:" + url + " params:"
				+ (null != params ? params.getParamString() : ""));
		mClient.get(null, url, headers, params,
				buildJsonHttpResponseHandler(token, flag, listener));
	}

	public void getDownload(String url, RequestParams params, String savePath,
			Object flag, final OnResultListener listener) {
		params = initRequestParams(params);
		url = rebuildURL(url);
		LogUtils.d(TAG, "GET Download URL:" + url + " params:"
				+ (null != params ? params.getParamString() : ""));
		// 这里做下载，比较耗时，所以做一个单独的httpClient。如果继续用这个单列的httpclient的话，会导致超时，或者CPU占用率非常高。
		AsyncHttpClient client;
		try {
			client = new AsyncHttpClient();
			client.get(url, params,
					buildDownloadHttpResponseHandler(savePath, flag, listener));
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 发送post请求。 post的包体内容可以通过entity封装。 如果post字符串，可以使用StringEntity；
	 * 
	 * @param url
	 *            请求的url，通用的参数可以可以不写，HttpBox有自动加入。
	 * @param headers
	 *            请求Headers
	 * @param entity
	 *            POST包体
	 * @param contentType
	 *            POST包体格式
	 * @param flag
	 *            回调传回的标记
	 * @param token
	 *            如果响应是Json，设置这个参数，可将响应Json自动解析为这个参数描述的对象。
	 * @param listener
	 *            回调接口
	 */
	public void post(String url, Header[] headers, HttpEntity entity,
			String contentType, Object flag, final TypedToken token,
			final OnResultListener listener) {
		post(url, headers, entity, new RequestParams(), flag, token, listener);
	}

	/**
	 * 发送HTTP POST 请求，并且将响应内容为Json，将Json封装为token类型指定的对象。
	 * 
	 * @param url
	 * @param token
	 *            将响应Json解析为这个参数描述的对象。
	 * @param params
	 *            请求参数。
	 * @param flag
	 * @param listener
	 * 
	 * @see
	 */
	public void post(String url, final TypedToken token, RequestParams params,
			Object flag, final OnResultListener listener) {
		post(url, null, "", params, flag, token, listener);
	}

	/**
	 * 发送post请求。 post的包体内容可以通过entity封装。 如果post字符串，可以使用StringEntity；
	 * 
	 * @param url
	 *            请求的url，通用的参数可以可以不写，HttpBox有自动加入。
	 * @param headers
	 *            请求Headers
	 * @param postBody
	 *            POST包体字符串
	 * @param params
	 *            请求行附带的参数
	 * @param flag
	 *            回调传回的标记
	 * @param token
	 *            如果响应是Json，设置这个参数，可将响应Json自动解析为这个参数描述的对象。
	 * @param listener
	 *            回调接口
	 */
	public void post(String url, Header[] headers, String postBody,
			RequestParams params, Object flag, final TypedToken token,
			final OnResultListener listener) {
		StringEntity entity = null;
		try {
			if (!TextUtils.isEmpty(postBody)) {
				entity = new StringEntity(postBody, "UTF-8");
				LogUtils.d("response", " \n");
				LogUtils.d("response", " \n");
				LogUtils.w("response",
						"********************************************");
				LogUtils.d("response", "body:\n" + postBody);
				LogUtils.w("response",
						"********************************************");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		post(url, headers, entity, params, flag, token, listener);
	}

	/**
	 * 发送post请求。 post的包体内容可以通过entity封装。 如果post字符串，可以使用StringEntity；
	 * 
	 * @param url
	 *            请求的url，通用的参数可以可以不写，HttpBox有自动加入。
	 * @param headers
	 *            请求Headers
	 * @param entity
	 *            POST包体
	 * @param params
	 *            请求行附带的参数
	 * @param flag
	 *            回调传回的标记
	 * @param token
	 *            如果响应是Json，设置这个参数，可将响应Json自动解析为这个参数描述的对象。
	 * @param listener
	 *            回调接口
	 */
	public void post(String url, Header[] headers, HttpEntity entity,
			RequestParams params, Object flag, final TypedToken token,
			final OnResultListener listener) {
		post(url, headers, entity, params, CONTENT_TYPE_JSON, flag, token,
				DEFAULT_CONNECTION_TIMEOUT, DEFAULT_SOTIMEOUT, listener);
	}

	/**
	 * 发送post请求。 post的包体内容可以通过entity封装。 如果post字符串，可以使用StringEntity；
	 * 
	 * @param url
	 *            请求的url，通用的参数可以可以不写，HttpBox有自动加入。
	 * @param headers
	 *            请求Headers
	 * @param entity
	 *            POST包体
	 * @param params
	 *            请求行附带的参数
	 * @param flag
	 *            回调传回的标记
	 * @param token
	 *            如果响应是Json，设置这个参数，可将响应Json自动解析为这个参数描述的对象。
	 * @param connectionTimeout
	 *            请求连接超时
	 * @param soTimeout
	 *            响应读取超时
	 * @param listener
	 *            回调接口
	 */
	public void post(String url, Header[] headers, HttpEntity entity,
			RequestParams params, String contentType, Object flag,
			final TypedToken token, int connectionTimeout, int soTimeout,
			final OnResultListener listener) {
		RequestParams newparams = initRequestParams(params);
		String paramString = newparams.getParamString();
		if (paramString == null || paramString.length() <= 0) {
			url = rebuildURL(url);
		} else {
			url = rebuildURL(url) + "?" + paramString;
		}

		AsyncHttpClient client = null; // 如果包体比较大
		if (null != entity) {
			long length = entity.getContentLength();
			if (length > 1024 * 256) { // 256KB
				try {
					client = new AsyncHttpClient(); // 如果包体比较大
				} catch (KeyManagementException e) {
					e.printStackTrace();
				} catch (UnrecoverableKeyException e) {
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (KeyStoreException e) {
					e.printStackTrace();
				}
			} else {
				client = mClient;
			}
		} else {
			client = mClient;
		}

		StringBuffer cookieString = cookieStore.getCookieHeader(url);
		Header userHeader = null;
		List<Header> headerList = new ArrayList<Header>();
		if (null != headers) {
			for (int i = 0; i < headers.length; i++) {
				if ("Cookie".equals(headers[i].getName())) {
					userHeader = headers[i];
				} else {
					headerList.add(headers[i]);
				}
			}
		}

		if (null != userHeader) {
			String value = userHeader.getValue();
			cookieString.append(value);
		}

		Header header = new BasicHeader("Cookie", cookieString.toString());
		headerList.add(header);

		Header[] newHeaders = new Header[headerList.size()];
		for (int i = 0; i < headerList.size(); i++) {
			newHeaders[i] = headerList.get(i);
		}

		if (TextUtils.isEmpty(contentType)) {
			contentType = CONTENT_TYPE_JSON;
		}

		LogUtils.d(TAG, "POST URL:" + url + " entityContentLength:"
				+ (null != entity ? entity.getContentLength() : "null"));

		client.post(AndroidConfig.getContext(), url, newHeaders, entity,
				contentType,
				buildJsonHttpResponseHandler(token, flag, listener),
				connectionTimeout, soTimeout);
	}

	/**
	 * 发送post请求。 post的包体内容可以通过entity封装。 如果post字符串，可以使用StringEntity；
	 * 
	 * @param url
	 *            请求的url，通用的参数可以可以不写，HttpBox有自动加入。
	 * @param headers
	 *            请求Headers
	 * @param params
	 *            请求行附带的参数
	 * @param flag
	 *            回调传回的标记
	 * @param token
	 *            如果响应是Json，设置这个参数，可将响应Json自动解析为这个参数描述的对象。
	 * @param connectionTimeout
	 *            请求连接超时
	 * @param soTimeout
	 *            响应读取超时
	 * @param listener
	 *            回调接口
	 */
	public void get(String url, Header[] headers, RequestParams params,
			Object flag, final TypedToken token, int connectionTimeout,
			int soTimeout, final OnResultListener listener) {
		RequestParams newparams = initRequestParams(params);
		url = rebuildURL(url);

		AsyncHttpClient client = mClient;

		StringBuffer cookieString = cookieStore.getCookieHeader(url);
		Header userHeader = null;
		List<Header> headerList = new ArrayList<Header>();
		if (null != headers) {
			for (int i = 0; i < headers.length; i++) {
				if ("Cookie".equals(headers[i].getName())) {
					userHeader = headers[i];
				} else {
					headerList.add(headers[i]);
				}
			}
		}

		if (null != userHeader) {
			String value = userHeader.getValue();
			cookieString.append(value);
		}

		Header header = new BasicHeader("Cookie", cookieString.toString());
		headerList.add(header);

		Header[] newHeaders = new Header[headerList.size()];
		for (int i = 0; i < headerList.size(); i++) {
			newHeaders[i] = headerList.get(i);
		}

		LogUtils.d(TAG, "GET URL:" + url + " params:"
				+ (null != params ? params.getParamString() : ""));
		client.get(AndroidConfig.getContext(), url, newHeaders, newparams,
				buildJsonHttpResponseHandler(token, flag, listener),
				connectionTimeout, soTimeout);
	}

	public static Header getHeader(Header[] headers, String name) {
		for (Header h : headers) {
			if (name.equalsIgnoreCase(h.getName())) {
				return h;
			}
		}
		return null;
	}

	private BinaryHttpResponseHandler buildBinaryHttpResponseHandler(
			Object flag, final OnResultListener listener) {
		BinaryHttpResponseHandler handler = new BinaryHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, byte[] bytes) {
				if (null != listener) {
					listener.onSuccess(statusCode, null, bytes, getFlag());
				}
			}

			@Override
			public void onFailure(Throwable error, String content) {
				if (null != listener) {
					listener.onFailure(error, getFlag());
				}
			}
		};
		handler.setFlag(flag);
		return handler;
	}

	private AsyncHttpResponseHandler buildJsonHttpResponseHandler(
			final TypedToken token, final Object flag,
			final OnResultListener listener) {
		AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					String content) {

				LogUtils.d(TAG,
						"AsyncHttpResponseHandler.onSuccess ResponseBody: \n  "
								+ content + "\n\n");

				if (null == token) {
					listener.onSuccess(statusCode, headers, content, getFlag());
					return;
				}

				OJM ojm = OJMFactory.createOJM();
				Object o;
				try {
					o = ojm.fromJson(content, token);

					try {
						Class<?> c = Class
								.forName("com.xunlei.router.protocol.util.BaseResponseMessage");
						if (c.isInstance(o)) {
							Field flagField = c.getField("flag");
							if (flagField != null) {
								flagField.setAccessible(true);
								flagField.set(o, flag);
							}
						}
					} catch (Exception e) {
						LogUtils.d(TAG,
								"token type is not a BaseReponseMessage, ignore to set flag param.");
					}

					listener.onSuccess(statusCode, headers, o, getFlag());
				} catch (Exception e) {
					onFailure(e, content);
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Throwable error, String content) {
				listener.onFailure(error, getFlag());
			}
		};
		handler.setFlag(flag);
		return handler;
	}

	private DownloadHttpResponseHandler buildDownloadHttpResponseHandler(
			String savePath, Object flag, final OnResultListener listener) {
		DownloadHttpResponseHandler handler = new DownloadHttpResponseHandler(
				savePath) {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					String savePath) {
				if (null != listener) {
					listener.onSuccess(statusCode, headers, savePath, getFlag());
				}
			}

			@Override
			public void onFailure(Throwable error, String content) {
				if (null != listener) {
					listener.onFailure(error, getFlag());
				}
			}

			@Override
			public void onProgressChanged(long contentLength, long loadedLength) {
				if (null != listener) {
					listener.onProgressChanged(contentLength, loadedLength);
				}
			}
		};
		handler.setFlag(flag);
		return handler;
	}

	private String rebuildURL(String url) {
		return url.replaceAll(" ", "");
	}

	/**
	 * 发送Post请求，请求结束或者出现异常后，会通过Handler发送消息。
	 * 
	 * @param handler
	 * @param msgIdOnEnd
	 * @param msgObjType
	 * @param url
	 * @param headers
	 * @param entity
	 * @param params
	 * @param contentType
	 * @param flag
	 * @param connectionTimeout
	 * @param soTimeout
	 */
	public void post(final Handler handler, final int msgIdOnEnd,
			final TypedToken msgObjType, String url, Header[] headers,
			HttpEntity entity, RequestParams params, String contentType,
			Object flag, int connectionTimeout, int soTimeout) {
		if (null == contentType) {
			contentType = CONTENT_TYPE_JSON;
		}
		HttpBox.getInstance().post(url, headers, entity, params, contentType,
				flag, msgObjType, connectionTimeout, soTimeout,
				new OnResultListener() {

					@Override
					public void onSuccess(int responseCode, Header[] headers,
							Object result, Object flag) {

						ResponseObj obj = new ResponseObj();
						obj.obj = result;
						obj.flag = flag;
						handler.obtainMessage(msgIdOnEnd, 0, 0, obj)
								.sendToTarget();
					}

					@Override
					public void onFailure(Throwable error, Object flag) {
						ResponseObj obj = new ResponseObj();
						obj.obj = error.getMessage();
						obj.flag = flag;
						handler.obtainMessage(msgIdOnEnd, -1, 0, obj)
								.sendToTarget();
					}
				});
	}

	private String uid = null;

	private RequestParams initRequestParams(RequestParams params) {
		if (null == params || params.containskey(KEY_NO_DEFAULT)) {
			return new RequestParams();
		}

		params.put("imei", AndroidConfig.getIMEI());
		params.put("cvc", String.valueOf(AndroidConfig.getVersionCode())); // 客户端版本号
		params.put("ov", String.valueOf(AndroidConfig.getAndroidVersion())); // OS版本、API
																				// level
		params.put("device", AndroidConfig.getDeviceInfo()); // 手机品牌和型号

		// params.put("ts", String.valueOf(System.currentTimeMillis())); //
		// 当前时间戳
		// params.put("channelId", AndroidConfig.getPartnerId()); //渠道ID
		return params;
	}

	public void setUID(String uid) {
		this.uid = uid;
	}

	public static class ResponseObj {
		public Object obj;
		public Object flag;
	}

}
