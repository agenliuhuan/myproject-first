/*
 * 文件名称 : OJMUtil.java
 * <p>
 * 作者信息 : admin
 * <p>
 * 创建时间 : 2013-10-16, 下午2:22:20
 * <p>
 * 版权声明 : Copyright (c) 2012-2013 Xunlei Network Ltd. All rights reserved
 * <p>
 * 评审记录 :
 * <p>
 */

package mobi.dlys.android.core.net.extendcmp.ojm.impl;

/**
 * 请在这里增加文件描述
 * <p>
 */
public class OJMUtil {

	/**
	 * 
	 * @param clz
	 * @return
	 */
	public static boolean isPrimitive(Class<?> clz) {
		return clz.isPrimitive();
	}

	public static String sampleType2Json(Object sample) {
		Class<?> clz = sample.getClass();
		String json = null;
		if (Integer.class.equals(clz) || Long.class.equals(clz)
				|| Short.class.equals(clz) || Boolean.class.equals(clz)
				|| String.class.equals(clz) || Byte.class.equals(clz)
				|| Float.class.equals(clz) || Double.class.equals(clz)) {
			json = "\"" + sample.toString() + "\"";
		}

		return json;
	}

	@SuppressWarnings("unchecked")
	public static <T> T json2SampleObject(String json, Class<T> clz) {
		try {
			T obj = null;
			if (int.class.equals(clz)) {

				obj = (T) Integer.valueOf(json);
			} else if (short.class.equals(clz)) {
				obj = (T) (Short) Short.parseShort(json);
			} else if (long.class.equals(clz)) {
				obj = (T) (Long) Long.parseLong(json);
			} else if (boolean.class.equals(clz)) {
				obj = (T) (Boolean) Boolean.parseBoolean(json);
			} else if (byte.class.equals(clz)) {
				obj = (T) (Byte) (byte) Integer.parseInt(json);
			} else if (float.class.equals(clz)) {
				obj = (T) (Float) Float.parseFloat(json);
			} else if (double.class.equals(clz)) {
				obj = (T) (Double) Double.parseDouble(json);
			} else if (String.class.equals(clz)) {
				obj = (T) (String) new String(json);
			}
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
