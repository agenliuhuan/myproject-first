package mobi.dlys.android.familysafer.player.download;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.familysafer.player.entity.MusicInfoEntity;
import mobi.dlys.android.familysafer.player.net.HttpDownLoad;
import mobi.dlys.android.familysafer.utils.FileUtils;
import android.content.Context;

/*
 * 下载管理
 */
public class DownloadManage {
	public final static String BUFFER_FILE = "BufferFile.dat"; // 缓冲文件
	public final static String PATH_TAG = "/";
	public final static String BUFFER_TAG = ".dwl";
	public final static String MP3_TAG = ".amr";
	public final static String DATA_TAG = ".dat";

	private List<DownLoadInfo> mlistDownLoadInfo = null; // 下载缓冲列表
	private HttpDownLoad mHttpDownLoad = null; // HTTP下载
	private Context mContext = null; // 上下文
	private boolean mbExit = false; // 退出

	/*
	 * 下载状态
	 */
	public class DownloadState {
		public final static int FAIL = -1; // 失败
		public final static int FINISH = 0; // 完成
		public final static int DOWNLOAD = 1; // 下载
		public final static int WAIT = 2; // 等待
		public final static int PAUSE = 3; // 暂停
	}

	/*
	 * 下载结果
	 */
	public static class DownLoadResult {
		public final static int WAIT = -3; // 等待
		public final static int START = -2; // 开始
		public final static int CANCEL = -1; // 取消
		public final static int SUCCESS = 0; // 成功
		public final static int FAIL = 1; // 失败
	}

	/*
	 * 下载歌曲信息
	 */
	public static class DownLoadInfo {
		public int nMusicID = 0; // 本地歌曲ID
		public int nMsgID = 0; // 消息ID
		public String strName = "";// 文件名或歌曲名
		public long lSize = 0;// 文件大小
		public int nProgress = 0;// 进度
		public boolean bAcceptRanges = true; // 支持断点下载
		public int nDownloadState = 0;// 下载状态 -1下载失败0已下载 1下载中 2等待下载
		public int nBitRate = 0;// 码率
		public String strSamplingRate = "";// 采样率
		public int nDuration = 0; // 时长
		public String strArtistName = "";// 歌手名
		public String strAlbumName = "";// 专辑名
		public String strLocalMusicPath = "";// 歌曲本地地址
		public String strOnlineMusicUrl = "";// 歌曲网络地址
		public String strOnlineLrcUrl = "";// 歌词网络地址
		public String strOnlineCoverUrl = "";// 封面网络地址
		public int nCollect = 0;// 是否收藏 0已收藏 1未收藏
		public long lDateTime = 0;// 开始缓存时间
		public int nDownLoadResult = DownLoadResult.WAIT; // 下载结果

		public static void clone(DownLoadInfo dest, DownLoadInfo src) {
			dest.nMusicID = src.nMusicID;
			dest.nMsgID = src.nMsgID;
			dest.strName = new String(src.strName);
			dest.lSize = src.lSize;
			dest.nProgress = src.nProgress;
			dest.bAcceptRanges = src.bAcceptRanges;
			dest.nDownloadState = src.nDownloadState;
			dest.nBitRate = src.nBitRate;
			dest.strSamplingRate = new String(src.strSamplingRate);
			dest.nDuration = src.nDuration;
			dest.strArtistName = new String(src.strArtistName);
			dest.strAlbumName = new String(src.strAlbumName);
			dest.strLocalMusicPath = new String(src.strLocalMusicPath);
			dest.strOnlineMusicUrl = new String(src.strOnlineMusicUrl);
			dest.strOnlineLrcUrl = new String(src.strOnlineLrcUrl);
			dest.strOnlineCoverUrl = new String(src.strOnlineCoverUrl);
			dest.nCollect = src.nCollect;
			dest.lDateTime = src.lDateTime;
			dest.nDownLoadResult = src.nDownLoadResult;
		}
	}

	/*
	 * 构造
	 */
	public DownloadManage(Context context) {
		mContext = context;

		mlistDownLoadInfo = getAllDownLoad();

		if (null == mlistDownLoadInfo) {
			mlistDownLoadInfo = new ArrayList<DownLoadInfo>();
		}

		mHttpDownLoad = new HttpDownLoad(context);

		new Thread(new Runnable() {
			public void run() {
				threadManage();
			}
		}).start();
	}

	/*
	 * 获取下载信息
	 */
	public DownLoadInfo getDownLoadInfo() {
		return mHttpDownLoad.getDownLoadInfo();
	}

	/*
	 * 更新下载
	 */
	public void updateDownload(DownLoadInfo downLoadInfo) {
		synchronized (mlistDownLoadInfo) {
			for (int i = 0; i < mlistDownLoadInfo.size(); ++i) {
				if (mlistDownLoadInfo.get(i).nMusicID == downLoadInfo.nMusicID && mlistDownLoadInfo.get(i).nMsgID == downLoadInfo.nMsgID) {
					mlistDownLoadInfo.get(i).nDownloadState = downLoadInfo.nDownloadState;
					mlistDownLoadInfo.get(i).lSize = downLoadInfo.lSize;
					mlistDownLoadInfo.get(i).nProgress = downLoadInfo.nProgress;
					mlistDownLoadInfo.get(i).nBitRate = downLoadInfo.nBitRate;

					break;
				}
			}
		}
	}

