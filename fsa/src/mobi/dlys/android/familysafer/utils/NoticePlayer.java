package mobi.dlys.android.familysafer.utils;

import mobi.dlys.android.familysafer.R;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

public class NoticePlayer {
	static SoundPool spool;

	public static void playA(final Context context) {
		spool = new SoundPool(1, AudioManager.STREAM_RING, 100);
		final int aid = spool.load(context, R.raw.noticea, 1);
		spool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			public void onLoadComplete(SoundPool arg0, int arg1, int arg2) {
				AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
				float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_RING);
				float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_RING);
				float volumnRatio = audioCurrentVolumn / audioMaxVolumn;
				spool.play(aid, volumnRatio, volumnRatio, 1, -1, 1f);
			}
		});
	}

	public static void playB(final Context context) {
		spool = new SoundPool(1, AudioManager.STREAM_RING, 100);
		final int bid = spool.load(context, R.raw.noticeb, 1);
		spool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			public void onLoadComplete(SoundPool arg0, int arg1, int arg2) {
				AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
				float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_RING);
				float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_RING);
				float volumnRatio = audioCurrentVolumn / audioMaxVolumn;
				spool.play(bid, volumnRatio, volumnRatio, 1, 0, 1);
			}
		});
	}
	
	public static void playCheckin(final Context context) {
        spool = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
        final int bid = spool.load(context, R.raw.firstcheckin, 1);
        spool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            public void onLoadComplete(SoundPool arg0, int arg1, int arg2) {
                AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                float volumnRatio = audioCurrentVolumn / audioMaxVolumn;
                spool.play(bid, volumnRatio, volumnRatio, 1, 0, 1);
            }
        });
    }
	
	public static void playClueUpload(final Context context) {
        spool = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
        final int bid = spool.load(context, R.raw.clueupload, 1);
        spool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            public void onLoadComplete(SoundPool arg0, int arg1, int arg2) {
                AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                float volumnRatio = audioCurrentVolumn / audioMaxVolumn;
                spool.play(bid, volumnRatio, volumnRatio, 1, 0, 1);
            }
        });
    }

	public static void stop() {
		spool.autoPause();
	}

}
