package mobi.dlys.android.familysafer.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.UUID;

import mobi.dlys.android.core.net.extendcmp.ojm.OJMFactory;
import mobi.dlys.android.core.utils.LogUtils;
import mobi.dlys.android.familysafer.biz.vo.upload.UploadRespObject;
import mobi.dlys.android.familysafer.player.utils.HttpUtil;

import org.apache.http.HttpStatus;

import android.text.TextUtils;

public class HttpUploadHelper {
	private static final String TAG = "uploadFile";
	private static final int TIME_OUT = 20 * 1000; // 超时时间
	private static final String CHARSET = "utf-8"; // 设置编码
	private static final int UPLOAD_TRY_COUNT = 1; // 重试次数

	public static UploadRespObject upload(String actionUrl, File file, boolean voice) {
		String content = post(actionUrl, file, voice);
		if (!TextUtils.isEmpty(content)) {
			try {
				UploadRespObject uploadResp = OJMFactory.createOJM().fromJson(content, UploadRespObject.class);
				if (uploadResp != null && uploadResp.success && uploadResp.fileInfo != null) {
					return uploadResp;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 上传文件
	 * 
	 * @param actionUrl
	 * @param file
	 * @return
	 */
	private static String post(String actionUrl, File file, boolean voice) {
		if (TextUtils.isEmpty(actionUrl) || null == file) {
			return null;
		}
		String result = null;
		String type;
		boolean uploadSuccess = false;
		int tryTime = 0;
		if (voice) {
			type = "voice";
		} else {
			type = "image";
		}

		while (tryTime <= UPLOAD_TRY_COUNT && !uploadSuccess) {
			String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
			String PREFIX = "--", LINE_END = "\r\n";
			String CONTENT_TYPE = "multipart/form-data"; // 内容类型

			try {
				LogUtils.d("ApiClient", " \n");
				LogUtils.e("ApiClient", "***********************************");
				LogUtils.d("ApiClient", "上传请求数据:\n" + actionUrl);
				HttpURLConnection conn = HttpUtil.getHttpURLConnection(actionUrl);
				conn.setReadTimeout(TIME_OUT);
				conn.setConnectTimeout(TIME_OUT);
				conn.setDoInput(true); // 允许输入流
				conn.setDoOutput(true); // 允许输出流
				conn.setUseCaches(false); // 不允许使用缓存
				conn.setRequestMethod("POST"); // 请求方式
				conn.setRequestProperty("Charset", CHARSET); // 设置编码
				conn.setRequestProperty("connection", "keep-alive");
				conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

				if (file != null) {
					/**
					 * 当文件不为空，把文件包装并且上传
					 */
					DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
					StringBuffer sb = new StringBuffer();
					sb.append(PREFIX);
					sb.append(BOUNDARY);
					sb.append(LINE_END);
					/**
					 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
					 * filename是文件的名字，包含后缀名的 比如:abc.png
					 */
					sb.append("Content-Disposition: form-data; name=\"" + type + "\"; filename=\"" + file.getName() + "\"" + LINE_END);
					sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
					sb.append(LINE_END);
					dos.write(sb.toString().getBytes());
					InputStream is = new FileInputStream(file);
					byte[] bytes = new byte[1024];
					int len = 0;
					while ((len = is.read(bytes)) != -1) {
						dos.write(bytes, 0, len);
					}
					is.close();
					dos.write(LINE_END.getBytes());
					byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
					dos.write(end_data);
					dos.flush();
					/**
					 * 获取响应码 200=成功 当响应成功，获取响应的流
					 */
					int res = conn.getResponseCode();
					LogUtils.d("ApiClient", "上传返回结果:\n" + conn.getResponseMessage());
					if (HttpStatus.SC_OK == res) {
						InputStream input = conn.getInputStream();
						StringBuffer sb1 = new StringBuffer();
						int ss;
						while ((ss = input.read()) != -1) {
							sb1.append((char) ss);
						}
						result = sb1.toString();
						uploadSuccess = true;
					}
				}
			} catch (MalformedURLException e) {
				LogUtils.d("ApiClient", "上传返回结果:\n" + e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				LogUtils.d("ApiClient", "上传返回结果:\n" + e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				LogUtils.d("ApiClient", "上传返回结果:\n" + e.getMessage());
				e.printStackTrace();
			}
			LogUtils.e("ApiClient", "***********************************");
			tryTime += 1;
		}

		return result;
	}
}