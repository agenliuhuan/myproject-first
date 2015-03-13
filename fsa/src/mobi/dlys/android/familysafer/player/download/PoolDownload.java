package mobi.dlys.android.familysafer.player.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.player.download.DownloadManage.DownloadState;
import mobi.dlys.android.familysafer.player.net.HttpDownLoad.DownLoadError;
import mobi.dlys.android.familysafer.player.utils.HttpUtil;
import mobi.dlys.android.familysafer.utils.FileUtils;
import mobi.dlys.android.familysafer.utils.NetUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

/*
 * 音乐文件下载队列
 */
public class PoolDownload {
	private static final String TAG = "PoolDownload";
	public final static String POOLDOWNLOAD_ID = "mobi.dlys.android.familysafer.pooldownload.id"; // 队列ID
	public final static String POOLDOWNLOAD_MUSICID = "mobi.dlys.android.familysafer.pooldownload.musicid"; // 音乐ID
	public final static String POOLDOWNLOAD_MSGID = "mobi.dlys.android.familysafer.pooldownload.msgid"; // 音乐ID
	public final static String POOLDOWNLOAD_BITRATE = "mobi.dlys.android.familysafer.pooldownload.bitrate"; // 码率
	public final static String POOLDOWNLOAD_TOTAL = "mobi.dlys.android.familysafer.pooldownload.total"; // 总大小
	public final static String POOLDOWNLOAD_CURRENT = "mobi.dlys.android.familysafer.pooldownload.current"; // 当前大小
	public final static String POOLDOWNLOAD_STATE = "mobi.dlys.android.familysafer.pooldownload.state"; // 下载状态
	public final static String BUFFER_MUSIC_PATH = FileUtils.BUFFER; // 路径
	public final static String BUFFER_MUSIC_PREFIX = "voice"; // 前缀
	public final static String BUFFER_MUSIC_SUFFIX = ".buf"; // 后缀
	public final static String ACCESS_BUFFER_MUSIC_MODE = "rwd"; // 访问方式

	private final static int SOCKET_BUFFER_SIZE_WIFI = 64 * 1024; // WIFI缓冲大小
	private final static int SOCKET_BUFFER_SIZE_GPRS = 16 * 1024; // GPRS缓冲大小

	private final static int SOCKET_BUFFER_WRITE_WIFI = 64 * 1024 + SOCKET_BUFFER_SIZE_WIFI; // WIFI缓冲大小
	private final static int SOCKET_BUFFER_WRITE_GPRS = 16 * 1024 + SOCKET_BUFFER_SIZE_GPRS; // GPRS缓冲大小

	private final static int SOCKET_CONNECT_TIMEOUT_WIFI = 2000; // 连接超时
	private final static int SOCKET_CONNECT_TIMEOUT_GPRS = 5000; // 连接超时

	private final static int SOCKET_READ_TIMEOUT_WIFI = 5000; // WIFI读取超时
	private final static int SOCKET_READ_TIMEOUT_GPRS = 30000; // GPRS读取超时

	// private final static int SOCKET_CONNECT_TIMEOUT = 5000;
	// private final static int SOCKET_READ_TIMEOUT = 10000; // 读取超时
	// private final static int SOCKET_BUFFER_SIZE = 128 * 1024; // 缓冲大小

	/*
	 * 下载信息
	 */
	public static class PoolDownloadInfo {
		public int nID = 0; // 标示
		public int nMusicID = 0; // 失败
		public int nMsgID = 0; // 失败
		public int nBitRate = 0; // 码率
		public int nTotal = 0; // 总大小
		public int nCurrent = 0; // 下载
		public boolean bAcceptRanges = true; // 支持断点下载
		public int nState = DownloadState.WAIT; // 等待
		public File fileMusic = null; // 文件句柄
		public String strPath = ""; // 文件路径
		public String strUrl = ""; // 下载路径
		public Handler handler = null; // 信息句柄
		public boolean bCancel = false; // 中止
		public String strSongName = ""; // 歌曲名

		public static void clone(PoolDownloadInfo dest, PoolDownloadInfo src) {
			dest.nID = src.nID;
			dest.nMusicID = src.nMusicID;
			dest.nMsgID = src.nMsgID;
			dest.nBitRate = src.nBitRate;
			dest.nTotal = src.nTotal;
			dest.nCurrent = src.nCurrent;
			dest.bAcceptRanges = src.bAcceptRanges;
			dest.nState = src.nState;
			dest.fileMusic = src.fileMusic;
			dest.strPath = new String(src.strPath);
			dest.strUrl = new String(src.strUrl);
			dest.handler = src.handler;
			dest.bCancel = src.bCancel;
			dest.strSongName = src.strSongName;
		}
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

