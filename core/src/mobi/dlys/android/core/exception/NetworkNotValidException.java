/**
 * NetworkNotValidException.java
 *
 * Copyright 2014 cokosoft.com. All Rights Reserved.
 */
package mobi.dlys.android.core.exception;

/**
 * 网络无效异常. User: Wangle<87292008@qq.com> Date: 2014/5/16 0016 Time: 11:21
 */
@SuppressWarnings("serial")
public class NetworkNotValidException extends BaseException {

	public NetworkNotValidException() {
		super("当前网络不可用");
	}
}
