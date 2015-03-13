package mobi.dlys.android.familysafer.player.service;

import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.familysafer.player.PlayerManage;
import mobi.dlys.android.familysafer.player.PlayerMusicInfo;
import mobi.dlys.android.familysafer.player.download.DownloadManage;
import mobi.dlys.android.familysafer.player.entity.MusicInfoEntity;
import mobi.dlys.android.familysafer.player.entity.MusicInfoListEntity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

/*
 * 播放服务类
 */
public class PlayerService extends Service {
	public static PlayerManage mPlayerManage = null; // 播放管理
	private DownloadManage mDownloadManage = null; // 下载管理

	/*
	 * aidl接口
	 */
	PlayerInterface.Stub mStub = new PlayerInterface.Stub() {
		/*
		 * 播放
		 */
		public void IPlay() throws RemoteException {
			mPlayerManage.play();
		}

		/*
		 * 上一首
		 */
		public void IPrev() throws RemoteException {
			mPlayerManage.prev();
		}

		/*
		 * 下一首
		 */
		public void INext() throws RemoteException {
			mPlayerManage.next();
		}

		/*
		 * 本地音乐下一首
		 */
		public void ILocalNext() throws RemoteException {
			mPlayerManage.localNext();
		}

		/*
		 * 播放完成后下一首
		 */
		public void IFinishNext() throws RemoteException {
			mPlayerManage.finishNext();
		}

		/*
		 * 暂停
		 */
		public void IPause() throws RemoteException {
			mPlayerManage.pause();
		}

		/*
		 * 停止
		 */
		public void IStop() throws RemoteException {
			mPlayerManage.stop();
		}

		/*
		 * 清除
		 */
		public void IClear() throws RemoteException {
			mPlayerManage.clear();
		}

		/*
		 * 是否正在播放
		 */
		public int IIsPlaying() throws RemoteException {
			return mPlayerManage.isPlaying();
		}

		/*
		 * 是否正在播放
		 */
		public int IGetPlayState() throws RemoteException {
			return mPlayerManage.getPlayState();
		}

		/*
		 * 删除播放列表
		 */
		public void IDeleteMusicList(int nMusicID) {
			mPlayerManage.deleteMusic(nMusicID);
		}

		/*
		 * 添加音乐
		 */
		public void IAddMusic(int nMusicID) {
			mPlayerManage.addMusic(nMusicID);
		}

		/*
		 * 替换当前音乐
		 */
		public void IReplaceMusic(int nMusicID) {
			mPlayerManage.replaceMusic(nMusicID);
		}

		/*
		 * 插入音乐
		 */
		public void IInsertPrevMusic(int nMusicID) {
			mPlayerManage.insertPrevMusic(nMusicID);
		}

		/*
		 * 替换当前音乐
		 */
		public void IReplaceMusicInfo(PlayerMusicInfo musicInfo) {
			mPlayerManage.replaceMusic(musicInfo);
		}

		/*
		 * 插入音乐
		 */
		public void IInsertPrevMusicInfo(PlayerMusicInfo musicInfo) {
			mPlayerManage.insertPrevMusicInfo(musicInfo);
		}

		/*
		 * 添加音乐
		 */
		public void IAddMusicInfo(PlayerMusicInfo musicInfo) {
			mPlayerManage.addMusicInfo(musicInfo);
		}

		/*
		 * 添加播放列表
		 */
		public void IAddLocalMusic(MusicInfoListEntity musicInfoListEntity, int nIndex) throws RemoteException {
			List<PlayerMusicInfo> listMusicInfo = new ArrayList<PlayerMusicInfo>();
			List<MusicInfoEntity> listMusicInfoEntity = null;
			PlayerMusicInfo musicInfo = null;

			listMusicInfoEntity = musicInfoListEntity.getMusicInfoEntity();

			for (int i = 0; i < listMusicInfoEntity.size(); ++i) {
				musicInfo = new PlayerMusicInfo();

				PlayerMusicInfo.Convert(musicInfo, listMusicInfoEntity.get(i));

				listMusicInfo.add(musicInfo);
			}

			mPlayerManage.addLocalMusic(listMusicInfo, nIndex);
		}

		/*
		 * 获取音乐信息
		 */
		public void IGetMusicInfo(MusicInfoEntity musicInfoEntity) throws RemoteException {
			PlayerMusicInfo musicInfo = new PlayerMusicInfo();

			mPlayerManage.getMusicInfo(musicInfo);

			PlayerMusicInfo.Convert(musicInfoEntity, musicInfo);
		}

		/*
		 * 获取播放时长
		 */
		public int IGetDuration() throws RemoteException {
			return mPlayerManage.getDuration();
		}

		/*
		 * 获取播放位置
		 */
		public int IGetCurrentPosition() throws RemoteException {
			return mPlayerManage.getCurrentPosition();
		}

		/*
		 * 获取缓冲位置
		 */
		public int IGetBufferDuration() throws RemoteException {
			return mPlayerManage.getBufferDuration();
		}

		/*
		 * 设置播放位置
		 */
		public void ISetPosition(int nPosition) throws RemoteException {
			mPlayerManage.setPosition(nPosition);
		}

		/*
		 * 获取播放位置
		 */
		public int IGetLoopStyle() throws RemoteException {
			return mPlayerManage.getLoopStyle();
		}

		/*
		 * 设置播放位置
		 */
		public void ISetLoopStyle(int nLoopStyle) throws RemoteException {
			mPlayerManage.setLoopStyle(nLoopStyle);
		}

		/*
		 * 设置下一个循环方式
		 */
		public int ISetNextLoopStyle() throws RemoteException {
			return mPlayerManage.setNextLoopStyle();
		}

		/*
		 * 获取频谱数据
		 */
		public byte[] IGetFFTData() throws RemoteException {
			return mPlayerManage.getFFTData();
		}

		/*
		 * 添加下载列表
		 */
		public void IAddDownloadMusic(MusicInfoEntity musicInfoEntity) throws RemoteException {
			mDownloadManage.addDownload(musicInfoEntity);
		}

		/*
		 * 删除下载列表
		 */
		public void IDeleteDownloadMusic(int nMusicID, int nMsgID) throws RemoteException {
			mDownloadManage.deleteDownload(nMusicID, nMsgID);
		}

		/*
		 * 暂停下载
		 */
		public void IPauseDownloadMusic(int nMusicID) throws RemoteException {
			mDownloadManage.pauseDownload(nMusicID);
		}

		/*
		 * 恢复下载
		 */
		public void IResumeDownloadMusic(int nMusicID) throws RemoteException {
			mDownloadManage.resumeDownload(nMusicID);
		}

	};

	/*
	 * 邦定
	 */
	public IBinder onBind(Intent arg0) {
		return mStub;
	}

	/*
	 * 创建
	 */
	public void onCreate() {
		super.onCreate();

		mPlayerManage = new PlayerManage(this);
		mDownloadManage = new DownloadManage(this);
	}

	/*
	 * 销毁
	 */
	public void onDestroy() {
		super.onDestroy();

		mPlayerManage.destroy();
		mDownloadManage.destroy();

		try {
			Thread.sleep(1109);
		} catch (Exception e) {
		}
	}

	/*
	 * 启动
	 */
	@SuppressWarnings("deprecation")
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	/*
	 * 命令执行
	 */
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		return START_STICKY;
	}
}