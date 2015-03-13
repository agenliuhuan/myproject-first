package mobi.dlys.android.familysafer.player.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URLConnection;

import mobi.dlys.android.familysafer.player.download.DownloadManage.DownLoadResult;
import mobi.dlys.android.familysafer.player.download.DownloadManage.DownloadState;
import mobi.dlys.android.familysafer.player.utils.HttpUtil;
import mobi.dlys.android.familysafer.utils.DateUtils;
import mobi.dlys.android.familysafer.utils.FileUtils;
import mobi.dlys.android.familysafer.utils.NetUtils;
import android.content.Context;

/*
 * 网络下载
 */
public abstract class DownloadBase {
	public final static String ACTION_DOWNLOAD = "mobi.dlys.android.familysafer.download"; // 音乐ID
	public final static String ACTION_DOWNLOAD_MUSICID = "mobi.dlys.android.familysafer.download.musicid"; // 音乐ID
	public final static String ACTION_DOWNLOAD_TOTAL = "mobi.dlys.android.familysafer.download.total"; // 总大小
	public final static String ACTION_DOWNLOAD_CURRENT = "mobi.dlys.android.familysafer.download.current"; // 当前大小
	public final static String ACTION_DOWNLOAD_STATE = "mobi.dlys.android.familysafer.download.state"; // 下载状态
	public final static String ACTION_DOWNLOAD_RESULT = "mobi.dlys.android.familysafer.download.result"; // 下载结果
	public final static String PATH_TAG = "/";
	// public final static String BUFFER_TAG = ".dwl";
	public final static String ACCESS_FILE_MODE = "rwd";

	protected final static int SOCKET_BUFFER_READ_WIFI = 64 * 1024; // WIFI缓冲大小
	protected final static int SOCKET_BUFFER_READ_GPRS = 16 * 1024; // GPRS缓冲大小
	protected final static int SOCKET_BUFFER_WRITE_WIFI = 64 * 1024; // WIFI缓冲大小
	protected final static int SOCKET_BUFFER_WRITE_GPRS = 16 * 1024; // GPRS缓冲大小
	protected final static int SOCKET_CONNECT_TIMEOUT_WIFI = 2000; // 连接超时
	protected final static int SOCKET_CONNECT_TIMEOUT_GPRS = 5000; // 连接超时
	protected final static int SOCKET_READ_TIMEOUT_WIFI = 2000; // WIFI读取超时
	protected final static int SOCKET_READ_TIMEOUT_GPRS = 5000; // GPRS读取超时
	protected final static int DOWNLOAD_TRY_COUNT = 100; // 重试次数
	protected final static int REFRESH_DOWNLOAD_STATE_TIME = 1000; // 刷新状态时间

	/*
	 * 请求信息
	 */
	public class RequestProperty {
		public final static String DLID = "dlId";
	}

	/*
	 * 下载歌曲信息
	 */
	public static class DownLoadInfo {
		public String strName = "";
		public String strUrl = "";
		public int nSize = 0;// 文件大小
		public int nProgress = 0;// 进度
		public boolean bAcceptRanges = true; // 支持断点下载
		public int nDownloadState = 0;// 下载状态 -1下载失败0已下载 1下载中 2等待下载
		public int nDownLoadResult = DownLoadResult.WAIT; // 下载结果

		public static void clone(DownLoadInfo dest, DownLoadInfo src) {
			dest.strName = new String(src.strName);
			dest.strUrl = new String(src.strUrl);
			dest.nSize = src.nSize;
			dest.nProgress = src.nProgress;
			dest.bAcceptRanges = src.bAcceptRanges;
			dest.nDownloadState = src.nDownloadState;
			dest.nDownLoadResult = src.nDownLoadResult;
		}
	}

	/*
	 * 下载错误信息
	 */
	public class DownLoadError {
		public final static int FINISH = -2; // 已经完成下载
		public final static int RANGE_MODE = -2; // 断点续传模式
		public final static int NORMAL_MODE = -1; // 正常下载模式
		public final static int SUCCESS = 0; // 成功
		public final static int FAIL = 1; // 失败
		public final static int GET_URL_ERROR = 2; // 无法取得文件URL地址
		public final static int CONNECT_URL_ERROR = 3; // 连接文件URL地址失败
		public final static int SOCKET_ERROR = 4; // 网络出现异常
		public final static int FILE_SIZE_ERROR = 5; // 文件大小不一致
	}

