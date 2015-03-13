/**
 * BaseException.java
 *
 * Copyright 2014 cokosoft.com. All Rights Reserved.
 */
package mobi.dlys.android.core.exception;

/**
 * 基础异常. User: Wangle<87292008@qq.com> Date: 2014/5/16 0016 Time: 11:20
 */
@SuppressWarnings("serial")
public class BaseException extends RuntimeException {

	public BaseException(String detailMessage) {
		super(detailMessage);
	}

	public BaseException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public BaseException(Throwable throwable) {
		super(throwable);
	}
}
