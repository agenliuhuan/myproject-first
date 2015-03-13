package mobi.dlys.android.familysafer.audio;

import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class LocalPlayer {
	private MediaPlayer mPlayer = null;

	public void startPlay(String playFilePath, OnCompletionListener listener) {
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setOnCompletionListener(listener);
			mPlayer.setDataSource(playFilePath);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IOException e) {
		}
	}

	public void stopPlay() {
		if (null != mPlayer) {
			mPlayer.release();
			mPlayer = null;
		}
	}
}