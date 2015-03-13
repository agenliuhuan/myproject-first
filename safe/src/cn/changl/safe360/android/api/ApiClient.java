/**
 * ApiClient.java
 *
 * Copyright 2014 cokosoft.com. All Rights Reserved.
 */
package cn.changl.safe360.android.api;

import mobi.dlys.android.core.utils.LogUtils;
import android.text.TextUtils;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb;

import com.google.protobuf.Message;

/**
 * Api客户端. User: Wangle<87292008@qq.com> Date: 2014/6/9 0009 Time: 17:46
 */
public abstract class ApiClient {

	private static final String TAG = ApiClient.class.getSimpleName();

	protected static void debug(String msg) {
		LogUtils.d(TAG, msg);
	}

	protected static <T extends Message> T executeNetworkInvokeSimple(T message) {
		debug(" \n");
		LogUtils.e(TAG, "***********************************");
		debug("调用接口请求数据:\n" + message.toString());
		T rec = PPNetManager.getInstance().executeNetworkInvoke(message);
		if (rec instanceof Safe360Pb) {
			Safe360Pb.ResponseStatus status = ((Safe360Pb) rec).getResponseStatus();
			debug("调用接口返回结果:\n" + rec);
			String code = status.getCode();
			String msg = status.getMsg();
			debug("结果描述：" + ((TextUtils.isEmpty(msg)) ? "成功" : msg));
			LogUtils.e(TAG, "***********************************");
			if ((!TextUtils.isEmpty(code)) && code.equals("00000")) {
				return rec;
			} else {
				if (!TextUtils.isEmpty(code) && code.equals("1001")) {

				}
				// throw new BaseException(status.getMsg());
				return rec;
			}
		} else {
			// throw new BaseException("数据格式错误");
			return null;
		}
	}

	/**
	 * 判断请求是否成功
	 * 
	 * @param message
	 * @return
	 */
	public static boolean isOK(Message message) {
		if (message instanceof Safe360Pb) {
			Safe360Pb.ResponseStatus status = ((Safe360Pb) message).getResponseStatus();
			String code = status.getCode();
			if ((!TextUtils.isEmpty(code)) && code.equals("00000")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 判断token是否有效
	 * 
	 * @param message
	 * @return
	 */
	public static boolean isTokenValid(Message message) {
		if (message instanceof Safe360Pb) {
			Safe360Pb.ResponseStatus status = ((Safe360Pb) message).getResponseStatus();
			String code = status.getCode();
			if ((!TextUtils.isEmpty(code)) && code.equals("1003")) {
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}
}