	/*
	 * HTTP 头字段
	 */
	public class HeaderField {
		public final static String ACCEPT_RANGES = "accept-ranges", CONTENT_RANGE = "content-range", LOCATION = "location";
	}

	/*
	 * HTTP 头字段结果
	 */
	public class HeaderFieldResult {
		public final static String BYTES = "bytes", NONE = "none";
	}

	/*
	 * 下载状态
	 */
	public class CancelState {
		public final static int DOWNLOAD = 0, CANCEL = 1, DELETE = 2;
	}

	protected DownLoadInfo mDownLoadInfo = null; // 下载信息
	protected Context mContext = null;
	protected int mnCancelState = CancelState.DOWNLOAD; // 取消
	protected long mlTryCount = DOWNLOAD_TRY_COUNT;

	/*
	 * 构造
	 */
	public DownloadBase(Context context) {
		mDownLoadInfo = new DownLoadInfo();

		mContext = context;
	}

	/*
	 * 获取下载
	 */
	protected String getUrl() {
		return mDownLoadInfo.strUrl;
	}

	/*
	 * 广播下载状态
	 */
	protected abstract void sendDownloadState(DownLoadInfo downLoadInfo);

	/*
	 * 获取路径
	 */
	protected abstract String getPath();

	/*
	 * 获取缓冲路径
	 */
	protected String getName() {
		return mDownLoadInfo.strName;
	}

	/*
	 * 获取缓冲路径
	 */
	protected abstract String getTag();

	/*
	 * 获取缓冲后缀
	 */
	protected abstract String getBufferTag();

	/*
	 * 检查下载完成
	 */
	protected abstract boolean isFinish(long lLength);

	/*
	 * 获取完整下载路径
	 */
	protected String getFullPath() {
		return FileUtils.getFilePath(getPath(), getName(), getTag());
	}

	/*
	 * 获取完整缓冲路径
	 */
	protected String getFullBufferPath() {
		return FileUtils.getFilePath(getPath(), getName(), getBufferTag());
	}

	/*
	 * 设置重试次数
	 */
	public void setTryCount(long lCount) {
		mlTryCount = (lCount < 1) ? 1 : lCount;
	}

	/*
	 * 强制停止下载
	 */
	public void cancel(String strName) {
		if (mDownLoadInfo.strName.equals(strName)) {
			mnCancelState = CancelState.CANCEL;
		}
	}

	/*
	 * 强制删除
	 */
	public void delete(String strName) {
		if (mDownLoadInfo.strName.equals(strName)) {
			mnCancelState = CancelState.DELETE;
		}
	}

	/*
	 * 获取下载信息
	 */
	public DownLoadInfo getDownLoadInfo() {
		return mDownLoadInfo;
	}

	/*
	 * 中止下载
	 */
	public void forceCancel() {
		cancel(mDownLoadInfo.strName);
	}

	/*
	 * 退出
	 */
	public void exit() {
		cancel(mDownLoadInfo.strName);
	}

	/*
	 * 获取写缓冲大小
	 */
	protected int getSocketBufferWriteMaxSize() {
		return !NetUtils.isWifiEnable() ? SOCKET_BUFFER_WRITE_GPRS + SOCKET_BUFFER_READ_GPRS : SOCKET_BUFFER_WRITE_WIFI + SOCKET_BUFFER_READ_WIFI;
	}

	/*
	 * 获取写缓冲警告大小
	 */
	protected int getSocketBufferWriteWarningSize() {
		return !NetUtils.isWifiEnable() ? SOCKET_BUFFER_WRITE_GPRS : SOCKET_BUFFER_WRITE_WIFI;
	}

	/*
	 * 获取读缓冲大小
	 */
	protected int getSocketBufferReadSize() {
		return !NetUtils.isWifiEnable() ? SOCKET_BUFFER_READ_GPRS : SOCKET_BUFFER_READ_WIFI;
	}

	/*
	 * 获取连接超时大小
	 */
	protected int getConnectTimeoutSize() {
		return !NetUtils.isWifiEnable() ? SOCKET_CONNECT_TIMEOUT_GPRS : SOCKET_CONNECT_TIMEOUT_WIFI;
	}

	/*
	 * 获取读取超时大小
	 */
	protected int getReadTimeoutSize() {
		return !NetUtils.isWifiEnable() ? SOCKET_READ_TIMEOUT_GPRS : SOCKET_READ_TIMEOUT_WIFI;
	}