	/*
	 * 添加下载列表
	 */
	public void addDownload(MusicInfoEntity musicInfoEntity) {
		DownLoadInfo downLoadInfo = null;
		boolean bFind = false;

		try {
			downLoadInfo = new DownLoadInfo();

			synchronized (mlistDownLoadInfo) {
				if (downLoadInfo != null) {
					downLoadInfo.nMusicID = musicInfoEntity.nMusicID;
					downLoadInfo.nMsgID = musicInfoEntity.nMsgID;
					downLoadInfo.strName = musicInfoEntity.strSongName;

					for (int i = 0; i < mlistDownLoadInfo.size(); ++i) {
						if (mlistDownLoadInfo.get(i).nMusicID == downLoadInfo.nMusicID && mlistDownLoadInfo.get(i).nMsgID == downLoadInfo.nMsgID) {
							bFind = true;

							break;
						}
					}

					if (!bFind) {
						mlistDownLoadInfo.add(downLoadInfo);
					}
				}
			}
		} catch (Exception e) {
		}
	}

	/*
	 * 删除下载
	 */
	public void deleteDownload(int nMusicID, int nMsgID) {
		synchronized (mlistDownLoadInfo) {
			for (int i = 0; i < mlistDownLoadInfo.size(); ++i) {
				if (mlistDownLoadInfo.get(i).nMusicID == nMusicID && mlistDownLoadInfo.get(i).nMsgID == nMsgID) {
					mlistDownLoadInfo.remove(i);

					mHttpDownLoad.cancel(nMusicID);

					// FileUtils.deleteDownloadFile(nMusicID);

					FileUtils.delDirectory(getPath() + PATH_TAG + nMusicID);

					break;
				}
			}
		}
	}

	/*
	 * 暂停下载
	 */
	public void pauseDownload(int nMusicID) {
		synchronized (mlistDownLoadInfo) {
			for (int i = 0; i < mlistDownLoadInfo.size(); ++i) {
				if (mlistDownLoadInfo.get(i).nMusicID == nMusicID) {
					mHttpDownLoad.cancel(nMusicID);

					break;
				}
			}
		}
	}

	/*
	 * 恢复下载
	 */
	public void resumeDownload(int nMusicID) {
		synchronized (mlistDownLoadInfo) {
			for (int i = 0; i < mlistDownLoadInfo.size(); ++i) {
				if (mlistDownLoadInfo.get(i).nMusicID == nMusicID) {
					mlistDownLoadInfo.get(i).nDownloadState = DownloadState.WAIT;

					break;
				}
			}
		}
	}

	/*
	 * 获取需要下载的项
	 */
	private DownLoadInfo getNextDownLoad() {
		DownLoadInfo downLoadInfo = null;

		synchronized (mlistDownLoadInfo) {
			for (int i = 0; i < mlistDownLoadInfo.size(); ++i) {
				if (mlistDownLoadInfo.get(i).nDownloadState == DownloadState.WAIT) {
					downLoadInfo = mlistDownLoadInfo.get(i);

					break;
				}
			}
		}

		return downLoadInfo;
	}

	/*
	 * 管理线程
	 */
	private void threadManage() {
		DownLoadInfo downLoadInfo = new DownLoadInfo();

		while (!mbExit) {
			try {
				downLoadInfo = getNextDownLoad();

				if (downLoadInfo != null) {
					mHttpDownLoad.download(downLoadInfo);

					updateDownload(downLoadInfo);
				}
			} catch (Exception e) {
			}

			try {
				Thread.sleep(512);
			} catch (Exception e) {
			}
		}
	}

	/*
	 * 销毁
	 */
	public void destroy() {
		mbExit = true;
		mHttpDownLoad.forceCancel();
	}

	/*
	 * 获取缓冲目录路径
	 */
	private String getPath() {
		return FileUtils.VOICE;
	}

	/*
	 * 缓冲目录检查
	 */
	private int getMusicId(File file) {
		int nValue = 0;

		try {
			nValue = Integer.parseInt(file.getName());
		} catch (Exception e) {
			nValue = 0;
		}

		return nValue;
	}

	/*
	 * 获取下载列表
	 */
	private List<DownLoadInfo> getAllDownLoad() {
		List<DownLoadInfo> listDownLoadInfo = null;
		File file = null, fileList[] = null;
		String strPath = "";
		int nValue = 0;

		try {
			strPath = FileUtils.getSDPath();

			listDownLoadInfo = new ArrayList<DownLoadInfo>();
			file = new File(strPath);

			if (file.exists()) {
				if (file.isDirectory()) {
					fileList = file.listFiles();

					for (int i = 0; i < fileList.length; ++i) {
						nValue = getMusicId(fileList[i]);

						if (nValue > 0) {

						}
					}
				} else {
					FileUtils.delFile(getPath());

					file.mkdir();
				}
			} else {
				file.mkdir();
			}
		} catch (Exception e) {
			listDownLoadInfo = null;
		}

		return listDownLoadInfo;
	}
}