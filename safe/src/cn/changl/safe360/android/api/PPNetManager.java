package cn.changl.safe360.android.api;

import java.io.IOException;

import mobi.dlys.android.core.exception.NetworkNotValidException;
import mobi.dlys.android.core.utils.AndroidUtils;
import mobi.dlys.android.core.utils.NetworkUtils;
import android.content.Context;
import android.text.TextUtils;
import cn.changl.safe360.android.App;

import com.google.protobuf.Message;

public class PPNetManager {
	private static final String TAG = PPNetManager.class.getSimpleName();

	private static final int CONNECT_TIME_LIMIT = 20000;
	private static final int SO_TIMEOUT = 20000;

	// 阿里云服务器地址
	// private static final String IP = "api.dlys.mobi";
	// private static final int PORT = 20001;
	// private static final String UPLOAD_IP = IP;

	// 内部测试服务器
	private static final String IP = "api.anntu.net"; // "121.41.82.235";
	private static final int PORT = 10001;
	public static final String IMAGE_DOMAIN = "img0.anntu.net";
	public static final String IMAGE_DOMAIN2 = "img0.anntu.cn";
	private static final String UPLOAD_IP = "121.41.82.235:82";

	public static final String HTTP = "http://";
	private static final String AVATAR_URL_SUFFIX = "/res/upload-user-face";
	private static final String CLUE_IMAGE_URL_SUFFIX = "/res/upload-clue-image";
	private static final String SOS_VOICE_URLSUFFIX = "/res/upload-sos-voice";

	private static final String APK_DOWNLOAD_PAGE = "http://t.cn/RZjQGRR";

	private Context mContext = null;
	private static PPNetManager mInstance = null;

	private int mConnectTimeOut = CONNECT_TIME_LIMIT;
	private int mSoTimeOut = SO_TIMEOUT;

	private String ip = IP;
	private int port = PORT;
	private String uploadIpPort = UPLOAD_IP;

	private String downloadPage = APK_DOWNLOAD_PAGE;

	public static PPNetManager getInstance() {
		if (null == mInstance) {
			mInstance = new PPNetManager();
		}

		return mInstance;
	}

	public String getDownloadPage() {
		return downloadPage;
	}

	public void setDownloadPage(String downloadPage) {
		if (!TextUtils.isEmpty(downloadPage)) {
			this.downloadPage = downloadPage;
		}
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		if (!TextUtils.isEmpty(ip)) {
			this.ip = ip;
		}
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUploadAvatarUrl() {
		return HTTP + uploadIpPort + AVATAR_URL_SUFFIX;
	}

	public void setUploadIpPort(String uploadIpPort) {
		this.uploadIpPort = uploadIpPort;
	}

	public String getUploadClueImageUrl() {
		return HTTP + uploadIpPort + CLUE_IMAGE_URL_SUFFIX;
	}

	public String getUploadSOSVoiceUrl() {
		return HTTP + uploadIpPort + SOS_VOICE_URLSUFFIX;
	}

	public int getConnectTimeOut() {
		return mConnectTimeOut;
	}

	public void setConnectTimeOut(int connectTimeOut) {
		this.mConnectTimeOut = connectTimeOut;
	}

	public int getSoTimeOut() {
		return mSoTimeOut;
	}

	public void setSoTimeOut(int soTimeOut) {
		this.mSoTimeOut = soTimeOut;
	}

	public void resetTimeOut() {
		mConnectTimeOut = CONNECT_TIME_LIMIT;
		mSoTimeOut = SO_TIMEOUT;
	}

	protected void debug(String msg) {
		// LogUtils.d(TAG, msg);
	}

	@SuppressWarnings("unchecked")
	public <T extends Message> T executeNetworkInvoke(T message) {
		try {
			byte[] bytes = executeNetCall(message);
			T result = (T) message.newBuilderForType().mergeFrom(bytes).buildPartial();
			debug("网络调用结果=" + result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			// throw new RuntimeException(e.getMessage());
			return null;
		}
	}

	/**
	 * 执行网络调用，返回字节数组.
	 */
	private byte[] executeNetCall(Message message) throws IOException {
		if (!AndroidUtils.isNetworkValid(mContext)) {
			throw new NetworkNotValidException();
		}
		try {
			debug("网络调用参数=" + message);
			debug("网络调用地址=" + ip);
			debug("网络调用端口=" + port);
			byte[] bytes = NetworkUtils.execute(message, ip, port, mConnectTimeOut, mSoTimeOut);
			debug("网络调用结果字节数组=" + bytes);
			return bytes;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("网络不给力");
		}
	}

	private PPNetManager() {
		this.mContext = App.getInstance().getApplicationContext();
	}
}
