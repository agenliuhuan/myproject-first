/**
 * AssertUtils.java
 *
 * Copyright 2014 dlys.mobi All Rights Reserved.
 */
package mobi.dlys.android.core.utils;

/**
 * 断言工具. User: Wangle<87292008@qq.com> Date: 2014/5/16 0016 Time: 10:54
 */
public final class AssertUtils {

	public static void hasText(String text) {
		hasText(text,
				"[Assertion failed] - this String argument must have text; it must not be null, empty, or blank");
	}

	/**
	 * 判断入参文本不为空,如果为空则报参数不合法异常.
	 */
	public static void hasText(String text, String message) {
		if (!StringUtils.hasText(text)) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void isTrue(boolean expression) {
		isTrue(expression, "[Assertion failed] - this expression must be true");
	}

	/**
	 * 判断入参条件为真,如果不为真,则报参数不合法异常.
	 */
	public static void isTrue(boolean expression, String message) {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void notNull(Object object) {
		notNull(object,
				"[Assertion failed] - this argument is required; it must not be null");
	}

	/**
	 * 判断入参对象不为空,如果为空则报参数不合法异常.
	 */
	public static void notNull(Object object, String message) {
		if (null == object) {
			throw new IllegalArgumentException(message);
		}
	}

	private AssertUtils() {
	}
}
