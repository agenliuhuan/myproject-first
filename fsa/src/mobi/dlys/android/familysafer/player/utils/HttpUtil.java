package mobi.dlys.android.familysafer.player.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import mobi.dlys.android.familysafer.utils.NetUtils;
import android.text.TextUtils;

public class HttpUtil {
	private static final int HTTP_PORT = 80;

	public static HttpURLConnection getHttpURLConnection(String path) throws MalformedURLException, IOException {
		HttpURLConnection httpURLConnection = null;

		if (TextUtils.isEmpty(path)) {
			return null;
		}
		StringBuilder newPath = new StringBuilder();
		for (int i = 0, len = path.length(); i < len; i++) {
			String ch = path.substring(i, i + 1);
			if (ch.matches("[%/:=&?]+")) {
				newPath.append(ch);
			} else {
				newPath.append(URLEncoder.encode(ch, "UTF-8").replace("+", "%20"));
			}
		}
		path = newPath.toString();

		if (NetUtils.NET_CONNECTED == NetUtils.checkNet()) {
			URL url = new URL(path);
			if (NetUtils.isProxy) {
				String remoteHost = url.getHost(), onlineHost;
				int remotePort = url.getPort();
				if (remotePort != -1) {
					onlineHost = remoteHost + ":" + remotePort;
					// URL带Port
					path = path.replace(onlineHost, NetUtils.proxy + ":" + HTTP_PORT);
				} else {
					onlineHost = remoteHost;
					// URL不带Port
					path = path.replace(onlineHost, NetUtils.proxy + ":" + HTTP_PORT);
				}

				url = new URL(path);
				httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestProperty("X-Online-Host", onlineHost);
				httpURLConnection.setRequestProperty("Accept-Encoding", "identity"); // 设置http请求不要gzip压缩
			} else {
				httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestProperty("Accept-Encoding", "identity"); // 设置http请求不要gzip压缩
			}
		}
		return httpURLConnection;
	}
}
