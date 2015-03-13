package mobi.dlys.android.familysafer.player.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URLConnection;

import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.player.download.DownloadManage;
import mobi.dlys.android.familysafer.player.download.DownloadManage.DownLoadInfo;
import mobi.dlys.android.familysafer.player.download.DownloadManage.DownLoadResult;
import mobi.dlys.android.familysafer.player.download.DownloadManage.DownloadState;
import mobi.dlys.android.familysafer.player.utils.HttpUtil;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import mobi.dlys.android.familysafer.utils.DateUtils;
import mobi.dlys.android.familysafer.utils.FileUtils;
import mobi.dlys.android.familysafer.utils.NetUtils;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

/*
 * 网络下载
 */
public class HttpDownLoad {
	public final static String ACTION_DOWNLOAD = "mobi.dlys.android.familysafer.download"; // 音乐ID
	public final static String ACTION_DOWNLOAD_MUSICID = "mobi.dlys.android.familysafer.download.musicid"; // 音乐ID
	public final static String ACTION_DOWNLOAD_TOTAL = "mobi.dlys.android.familysafer.download.total"; // 总大小
	public final static String ACTION_DOWNLOAD_CURRENT = "mobi.dlys.android.familysafer.download.current"; // 当前大小
	public final static String ACTION_DOWNLOAD_STATE = "mobi.dlys.android.familysafer.download.state"; // 下载状态
	public final static String ACTION_DOWNLOAD_RESULT = "mobi.dlys.android.familysafer.download.result"; // 下载结果

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
	private final static int DOWNLOAD_TRY_COUNT = 100; // 重试次数
	private final static int REFRESH_DOWNLOAD_STATE_TIME = 1000; // 刷新状态时间

	/*
	 * 下载错误信息
	 */
	public class DownLoadError {
		public final static int FINISH = -2; // 已经完成下载
		public final static int RANGE_MODE = -2; // 断点续传模式
		public final static int NORMAL_MODE = -1; // 正常下载模式
		public final static int SUCCESS = 0; // 成功
		public final static int FAIL = 1; // 失败
		public final static int GET_URL_ERROR = 2; // 无法取得歌曲URL地址
		public final static int CONNECT_URL_ERROR = 3; // 连接歌曲URL地址失败
		public final static int SOCKET_ERROR = 4; // 网络出现异常
		public final static int FILE_SIZE_ERROR = 5; // 文件大小不一致
	}

	/*
	 * HTTP 头字段
	 */
	public class HeaderField {
		public final static String ACCEPT_RANGES = "accept-ranges", CONTENT_RANGE = "content-range";
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

	private DownLoadInfo mDownLoadInfo = null; // 下载信息
	private Context mContext = null; // 上下文
	private int mnCancelState = CancelState.DOWNLOAD; // 取消

	/*
	 * 构造
	 */
	public HttpDownLoad(Context context) {
		mContext = context;
		mDownLoadInfo = new DownLoadInfo();
	}

	/*
	 * 强制停止下载
	 */
	public void cancel(int nMusicID) {
		if (mDownLoadInfo.nMusicID == nMusicID) {
			mnCancelState = CancelState.CANCEL;
		}
	}

	/*
	 * 强制删除
	 */
	public void delete(int nMusicID) {
		if (mDownLoadInfo.nMusicID == nMusicID) {
			mnCancelState = CancelState.DELETE;
		}
	}

