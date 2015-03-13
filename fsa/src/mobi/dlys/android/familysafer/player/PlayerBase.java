package mobi.dlys.android.familysafer.player;

/*
 * 播放器
 */
public abstract class PlayerBase {

	public abstract int getCurrentPosition(); // 获取播放位置

	public abstract int getBufferDuration(); // 获取缓冲总长度

	public abstract int getDuration(); // 获取总长度

	public abstract int getPlayState(); // 获取播放状态

	public abstract void setPosition(int nPosition); // 设置播放位置

	public abstract void forceStop(); // 强制停止播放

	public abstract int isPlaying(); // 是否播放状态

	public abstract byte[] getFFTData(); // 获取频谱数据

	public abstract void destroy(); // 销毁

	public abstract void play(); // 播放

	public abstract void pause(); // 暂停

	public abstract void stop(); // 停止

	public abstract void clear(); // 清除

	public abstract void buffer(PlayerMusicInfo musicInfo); // 缓冲歌曲
}