	/*
	 * 获取文件大小
	 */
	protected int getFileInfo() {
		HttpURLConnection httpURLConnection = null;
		String strAcceptRanges = "";
		File fileDownload = null;
		int nResult = DownLoadError.FAIL, nContentLength = 0;

		try {
			FileUtils.createDirectory(getPath());

			fileDownload = new File(getFullBufferPath());

			if (fileDownload.exists() && fileDownload.isFile()) {
				mDownLoadInfo.nProgress = (int) fileDownload.length();
			}

			httpURLConnection = HttpUtil.getHttpURLConnection(mDownLoadInfo.strUrl);

			httpURLConnection.setConnectTimeout(getConnectTimeoutSize());
			httpURLConnection.setReadTimeout(getReadTimeoutSize());
			nContentLength = httpURLConnection.getContentLength();

			strAcceptRanges = httpURLConnection.getHeaderField(HeaderField.ACCEPT_RANGES);

			if ((strAcceptRanges != null) && strAcceptRanges.equals(HeaderFieldResult.BYTES)) {
				mDownLoadInfo.bAcceptRanges = true;
			} else {
				mDownLoadInfo.bAcceptRanges = false;
			}

			httpURLConnection.disconnect();

			if (mDownLoadInfo.nSize <= 0) {
				mDownLoadInfo.nSize = nContentLength;
			}

			if (nContentLength > 0) {
				if ((mDownLoadInfo.nSize != nContentLength) || (mDownLoadInfo.nProgress > mDownLoadInfo.nSize)) {
					if (fileDownload.exists() && fileDownload.isFile()) {
						fileDownload.delete();
					}

					mDownLoadInfo.nSize = nContentLength;
					mDownLoadInfo.nProgress = 0;
				}

				if (mDownLoadInfo.nSize > 0) {
					if (isFinish(mDownLoadInfo.nSize)) {
						renameDownloadFile(false);

						nResult = DownLoadError.FINISH;
					} else {
						fileDownload = new File(getFullBufferPath());

						if (fileDownload.exists() && fileDownload.isFile() && (fileDownload.length() == mDownLoadInfo.nSize)) {
							nResult = DownLoadError.FINISH;
						} else {
							nResult = DownLoadError.SUCCESS;
						}
					}
				}
			}
		} catch (Exception e) {
			nResult = DownLoadError.FAIL;
		}

		return nResult;
	}

	/*
	 * 复制文件到下载目录
	 */
	protected void renameDownloadFile(boolean bFromSrc) {
		if (bFromSrc) {
			FileUtils.renameFile(getFullPath(), getFullBufferPath());
		} else {
			FileUtils.renameFile(getFullBufferPath(), getFullPath());
		}
	}

	/*
	 * 删除下载文件
	 */
	public void deleteDownloadFile(String strName) {
		File fileDelete = null;

		try {
			fileDelete = new File(getFullBufferPath());

			if (fileDelete.exists() && fileDelete.isFile()) {
				fileDelete.delete();
			}

			fileDelete = new File(getFullPath());

			if (fileDelete.exists() && fileDelete.isFile()) {
				fileDelete.delete();
			}
		} catch (Exception e) {
		}
	}

