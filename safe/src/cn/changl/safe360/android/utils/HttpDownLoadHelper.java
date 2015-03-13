package cn.changl.safe360.android.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import cn.changl.safe360.android.App;
import cn.changl.safe360.android.ui.comm.YSToast;

/*
 * 网络下载助手类
 */
public class HttpDownLoadHelper {

	public static final String EXTRA_DOWNLOAD_FILE_URI = "download_file_uri";
	public static final String EXTRA_DOWNLOAD_FILE_LENGTH = "download_file_length";
	public static final String EXTRA_DOWNLOAD_FILE_PROGRESS = "download_file_progress";

	private final static int SOCKET_BUFFER_READ_WIFI = 1 * 1024; // WIFI缓冲大小
	private final static int SOCKET_BUFFER_READ_GPRS = 512; // GPRS缓冲大小
	private final static int SOCKET_BUFFER_WRITE_WIFI = 1 * 1024; // WIFI缓冲大小
	private final static int SOCKET_BUFFER_WRITE_GPRS = 512; // GPRS缓冲大小
	private final static int SOCKET_CONNECT_TIMEOUT_WIFI = 30 * 1000; // 连接超时
	private final static int SOCKET_CONNECT_TIMEOUT_GPRS = 60 * 1000; // 连接超时
	private final static int SOCKET_READ_TIMEOUT_WIFI = 60 * 1000; // WIFI读取超时
	private final static int SOCKET_READ_TIMEOUT_GPRS = 120 * 1000; // GPRS读取超时
	private final static int DOWNLOAD_TRY_COUNT = 1; // 重试次数

	/*
	 * 获取写缓冲大小
	 */
	public int getSocketBufferWriteMaxSize() {
		return !NetUtils.isWifiEnable() ? SOCKET_BUFFER_WRITE_GPRS + SOCKET_BUFFER_READ_GPRS : SOCKET_BUFFER_WRITE_WIFI + SOCKET_BUFFER_READ_WIFI;
	}

	/*
	 * 获取写缓冲警告大小
	 */
	public static int getSocketBufferWriteWarningSize() {
		return !NetUtils.isWifiEnable() ? SOCKET_BUFFER_WRITE_GPRS : SOCKET_BUFFER_WRITE_WIFI;
	}

	/*
	 * 获取读缓冲大小
	 */
	public static int getSocketBufferReadSize() {
		return !NetUtils.isWifiEnable() ? SOCKET_BUFFER_READ_GPRS : SOCKET_BUFFER_READ_WIFI;
	}

	/*
	 * 获取连接超时大小
	 */
	public static int getConnectTimeoutSize() {
		return !NetUtils.isWifiEnable() ? SOCKET_CONNECT_TIMEOUT_GPRS : SOCKET_CONNECT_TIMEOUT_WIFI;
	}

	/*
	 * 获取读取超时大小
	 */
	public static int getReadTimeoutSize() {
		return !NetUtils.isWifiEnable() ? SOCKET_READ_TIMEOUT_GPRS : SOCKET_READ_TIMEOUT_WIFI;
	}

	/*
	 * 下载
	 */
	public static boolean download(Context context, String fileuri, String filepath, String action) {
		boolean result = false;

		int tryTime = 0;
		while (tryTime <= DOWNLOAD_TRY_COUNT && !result) {
			HttpURLConnection httpURLConnection = null;
			InputStream inputStream = null;
			OutputStream outputStream = null;
			File file = null;

			try {
				if (NetUtils.NET_NOT_CONNECTION == NetUtils.checkNet()) {
					if (null != context) {
						YSToast.showToast(context, "网络错误，下载失败");
					}
					return result;
				}

				httpURLConnection = HttpUtils.getHttpURLConnection(fileuri);
				httpURLConnection.setConnectTimeout(getConnectTimeoutSize());
				httpURLConnection.setReadTimeout(getReadTimeoutSize());

				int code = httpURLConnection.getResponseCode();
				// 连接成功
				if (HttpURLConnection.HTTP_OK == code) {
					int contentLength = httpURLConnection.getContentLength();
					inputStream = httpURLConnection.getInputStream();
					if (null != inputStream) {
						file = new File(filepath);
						outputStream = new FileOutputStream(file);

						byte[] buffer = new byte[getSocketBufferReadSize()];
						int bufLen = buffer.length;
						int readLen = 0;
						int progress = 0;
						while ((readLen = inputStream.read(buffer)) > 0) {
							outputStream.write(buffer, 0, readLen);
							progress += readLen;

							if (!TextUtils.isEmpty(action) && (readLen % bufLen != 0 || readLen % (3 * bufLen) == 0)) {
								Intent intent = new Intent(action);
								intent.putExtra(EXTRA_DOWNLOAD_FILE_URI, fileuri);
								intent.putExtra(EXTRA_DOWNLOAD_FILE_LENGTH, contentLength);
								intent.putExtra(EXTRA_DOWNLOAD_FILE_PROGRESS, progress);
								App.getInstance().sendBroadcast(intent);
							}
						}
						outputStream.flush();
						buffer = null;

						// cmwap下不验证图片大小
						if ((NetUtils.isProxy && (filepath.endsWith(FileUtils.JPG) || filepath.endsWith(FileUtils.PNG))) || contentLength == file.length()) {
							// 完整文件
							result = true;
						} else {
							// 下载失败,删除文件
							FileUtils.delFile(file);
						}
					}
				} else {
				}
			} catch (Exception e) {
				e.printStackTrace();
				// 出现异常,删除文件
				result = false;
				FileUtils.delFile(file);
			} finally {
				try {
					if (null != inputStream) {
						inputStream.close();
						inputStream = null;
					}
					if (null != outputStream) {
						outputStream.close();
						outputStream = null;
					}
					if (null != httpURLConnection) {
						httpURLConnection.disconnect();
						httpURLConnection = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			tryTime += 1;
		}

		return result;
	}
}