/**
 * NetworkUtils.java
 *
 * Copyright 2014 dlys.mobi All Rights Reserved.
 */
package mobi.dlys.android.core.utils;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 网络工具类. User: Wangle<87292008@qq.com> Date: 2014/5/16 0016 Time: 11:07
 */
public final class NetworkUtils {

	public static byte[] execute(Message message, String url, int port,
			int timeout) throws IOException {
		return execute(message, url, port, timeout, null);
	}

	public static byte[] execute(Message message, String url, int port,
			int timeout, Integer soTimeout) throws IOException {
		Socket socket = null;
		try {
			socket = new Socket();
			InetSocketAddress isa = new InetSocketAddress(url, port);
			if (null != soTimeout) {
				socket.setSoTimeout(soTimeout);
			}
			socket.connect(isa, timeout);

			byte[] data = message.toByteArray();
			CodedOutputStream codedOut = CodedOutputStream.newInstance(socket
					.getOutputStream());
			codedOut.writeRawVarint32(data.length);
			codedOut.writeRawBytes(data);
			codedOut.flush();

			CodedInputStream codedIn = CodedInputStream.newInstance(socket
					.getInputStream());
			int length = codedIn.readRawVarint32();
			byte[] buffer = codedIn.readRawBytes(length);
			return buffer;
		} finally {
			closeSocket(socket);
		}
	}

	public static void closeSocket(Socket socket) {
		if (null != socket) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * 判断ip能否连接.
	 */
	public static boolean isConnected(String ip, int port, int timeout) {
		Socket socket = null;
		try {
			socket = new Socket();
			InetSocketAddress isa = new InetSocketAddress(ip, port);
			socket.connect(isa, timeout);
			return !socket.isClosed() && socket.isConnected();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			closeSocket(socket);
		}
	}

	private NetworkUtils() {
	}
}
