package mobi.dlys.android.familysafer.audio;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import mobi.dlys.android.familysafer.utils.FileUtils;
import android.media.MediaRecorder;

public class Recorder {
	private MediaRecorder mRecorder = null;

	public void startRecord(String audioFilePath) {
		try {
			mRecorder = new MediaRecorder();
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
			mRecorder.setOutputFile(audioFilePath);
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

			mRecorder.prepare();
			mRecorder.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void stopRecord() {
		try {
			if (null != mRecorder) {

				mRecorder.stop();
				mRecorder.release();
				mRecorder = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getVoiceFilePath() {
		String strVoicePath = FileUtils.VOICE;
		File out = new File(strVoicePath);
		if (!out.exists()) {
			out.mkdirs();
		}
		if (!out.exists()) {
			return "";
		}
		String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + FileUtils.AMR;
		return strVoicePath + fileName;
	}
}