	/*
	 * 下载
	 */
	public int download(DownLoadInfo downLoadInfo) {
		String strUrl = "";
		int nDownloadState = DownloadState.FAIL, nDownloadError = DownLoadError.SUCCESS;

		DownLoadInfo.clone(mDownLoadInfo, downLoadInfo);

		mnCancelState = CancelState.DOWNLOAD;

		mDownLoadInfo.nDownloadState = DownloadState.DOWNLOAD;
		mDownLoadInfo.nDownLoadResult = DownLoadResult.START;

		sendDownloadState(mDownLoadInfo);

		for (int i = 0; i < mlTryCount; ++i) {
			mDownLoadInfo.nDownloadState = DownloadState.DOWNLOAD;
			mDownLoadInfo.nDownLoadResult = DownLoadResult.START;

			if (NetUtils.NET_NOT_CONNECTION == NetUtils.checkNet()) {
				mDownLoadInfo.nDownloadState = DownloadState.FAIL;
				mDownLoadInfo.nDownLoadResult = DownLoadResult.FAIL;

				break;
			} else {
				strUrl = getUrl();

				if (strUrl.length() > 0) {
					mDownLoadInfo.strUrl = new String(strUrl);

					nDownloadError = getFileInfo();

					if (DownLoadError.SUCCESS == nDownloadError) {
						if (mDownLoadInfo.bAcceptRanges) {
							nDownloadState = downloadRange();
						} else {
							nDownloadState = downloadNomal();
						}
					} else if (DownLoadError.FINISH == nDownloadError) {
						nDownloadState = DownLoadResult.SUCCESS;
						mDownLoadInfo.nDownloadState = DownloadState.FINISH;
					} else {
						nDownloadState = DownLoadResult.FAIL;
						mDownLoadInfo.nDownLoadResult = DownloadState.FAIL;
					}
				} else {
					mDownLoadInfo.nDownloadState = DownloadState.FAIL;
					mDownLoadInfo.nDownLoadResult = DownLoadResult.FAIL;
				}
			}

			if (mnCancelState == CancelState.CANCEL) {
				mDownLoadInfo.nDownloadState = DownloadState.PAUSE;
				mDownLoadInfo.nDownLoadResult = DownLoadResult.CANCEL;

				nDownloadState = mDownLoadInfo.nDownLoadResult;

				break;
			} else if (mnCancelState == CancelState.DELETE) {
				mDownLoadInfo.nDownloadState = DownloadState.FAIL;
				mDownLoadInfo.nDownLoadResult = DownLoadResult.CANCEL;

				nDownloadState = mDownLoadInfo.nDownLoadResult;

				deleteDownloadFile(getName());

				break;
			} else if (mDownLoadInfo.nDownloadState == DownloadState.FINISH) {
				renameDownloadFile(true);

				break;
			}

			try {
				Thread.sleep(500);
			} catch (Exception e) {
			}
		}

		DownLoadInfo.clone(downLoadInfo, mDownLoadInfo);

		sendDownloadState(mDownLoadInfo);

		return nDownloadState;
	}

	/*
	 * 断点续传
	 */
	public int downloadRange() {
		HttpURLConnection httpURLConnection = null;
		InputStream inputStream = null;
		RandomAccessFile fileDownload = null;
		int nRead = 0, nWrite = 0, nCounter = 0, nTotal = 0;
		byte byBuffer[] = null, byBufferWrite[] = null;
		long lTime = 0;

		try {
			byBuffer = new byte[getSocketBufferReadSize()];
			byBufferWrite = new byte[getSocketBufferWriteMaxSize()];

			nWrite = 0;
			nCounter = 0;
			nTotal = 0;

			httpURLConnection = HttpUtil.getHttpURLConnection(mDownLoadInfo.strUrl);

			httpURLConnection.setConnectTimeout(getConnectTimeoutSize());

			httpURLConnection.setReadTimeout(getReadTimeoutSize());

			httpURLConnection.setRequestProperty("Range", "bytes=" + mDownLoadInfo.nProgress + "-" + mDownLoadInfo.nSize);

			inputStream = httpURLConnection.getInputStream();

			if (null != inputStream) {
				fileDownload = new RandomAccessFile(getFullBufferPath(), ACCESS_FILE_MODE);

				fileDownload.seek(mDownLoadInfo.nProgress);

				lTime = DateUtils.currentTimeMillis();

				do {
					nRead = inputStream.read(byBuffer);

					if (nRead > 0) {
						for (int i = 0; i < nRead; ++i) {
							byBufferWrite[nWrite + i] = byBuffer[i];
						}

						nWrite += nRead;

						if (nWrite > getSocketBufferWriteWarningSize()) {
							fileDownload.write(byBufferWrite, 0, nWrite);

							mDownLoadInfo.nProgress += nWrite;

							nWrite = 0;
							++nCounter;
						}
					} else {
						break;
					}

					if (((DateUtils.currentTimeMillis() - lTime) > REFRESH_DOWNLOAD_STATE_TIME) && (nCounter > nTotal)) {
						sendDownloadState(mDownLoadInfo);

						lTime = DateUtils.currentTimeMillis();
						nTotal = nCounter;
					}
				} while (mnCancelState == CancelState.DOWNLOAD);

				if (nWrite > 0) {
					fileDownload.write(byBufferWrite, 0, nWrite);

					mDownLoadInfo.nProgress += nWrite;

					sendDownloadState(mDownLoadInfo);

					nWrite = 0;
				}

				inputStream.close();
				fileDownload.close();
				httpURLConnection.disconnect();

				if (mDownLoadInfo.nProgress == mDownLoadInfo.nSize) {
					mDownLoadInfo.nDownloadState = DownloadState.FINISH;
					mDownLoadInfo.nDownLoadResult = DownLoadResult.SUCCESS;
				} else {
					mDownLoadInfo.nDownloadState = DownloadState.FAIL;
					mDownLoadInfo.nDownLoadResult = DownLoadResult.FAIL;
				}
			} else {
				mDownLoadInfo.nDownloadState = DownloadState.FAIL;
				mDownLoadInfo.nDownLoadResult = DownLoadResult.FAIL;
			}
		} catch (Exception e) {
			mDownLoadInfo.nDownloadState = DownloadState.FAIL;
			mDownLoadInfo.nDownLoadResult = DownLoadResult.FAIL;
		}

		return mDownLoadInfo.nDownLoadResult;
	}