	private static List<PoolDownloadInfo> mlistPoolDownloadInfo = new ArrayList<PoolDownloadInfo>(); // 下载队列列表
	// private static MusicApiManage mMusicApiManage = null; // 音乐API
	private static int mnCounter = 0;

	/*
	 * 清除列表
	 */
	public static void clearPoolDownload(PoolDownloadInfo poolDownloadInfo) {
		synchronized (mlistPoolDownloadInfo) {
			if ((mlistPoolDownloadInfo != null) && (mlistPoolDownloadInfo.size() > 0)) {
				for (int i = 0; i < mlistPoolDownloadInfo.size(); ++i) {
					mlistPoolDownloadInfo.get(i).handler = null;
					mlistPoolDownloadInfo.get(i).bCancel = true;
				}

				mlistPoolDownloadInfo.clear();
			}
		}
	}

	/*
	 * 获取下载列表
	 */
	public static void getPoolDownload(PoolDownloadInfo poolDownloadInfo) {
		synchronized (mlistPoolDownloadInfo) {
			if ((mlistPoolDownloadInfo != null) && (mlistPoolDownloadInfo.size() > 0)) {
				PoolDownloadInfo.clone(poolDownloadInfo, mlistPoolDownloadInfo.get(mlistPoolDownloadInfo.size() - 1));
			}
		}
	}

	/*
	 * 获取下载列表
	 */
	private static PoolDownloadInfo getPoolDownload(int nID) {
		PoolDownloadInfo poolDownloadInfo = null;

		synchronized (mlistPoolDownloadInfo) {
			if (mlistPoolDownloadInfo != null) {
				for (int i = 0; i < mlistPoolDownloadInfo.size(); ++i) {
					if (mlistPoolDownloadInfo.get(i).nID == nID) {
						poolDownloadInfo = mlistPoolDownloadInfo.get(i);

						break;
					}
				}
			}
		}

		return poolDownloadInfo;
	}

	/*
	 * 添加下载列表
	 */
	public static void addPoolDownload(PoolDownloadInfo poolDownloadInfo) {
		int nSize = 0;

		synchronized (mlistPoolDownloadInfo) {
			if (mlistPoolDownloadInfo == null) {
				mlistPoolDownloadInfo = new ArrayList<PoolDownloadInfo>();
			}

			nSize = mlistPoolDownloadInfo.size();

			if ((nSize > 0) && (poolDownloadInfo.nMusicID == mlistPoolDownloadInfo.get(nSize - 1).nMusicID)) {
				if (mlistPoolDownloadInfo.get(nSize - 1).nState == DownloadState.FINISH) {
					sendDownloadState(mlistPoolDownloadInfo.get(nSize - 1));
				} else if (mlistPoolDownloadInfo.get(nSize - 1).nState == DownloadState.FAIL) {
					mlistPoolDownloadInfo.get(nSize - 1).nState = DownloadState.WAIT;
				}
			} else {
				for (int i = 0; i < mlistPoolDownloadInfo.size(); ++i) {
					mlistPoolDownloadInfo.get(i).handler = null;
					mlistPoolDownloadInfo.get(i).bCancel = true;
				}

				poolDownloadInfo.nID = ++mnCounter;
				poolDownloadInfo.nTotal = 0;
				poolDownloadInfo.nCurrent = 0;
				poolDownloadInfo.nState = DownloadState.WAIT;
				poolDownloadInfo.strPath = "";
				// poolDownloadInfo.strUrl = "";
				poolDownloadInfo.fileMusic = null;
				poolDownloadInfo.bCancel = false;

				mlistPoolDownloadInfo.add(poolDownloadInfo);

				new Thread(new Runnable() {
					public void run() {
						download(mnCounter);
					}
				}).start();
			}
		}
	}

	/*
	 * 删除队列中不再使用的下载
	 */
	private static void deletePoolDownload(int nID, int msgID) {
		synchronized (mlistPoolDownloadInfo) {
			if (mlistPoolDownloadInfo != null) {
				for (int i = 0; i < mlistPoolDownloadInfo.size(); ++i) {
					if (mlistPoolDownloadInfo.get(i).nID == nID) {
						mlistPoolDownloadInfo.remove(i);

						FileUtils.delFile(getMusic(nID, msgID));

						break;
					}
				}
			}
		}
	}