	/*
	 * 广播下载状态
	 */
	private void sendDownloadState(DownLoadInfo downLoadInfo) {
		Intent intent = null;

		try {
			intent = new Intent();

			intent.setAction(ACTION_DOWNLOAD);

			intent.putExtra(ACTION_DOWNLOAD_MUSICID, downLoadInfo.nMusicID);
			intent.putExtra(ACTION_DOWNLOAD_TOTAL, downLoadInfo.lSize);
			intent.putExtra(ACTION_DOWNLOAD_CURRENT, downLoadInfo.nProgress);
			intent.putExtra(ACTION_DOWNLOAD_STATE, downLoadInfo.nDownloadState);
			intent.putExtra(ACTION_DOWNLOAD_RESULT, downLoadInfo.nDownLoadResult);

			mContext.sendBroadcast(intent);

			// NotificationUtil.updateDownloadNotification(downLoadInfo.nMusicID,
			// downLoadInfo.nDownloadState, downLoadInfo.lSize,
			// downLoadInfo.nProgress);
		} catch (Exception e) {
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
		cancel(mDownLoadInfo.nMusicID);
	}

	/*
	 * 退出
	 */
	public void exit() {
		cancel(mDownLoadInfo.nMusicID);
	}

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
	 * 获取文件大小
	 */
	private int getFileInfo() {
		HttpURLConnection httpURLConnection = null;
		File fileDownload = null;
		String strAcceptRanges = "";
		int nResult = DownLoadError.FAIL, nContentLength = 0;

		try {
			fileDownload = new File(FileUtils.VOICE + mDownLoadInfo.nMusicID + "_" + mDownLoadInfo.nMsgID + DownloadManage.BUFFER_TAG);

			if (fileDownload.exists() && fileDownload.isFile()) {
				mDownLoadInfo.nProgress = (int) fileDownload.length();
			}

			httpURLConnection = HttpUtil.getHttpURLConnection(mDownLoadInfo.strOnlineMusicUrl);

			httpURLConnection.setConnectTimeout(getConnectTimeoutSize());

			httpURLConnection.setReadTimeout(getReadTimeoutSize());
			httpURLConnection.connect();
			nContentLength = httpURLConnection.getContentLength();

			strAcceptRanges = httpURLConnection.getHeaderField(HeaderField.ACCEPT_RANGES);

			if ((strAcceptRanges != null) && strAcceptRanges.equals(HeaderFieldResult.BYTES)) {
				mDownLoadInfo.bAcceptRanges = true;
			} else {
				mDownLoadInfo.bAcceptRanges = false;
			}

			httpURLConnection.disconnect();

			if (nContentLength > 0) {
				if ((mDownLoadInfo.lSize != nContentLength) || (mDownLoadInfo.nProgress > mDownLoadInfo.lSize)) {
					if (fileDownload.exists() && fileDownload.isFile()) {
						fileDownload.delete();
					}

					mDownLoadInfo.lSize = nContentLength;
					mDownLoadInfo.nProgress = 0;
				}

				if (mDownLoadInfo.lSize > 0) {
					fileDownload = new File(FileUtils.VOICE + mDownLoadInfo.strName + "_" + mDownLoadInfo.nMusicID + "_" + mDownLoadInfo.nMsgID
							+ DownloadManage.MP3_TAG);

					if (fileDownload.exists() && fileDownload.isFile() && (fileDownload.length() == mDownLoadInfo.lSize)) {
						nResult = DownLoadError.FINISH;
					} else {
						nResult = DownLoadError.SUCCESS;
					}
				}
			}
		} catch (Exception e) {
			nResult = DownLoadError.FAIL;
		}

		return nResult;
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

		for (int i = 0; i < DOWNLOAD_TRY_COUNT; ++i) {
			mDownLoadInfo.nDownloadState = DownloadState.DOWNLOAD;
			mDownLoadInfo.nDownLoadResult = DownLoadResult.START;

			/*
			 * try { if (mDownLoadInfo.nBitRate == 0) { strMp3 =
			 * NetUtils.isProxy ? Parameter.SUBITEMTYPE_MP3_LOW_QUALITY :
			 * Parameter.SUBITEMTYPE_MP3_HIGH_QUALITY; mDownLoadInfo.nBitRate =
			 * NetUtils.isProxy ? Parameter.BITRATE_MP3_LOW_QUALITY :
			 * Parameter.BITRATE_MP3_HIGH_QUALITY; } else { if
			 * (mDownLoadInfo.nBitRate == Parameter.BITRATE_MP3_HIGH_QUALITY) {
			 * strMp3 = Parameter.SUBITEMTYPE_MP3_HIGH_QUALITY; } else { strMp3
			 * = Parameter.SUBITEMTYPE_MP3_LOW_QUALITY; } } strUrl =
			 * mMusicApiManage.confirmDownload("" + mDownLoadInfo.nMusicID,
			 * strMp3); } catch (Exception e) { strUrl = null; }
			 */

			if (strUrl == null) {
				mDownLoadInfo.nDownloadState = DownloadState.FAIL;
				mDownLoadInfo.nDownLoadResult = DownLoadResult.FAIL;
			} else if (strUrl.equals(String.valueOf(NetUtils.NET_NOT_CONNECTION))) {
				mDownLoadInfo.nDownloadState = DownloadState.FAIL;
				mDownLoadInfo.nDownLoadResult = DownLoadResult.FAIL;

				break;
			} else {
				mDownLoadInfo.strOnlineMusicUrl = new String(strUrl);

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

				deleteDownloadFile(mDownLoadInfo.nMusicID, mDownLoadInfo.nMsgID, mDownLoadInfo.strName);

				break;
			} else if (mDownLoadInfo.nDownloadState == DownloadState.FINISH) {
				break;
			}

			try {
				Thread.sleep(50);
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

			httpURLConnection = HttpUtil.getHttpURLConnection(mDownLoadInfo.strOnlineMusicUrl);

			httpURLConnection.setConnectTimeout(getConnectTimeoutSize());

			httpURLConnection.setReadTimeout(getReadTimeoutSize());

			httpURLConnection.setRequestProperty("Range", "bytes=" + mDownLoadInfo.nProgress + "-" + mDownLoadInfo.lSize);

			inputStream = httpURLConnection.getInputStream();

			if (null != inputStream) {
				fileDownload = new RandomAccessFile(FileUtils.VOICE + mDownLoadInfo.nMusicID + "_" + mDownLoadInfo.nMsgID + DownloadManage.BUFFER_TAG, "rwd");

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

				if (mDownLoadInfo.nProgress == mDownLoadInfo.lSize) {
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

			urlConnection = HttpUtil.getHttpURLConnection(mDownLoadInfo.strOnlineMusicUrl);

			urlConnection.setConnectTimeout(getConnectTimeoutSize());
			urlConnection.setReadTimeout(getReadTimeoutSize());
			inputStream = urlConnection.getInputStream();

			mDownLoadInfo.lSize = urlConnection.getContentLength();

			if ((null != inputStream) && (mDownLoadInfo.lSize > 0)) {
				fileDownload = new File(mContext.getCacheDir(), DownloadManage.BUFFER_FILE);

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

				if (mDownLoadInfo.nProgress == mDownLoadInfo.lSize) {
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
	 * 删除下载文件
	 */
	public static void deleteDownloadFile(int nMusicID) {
		File fileDelete = null;

		try {
			fileDelete = new File(FileUtils.VOICE + nMusicID + DownloadManage.BUFFER_TAG);

			if (fileDelete.exists() && fileDelete.isFile()) {
				fileDelete.delete();
			}

			fileDelete = new File(FileUtils.VOICE + nMusicID + DownloadManage.MP3_TAG);

			if (fileDelete.exists() && fileDelete.isFile()) {
				fileDelete.delete();
			}
		} catch (Exception e) {
		}
	}

	public static void deleteDownloadFile(int nMusicID, int nMsgID, String strSongName) {
		File fileDelete = null;

		try {
			fileDelete = new File(FileUtils.VOICE + nMusicID + "_" + nMsgID + DownloadManage.BUFFER_TAG);

			if (fileDelete.exists() && fileDelete.isFile()) {
				fileDelete.delete();
			}

			fileDelete = new File(FileUtils.VOICE + nMusicID + "_" + nMsgID + DownloadManage.MP3_TAG);

			if (fileDelete.exists() && fileDelete.isFile()) {
				fileDelete.delete();
			}

			fileDelete = new File(FileUtils.VOICE + strSongName + "_" + nMusicID + "_" + nMsgID + DownloadManage.MP3_TAG);

			if (fileDelete.exists() && fileDelete.isFile()) {
				fileDelete.delete();
			}
		} catch (Exception e) {
		}
	}

	/*
	 * 下载
	 */
	public static boolean download(Context context, String fileuri, String filepath, String action) {
		boolean result = false;

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

			httpURLConnection = HttpUtil.getHttpURLConnection(fileuri);
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
		return result;
	}
}