/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    http://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */

package mobi.dlys.android.core.net.http.handler;

import java.io.BufferedInputStream;
import java.io.IOException;

import mobi.dlys.android.core.net.http.client.AsyncHttpClient;
import mobi.dlys.android.core.utils.LogUtils;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Used to intercept and handle the responses from requests made using
 * {@link AsyncHttpClient}. The {@link #onSuccess(String)} method is designed to
 * be anonymously overridden with your own response handling code.
 * <p>
 * Additionally, you can override the {@link #onFailure(Throwable, String)},
 * {@link #onStart()}, and {@link #onFinish()} methods as required.
 * <p>
 * For example:
 * <p>
 * 
 * <pre>
 * AsyncHttpClient client = new AsyncHttpClient();
 * client.get(&quot;http://www.google.com&quot;, new AsyncHttpResponseHandler() {
 * 	&#064;Override
 * 	public void onStart() {
 * 		// Initiated the request
 * 	}
 * 
 * 	&#064;Override
 * 	public void onSuccess(String response) {
 * 		// Successfully got a response
 * 	}
 * 
 * 	&#064;Override
 * 	public void onFailure(Throwable e, String response) {
 * 		// Response failed :(
 * 	}
 * 
 * 	&#064;Override
 * 	public void onFinish() {
 * 		// Completed the request (either success or failure)
 * 	}
 * });
 * </pre>
 */
public class AsyncHttpResponseHandler {
	protected static final int SUCCESS_MESSAGE = 0;

	protected static final int FAILURE_MESSAGE = 1;

	protected static final int START_MESSAGE = 2;

	protected static final int FINISH_MESSAGE = 3;

	protected static final int PROGRESS_MESSAGE = 4;

	private static final String TAG = "AsyncHttpResponseHandler";

	private Handler handler;

	protected Object mFlag;

	/**
	 * Creates a new AsyncHttpResponseHandler
	 */
	public AsyncHttpResponseHandler() {
		// Set up a handler to post events back to the correct thread if
		// possible
		if (Looper.myLooper() != null) {
			handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					AsyncHttpResponseHandler.this.handleMessage(msg);
				}
			};
		}
	}

	//
	// Callbacks to be overridden, typically anonymously
	//

	/**
	 * Fired when the request is started, override to handle in your own code
	 */
	public void onStart() {
	}

	/**
	 * Fired in all cases when the request is finished, after both success and
	 * failure, override to handle in your own code
	 */
	public void onFinish() {
	}

	/**
	 * 当文件加载进度发生改变时调用这个方法。
	 * 
	 * @param contentLength
	 *            响应流的大小
	 * @param loadedLength
	 *            当前已读取的大小。
	 */
	public void onProgressChanged(long contentLength, long loadedLength) {

	}

	/**
	 * Fired when a request returns successfully, override to handle in your own
	 * code
	 * 
	 * @param content
	 *            the body of the HTTP response from the server
	 */
	public void onSuccess(String content) {
	}

	/**
	 * Fired when a request returns successfully, override to handle in your own
	 * code
	 * 
	 * @param statusCode
	 *            the status code of the response
	 * @param headers
	 *            the headers of the HTTP response
	 * @param content
	 *            the body of the HTTP response from the server
	 */
	public void onSuccess(int statusCode, Header[] headers, String content) {
		onSuccess(statusCode, content);
	}

	/**
	 * Fired when a request returns successfully, override to handle in your own
	 * code
	 * 
	 * @param statusCode
	 *            the status code of the response
	 * @param content
	 *            the body of the HTTP response from the server
	 */
	public void onSuccess(int statusCode, String content) {
		onSuccess(content);
	}

	/**
	 * Fired when a request fails to complete, override to handle in your own
	 * code
	 * 
	 * @param error
	 *            the underlying cause of the failure
	 * @deprecated use {@link #onFailure(Throwable, String)}
	 */
	@Deprecated
	public void onFailure(Throwable error) {
	}

	/**
	 * Fired when a request fails to complete, override to handle in your own
	 * code
	 * 
	 * @param error
	 *            the underlying cause of the failure
	 * @param content
	 *            the response body, if any
	 */
	public void onFailure(Throwable error, String content) {
		// By default, call the deprecated onFailure(Throwable) for
		// compatibility
		onFailure(error);
	}

	//
	// Pre-processing of messages (executes in background threadpool thread)
	//

	public void sendSuccessMessage(int statusCode, Header[] headers,
			String responseBody) {
		sendMessage(obtainMessage(SUCCESS_MESSAGE,
				new Object[] { Integer.valueOf(statusCode), headers,
						responseBody }));
	}

	public void sendFailureMessage(Throwable e, String responseBody) {
		sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[] { e,
				responseBody }));
	}

	public void sendFailureMessage(Throwable e, byte[] responseBody) {
		sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[] { e,
				responseBody }));
	}

	public void sendStartMessage() {
		sendMessage(obtainMessage(START_MESSAGE, null));
	}

	public void sendFinishMessage() {
		sendMessage(obtainMessage(FINISH_MESSAGE, null));
	}

	public void sendProgressChangeMessage(long total, long loaded) {
		sendMessage(obtainMessage(PROGRESS_MESSAGE,
				new long[] { total, loaded }));
	}

	//
	// Pre-processing of messages (in original calling thread, typically the UI
	// thread)
	//

	protected void handleSuccessMessage(int statusCode, Header[] headers,
			String responseBody) {
		onSuccess(statusCode, headers, responseBody);
	}

	protected void handleFailureMessage(Throwable e, String responseBody) {
		onFailure(e, responseBody);
	}

	// Methods which emulate android's Handler and Message methods
	protected void handleMessage(Message msg) {
		Object[] response;

		switch (msg.what) {
		case SUCCESS_MESSAGE:
			response = (Object[]) msg.obj;
			handleSuccessMessage(((Integer) response[0]).intValue(),
					(Header[]) response[1], (String) response[2]);
			break;
		case FAILURE_MESSAGE:
			response = (Object[]) msg.obj;
			handleFailureMessage((Throwable) response[0], (String) response[1]);
			break;
		case START_MESSAGE:
			onStart();
			break;
		case FINISH_MESSAGE:
			onFinish();
			break;
		case PROGRESS_MESSAGE:
			long[] progress = (long[]) msg.obj;
			onProgressChanged(progress[0], progress[1]);
			break;
		}
	}

	protected void sendMessage(Message msg) {
		if (handler != null) {
			handler.sendMessage(msg);
		} else {
			handleMessage(msg);
		}
	}

	protected Message obtainMessage(int responseMessage, Object response) {
		Message msg = null;
		if (handler != null) {
			msg = this.handler.obtainMessage(responseMessage, response);
		} else {
			msg = Message.obtain();
			msg.what = responseMessage;
			msg.obj = response;
		}
		return msg;
	}

	// Interface to AsyncHttpRequest
	public void sendResponseMessage(HttpResponse response,
			HttpUriRequest request) {
		StatusLine status = response.getStatusLine();
		String responseBody = null;
		BufferedInputStream in = null;
		int responseCode = status.getStatusCode();

		if (200 != responseCode) {
			try {
				printRequest(request.getURI().toString(), "",
						EntityUtils.toString(response.getEntity()));
			} catch (Exception e) {
			}

			HttpResponseException exception = new HttpResponseException(
					status.getStatusCode(), status.getStatusCode() + " "
							+ status.getReasonPhrase());
			sendFailureMessage(exception, responseBody);
			return;
		}

		long length = 0;
		Header[] headers = response.getAllHeaders();
		Header[] contentLength = response.getHeaders("Content-Length");
		if (null != contentLength && contentLength.length > 0) {
			String c = contentLength[0].getName();
			String v = contentLength[0].getValue();
			if (v != null) {
				v = v.trim();
				try {
					length = Integer.parseInt(v);
				} catch (NumberFormatException e) {
					length = 0;
				}
			}
		}
		// LogUtils.log(TAG, "response code:" + responseCode +
		// " response content length:" + length);

		HttpEntity temp = response.getEntity();
		if (null != temp) {
			try {
				in = new BufferedInputStream(temp.getContent());
				// if (length > 0) {
				int len = -1;
				byte[] b = new byte[1024];
				StringBuffer sb = new StringBuffer();
				while ((len = in.read(b)) != -1) {
					String str = new String(b, 0, len);
					sb.append(str);
				}
				responseBody = sb.toString();

				printRequest(request.getURI().toString(), "", responseBody);
				// }
			} catch (IOException e) {
				printRequest(request.getURI().toString(), "", e.getMessage());
				sendFailureMessage(e, (String) null);
				return;
			} finally {
				try {
					if (null != in) {
						in.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

		sendSuccessMessage(status.getStatusCode(), response.getAllHeaders(),
				responseBody);
	}

	private void printRequest(String request, String postBody, String response) {
		LogUtils.d("response", " \n");
		LogUtils.d("response", " \n");
		LogUtils.w("response", "********************************************");
		LogUtils.d("response", "request:\n" + request);
		LogUtils.d("response", "response:\n" + response);
		LogUtils.w("response", "********************************************");
	}

	public void setFlag(Object flag) {
		mFlag = flag;
	}

	public Object getFlag() {
		return mFlag;
	}
}