	/*
	 * 获取缓冲文件
	 */
	public static String getMusic(int nID, int msgID) {
		return FileUtils.getFilePath(FileUtils.BUFFER + BUFFER_MUSIC_PREFIX, String.valueOf(nID) + "_" + msgID, BUFFER_MUSIC_SUFFIX);
	}

	/*
	 * 清除缓冲文件
	 */
	public static void clearMusic() {
		FileUtils.clearDirectory(BUFFER_MUSIC_PATH);
	}

	/*
	 * 获取文件大小
	 */
	private static int getFileInfo(PoolDownloadInfo poolDownloadInfo) {
		HttpURLConnection httpURLConnection = null;
		String strAcceptRanges = "";
		int nResult = DownLoadError.FAIL;

		try {
			httpURLConnection = HttpUtil.getHttpURLConnection(poolDownloadInfo.strUrl);

			httpURLConnection.setConnectTimeout(!NetUtils.isWifiEnable() ? SOCKET_CONNECT_TIMEOUT_GPRS : SOCKET_CONNECT_TIMEOUT_WIFI);

			httpURLConnection.setReadTimeout(!NetUtils.isWifiEnable() ? SOCKET_READ_TIMEOUT_GPRS : SOCKET_READ_TIMEOUT_WIFI);

			poolDownloadInfo.nTotal = httpURLConnection.getContentLength();

			strAcceptRanges = httpURLConnection.getHeaderField(HeaderField.ACCEPT_RANGES);

			if ((strAcceptRanges != null) && strAcceptRanges.equals(HeaderFieldResult.BYTES)) {
				poolDownloadInfo.bAcceptRanges = true;
			} else {
				poolDownloadInfo.bAcceptRanges = false;
			}

			httpURLConnection.disconnect();

			if (poolDownloadInfo.nTotal > 0) {
				FileUtils.createDummyFile(poolDownloadInfo.strPath, poolDownloadInfo.nTotal);
			}

			nResult = DownLoadError.SUCCESS;
		} catch (Exception e) {
			nResult = DownLoadError.FAIL;
		}

		return nResult;
	}

	/*
	 * 下载
	 */
	private static void download(final int nID) {
		PoolDownloadInfo poolDownloadInfo = null;
		String strUrl = "", strFile = "";
		File file = null;
		int nDownloadError;
		int nDownFinish;
		int nDownTimes = 0;

		poolDownloadInfo = getPoolDownload(nID);

		if (poolDownloadInfo != null) {
			while (!poolDownloadInfo.bCancel) {
				try {
					if (!((poolDownloadInfo.nCurrent == poolDownloadInfo.nTotal) && (poolDownloadInfo.nTotal > 0))) {
						if (poolDownloadInfo.strPath.length() == 0) {
							poolDownloadInfo.strPath = getMusic(nID, poolDownloadInfo.nMsgID);
						}

						if (poolDownloadInfo.strPath.length() > 0) {
							try {
								strUrl = poolDownloadInfo.strUrl;

								++nDownTimes;

								// 获取文件url

							} catch (Exception e) {
								strUrl = null;
							}

							if (!TextUtils.isEmpty(strUrl) && (!strUrl.equals(String.valueOf(NetUtils.NET_NOT_CONNECTION)))) {
								poolDownloadInfo.strUrl = new String(strUrl);

								nDownloadError = getFileInfo(poolDownloadInfo);

								if (DownLoadError.SUCCESS == nDownloadError) {
									if (poolDownloadInfo.bAcceptRanges) {
										nDownFinish = downloadRange(poolDownloadInfo);
									} else {
										poolDownloadInfo.nCurrent = 0;
										poolDownloadInfo.nTotal = 0;

										nDownFinish = downloadNomal(poolDownloadInfo);
									}

									if (DownLoadError.SUCCESS == nDownloadError && nDownFinish == DownloadState.FINISH) {
										// strFile =
										// FileUtils.getFilePath(FileUtils.MUSIC,
										// poolDownloadInfo.nMusicID,
										// FileUtils.MP3);
										strFile = FileUtils.VOICE + "voice_" + poolDownloadInfo.nMusicID + "_" + poolDownloadInfo.nMsgID + FileUtils.AMR;

										file = new File(strFile);
										if (file.exists() && file.isFile()) {
											file.delete();
										}

										if (!FileUtils.copyFile(strFile, FileUtils.getFilePath(FileUtils.BUFFER, BUFFER_MUSIC_PREFIX + nID + "_"
												+ poolDownloadInfo.nMsgID, BUFFER_MUSIC_SUFFIX))) {
											poolDownloadInfo.nState = DownloadState.FAIL;
											sendDownloadState(poolDownloadInfo);
											poolDownloadInfo.bCancel = true;
										}
									}
								}
							} else {
								poolDownloadInfo.nState = DownloadState.FAIL;

								sendDownloadState(poolDownloadInfo);

								if (nDownTimes >= 3) {
									poolDownloadInfo.bCancel = true;
								}
							}
						} else {
							poolDownloadInfo.nState = DownloadState.FAIL;

							sendDownloadState(poolDownloadInfo);

							if (nDownTimes >= 3) {
								poolDownloadInfo.bCancel = true;
							}
						}
					}
				} catch (Exception e) {
					poolDownloadInfo.nState = DownloadState.FAIL;

					sendDownloadState(poolDownloadInfo);

					if (nDownTimes >= 3) {
						poolDownloadInfo.bCancel = true;
					}
				}

				try {
					Thread.sleep(500);
				} catch (Exception e) {
				}
			}

			deletePoolDownload(nID, poolDownloadInfo.nMsgID);
		}
	}