	/*
	 * 下载
	 */
	public int downloadNomal() {
		URLConnection urlConnection = null;
		InputStream inputStream = null;
		FileOutputStream fileOutputStream = null;
		File fileDownload = null;
		int nRead = 0, nWrite = 0, nCounter = 0, nTotal = 0;
		byte byBuffer[] = null, byBufferWrite[] = null;
		long lTime = 0;

		try {
			byBuffer = new byte[getSocketBufferReadSize()];
			byBufferWrite = new byte[getSocketBufferWriteMaxSize()];

			nWrite = 0;
			nCounter = 0;
			nTotal = 0;

			urlConnection = HttpUtil.getHttpURLConnection(mDownLoadInfo.strUrl);

			urlConnection.setConnectTimeout(getConnectTimeoutSize());

			urlConnection.setReadTimeout(getReadTimeoutSize());

			// urlConnection.connect();
			inputStream = urlConnection.getInputStream();

			mDownLoadInfo.nSize = urlConnection.getContentLength();

			if ((null != inputStream) && (mDownLoadInfo.nSize > 0)) {
				fileDownload = new File(getFullBufferPath());

				if (fileDownload.exists()) {
					fileDownload.delete();
				}

				fileOutputStream = new FileOutputStream(fileDownload);

				lTime = DateUtils.currentTimeMillis();

				do {
					nRead = inputStream.read(byBuffer);

					if (nRead > 0) {
						for (int i = 0; i < nRead; ++i) {
							byBufferWrite[nWrite + i] = byBuffer[i];
						}

						nWrite += nRead;

						if (nWrite > getSocketBufferWriteWarningSize()) {
							fileOutputStream.write(byBufferWrite, 0, nWrite);

							mDownLoadInfo.nProgress += nWrite;

							nWrite = 0;
							++nCounter;
						}
					} else {
						break;
					}

					if (((DateUtils.currentTimeMillis() - lTime) > REFRESH_DOWNLOAD_STATE_TIME) && (nCounter > nTotal)) {
						sendDownloadState(mDownLoadInfo);

						lTime = DateUtils.currentTimeMillis();
						nTotal = nCounter;
					}
				} while (mnCancelState == CancelState.DOWNLOAD);

				if (nWrite > 0) {
					fileOutputStream.write(byBufferWrite, 0, nWrite);

					mDownLoadInfo.nProgress += nWrite;

					sendDownloadState(mDownLoadInfo);

					nWrite = 0;
				}

				inputStream.close();

				if (mDownLoadInfo.nProgress == mDownLoadInfo.nSize) {
					mDownLoadInfo.nDownloadState = DownloadState.FINISH;
					mDownLoadInfo.nDownLoadResult = DownLoadResult.SUCCESS;
				} else {
					mDownLoadInfo.nDownloadState = DownloadState.FAIL;
					mDownLoadInfo.nDownLoadResult = DownLoadResult.FAIL;
				}
			} else {
				mDownLoadInfo.nDownloadState = DownloadState.FAIL;
				mDownLoadInfo.nDownLoadResult = DownLoadResult.FAIL;
			}
		} catch (Exception e) {
			mDownLoadInfo.nDownloadState = DownloadState.FAIL;
			mDownLoadInfo.nDownLoadResult = DownLoadResult.FAIL;
		}

		return mDownLoadInfo.nDownLoadResult;
	}
}