	/*
	 * 断点续传
	 */
	public static int downloadRange(PoolDownloadInfo poolDownloadInfo) {
		HttpURLConnection httpURLConnection = null;
		InputStream inputStream = null;
		RandomAccessFile fileDownload = null;
		int nRead = 0, nWrite = 0, nBufferSize = 0;
		byte byBuffer[] = null, byBufferWrite[] = null;

		try {
			nBufferSize = !NetUtils.isWifiEnable() ? SOCKET_BUFFER_SIZE_GPRS : SOCKET_BUFFER_SIZE_WIFI;

			poolDownloadInfo.nState = DownloadState.DOWNLOAD;

			sendDownloadState(poolDownloadInfo);

			byBuffer = new byte[nBufferSize];

			byBufferWrite = new byte[!NetUtils.isWifiEnable() ? SOCKET_BUFFER_WRITE_GPRS : SOCKET_BUFFER_WRITE_WIFI];

			nWrite = 0;

			httpURLConnection = HttpUtil.getHttpURLConnection(poolDownloadInfo.strUrl);

			httpURLConnection.setConnectTimeout(!NetUtils.isWifiEnable() ? SOCKET_CONNECT_TIMEOUT_GPRS : SOCKET_CONNECT_TIMEOUT_WIFI);

			httpURLConnection.setReadTimeout(!NetUtils.isWifiEnable() ? SOCKET_READ_TIMEOUT_GPRS : SOCKET_READ_TIMEOUT_WIFI);

			httpURLConnection.setRequestProperty("Range", "bytes=" + poolDownloadInfo.nCurrent + "-" + poolDownloadInfo.nTotal);

			inputStream = httpURLConnection.getInputStream();

			if (null != inputStream) {
				fileDownload = new RandomAccessFile(poolDownloadInfo.strPath, ACCESS_BUFFER_MUSIC_MODE);

				fileDownload.seek(poolDownloadInfo.nCurrent);

				do {
					nRead = inputStream.read(byBuffer);

					if (nRead > 0) {
						for (int i = 0; i < nRead; ++i) {
							byBufferWrite[nWrite + i] = byBuffer[i];
						}

						nWrite += nRead;

						if (nWrite > (byBufferWrite.length - nBufferSize)) {
							fileDownload.write(byBufferWrite, 0, nWrite);

							poolDownloadInfo.nCurrent += nWrite;

							sendDownloadState(poolDownloadInfo);

							nWrite = 0;

							try {
								Thread.sleep(1);
							} catch (Exception e) {
							}
						}
					} else {
						break;
					}
				} while (!poolDownloadInfo.bCancel);

				if (nWrite > 0) {
					fileDownload.write(byBufferWrite, 0, nWrite);

					poolDownloadInfo.nCurrent += nWrite;

					sendDownloadState(poolDownloadInfo);
				}

				inputStream.close();
				fileDownload.close();
				httpURLConnection.disconnect();

				if (poolDownloadInfo.nCurrent == poolDownloadInfo.nTotal) {
					poolDownloadInfo.nState = DownloadState.FINISH;
				} else {
					poolDownloadInfo.nState = DownloadState.FAIL;
				}
			} else {
				poolDownloadInfo.nState = DownloadState.FAIL;
			}
		} catch (Exception e) {
			poolDownloadInfo.nState = DownloadState.FAIL;
		}

		sendDownloadState(poolDownloadInfo);

		return poolDownloadInfo.nState;
	}

	/*
	 * 下载
	 */
	public static int downloadNomal(PoolDownloadInfo poolDownloadInfo) {
		URLConnection urlConnection = null;
		InputStream inputStream = null;
		FileOutputStream fileOutputStream = null;
		File fileDownload = null;
		int nRead = 0, nWrite = 0, nBufferSize = 0;
		byte byBuffer[] = null, byBufferWrite[] = null;

		try {
			nBufferSize = !NetUtils.isWifiEnable() ? SOCKET_BUFFER_SIZE_GPRS : SOCKET_BUFFER_SIZE_WIFI;

			poolDownloadInfo.nState = DownloadState.DOWNLOAD;

			sendDownloadState(poolDownloadInfo);

			byBuffer = new byte[nBufferSize];

			byBufferWrite = new byte[!NetUtils.isWifiEnable() ? SOCKET_BUFFER_WRITE_GPRS : SOCKET_BUFFER_WRITE_WIFI];

			nWrite = 0;

			urlConnection = HttpUtil.getHttpURLConnection(poolDownloadInfo.strUrl);

			urlConnection.setConnectTimeout(!NetUtils.isWifiEnable() ? SOCKET_CONNECT_TIMEOUT_GPRS : SOCKET_CONNECT_TIMEOUT_WIFI);

			urlConnection.setReadTimeout(!NetUtils.isWifiEnable() ? SOCKET_READ_TIMEOUT_GPRS : SOCKET_READ_TIMEOUT_WIFI);

			inputStream = urlConnection.getInputStream();

			poolDownloadInfo.nTotal = urlConnection.getContentLength();

			sendDownloadState(poolDownloadInfo);

			if (null != inputStream) {
				// randomAccessFile = new
				// RandomAccessFile(poolDownloadInfo.strPath,
				// ACCESS_BUFFER_MUSIC_MODE);

				fileOutputStream = new FileOutputStream(fileDownload);

				poolDownloadInfo.nCurrent = 0;

				do {
					nRead = inputStream.read(byBuffer);

					if (nRead > 0) {
						for (int i = 0; i < nRead; ++i) {
							byBufferWrite[nWrite + i] = byBuffer[i];
						}

						nWrite += nRead;

						if (nWrite > (byBufferWrite.length - nBufferSize)) {
							fileOutputStream.write(byBufferWrite, 0, nWrite);

							poolDownloadInfo.nCurrent += nWrite;

							sendDownloadState(poolDownloadInfo);

							nWrite = 0;

							try {
								Thread.sleep(1);
							} catch (Exception e) {
							}
						}
					} else {
						break;
					}
				} while (!poolDownloadInfo.bCancel);

				if (nWrite > 0) {
					fileOutputStream.write(byBufferWrite, 0, nWrite);

					poolDownloadInfo.nCurrent += nWrite;

					sendDownloadState(poolDownloadInfo);
				}

				inputStream.close();

				if (poolDownloadInfo.nCurrent == poolDownloadInfo.nTotal) {
					poolDownloadInfo.nState = DownloadState.FINISH;
				} else {
					poolDownloadInfo.nState = DownloadState.FAIL;
				}
			} else {
				poolDownloadInfo.nState = DownloadState.FAIL;
			}
		} catch (Exception e) {
			poolDownloadInfo.nState = DownloadState.FAIL;
		}

		sendDownloadState(poolDownloadInfo);

		return poolDownloadInfo.nState;
	}

	/*
	 * 广播下载状态
	 */
	private static void sendDownloadState(PoolDownloadInfo poolDownloadInfo) {
		App app = null;
		Message message = null;
		Bundle bundle = null;

		try {
			app = App.getInstance();

			if ((app != null) && (poolDownloadInfo.handler != null)) {
				message = new Message();
				bundle = new Bundle();

				bundle.putInt(POOLDOWNLOAD_ID, poolDownloadInfo.nID);
				bundle.putInt(POOLDOWNLOAD_MUSICID, poolDownloadInfo.nMusicID);
				bundle.putInt(POOLDOWNLOAD_MSGID, poolDownloadInfo.nMsgID);
				bundle.putInt(POOLDOWNLOAD_BITRATE, poolDownloadInfo.nBitRate);
				bundle.putInt(POOLDOWNLOAD_TOTAL, poolDownloadInfo.nTotal);
				bundle.putInt(POOLDOWNLOAD_CURRENT, poolDownloadInfo.nCurrent);
				bundle.putInt(POOLDOWNLOAD_STATE, poolDownloadInfo.nState);

				message.setData(bundle);

				poolDownloadInfo.handler.sendMessage(message);
			}
		} catch (Exception e) {
		}
	}
}