/*
 * 文件名称 : InvokeUtil.java
 * <p>
 * 作者信息 : admin
 * <p>
 * 创建时间 : 2013-10-16, 下午7:37:11
 * <p>
 * 版权声明 : Copyright (c) 2012-2013 Xunlei Network Ltd. All rights reserved
 * <p>
 * 评审记录 :
 * <p>
 */

package mobi.dlys.android.core.net.extendcmp.ojm.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;

/**
 * 请在这里增加文件描述
 * <p>
 */
public class InvokeUtil {
	public static Object getFieldValue(Object obj, Field field) {

		// String methodName = getGetterName(field);
		Object value = null;
		try {

			field.setAccessible(true);
			value = field.get(obj);

			// Method method = obj.getClass().getDeclaredMethod(methodName, new
			// Class[] {});
			// value = method.invoke(obj, new Object[] {});
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return value;
	}

	public static void setFieldValue(Object receiver, Field field,
			Object fieldValue) {

		// String setMethod = getSetterName(field);
		try {
			// Method method = receiver.getClass().getDeclaredMethod(setMethod,
			// field.getType());
			// method.invoke(receiver, new Object[] { fieldValue });

			field.setAccessible(true);
			field.set(receiver, fieldValue);

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static String getGetterName(Field field) {
		String name = null;
		Class<?> type = field.getType();
		String fieldName = field.getName();
		if (boolean.class.equals(type) || Boolean.class.equals(type)) {
			// boolean类型的查询接口通常定义为is开头
			name = "is" + getName(fieldName);
		} else {
			// 其他类型的属性的查询接口通常定义为get开头
			name = "get" + getName(fieldName);
		}

		return name;
	}

	public static String getSetterName(Field field) {
		String name = null;
		String fieldName = field.getName();
		// 所有类型的属性的设置接口通常定义为set开头
		name = "set" + getName(fieldName);

		return name;
	}

	private static String getName(String fieldName) {
		String name = null;
		if (fieldName.length() >= 2) {
			char c0 = fieldName.charAt(0);
			char c1 = fieldName.charAt(1);
			// 形如 mName，第一个字母小写，第二个字母大写。
			if (Character.isLowerCase(c0) && Character.isUpperCase(c1)) {
				name = fieldName;
			} else {
				// 其他编码习惯,把第一个字母转为大写，其他不变。
				name = Character.toUpperCase(c0) + fieldName.substring(1);
			}

		} else {
			// 如果只有一个字母，则直接转为大写
			name = fieldName.toUpperCase();
		}
		return name;
	}

	public static Object[] toArray(Object array) {
		Object[] obj = null;
		if (int[].class.equals(array.getClass())) {
			int[] t = (int[]) array;
			obj = new Integer[t.length];
			for (int i = 0; i < t.length; i++) {
				obj[i] = t[i];
			}
		} else if (short[].class.equals(array.getClass())) {
			short[] t = (short[]) array;
			obj = new Short[t.length];
			for (int i = 0; i < t.length; i++) {
				obj[i] = t[i];
			}
		} else if (long[].class.equals(array.getClass())) {
			long[] t = (long[]) array;
			obj = new Long[t.length];
			for (int i = 0; i < t.length; i++) {
				obj[i] = t[i];
			}
		} else if (boolean[].class.equals(array.getClass())) {
			boolean[] t = (boolean[]) array;
			obj = new Boolean[t.length];
			for (int i = 0; i < t.length; i++) {
				obj[i] = t[i];
			}
		} else if (byte[].class.equals(array.getClass())) {
			byte[] t = (byte[]) array;
			obj = new Byte[t.length];
			for (int i = 0; i < t.length; i++) {
				obj[i] = t[i];
			}
		} else if (float[].class.equals(array.getClass())) {
			float[] t = (float[]) array;
			obj = new Float[t.length];
			for (int i = 0; i < t.length; i++) {
				obj[i] = t[i];
			}
		} else if (double[].class.equals(array.getClass())) {
			double[] t = (double[]) array;
			obj = new Double[t.length];
			for (int i = 0; i < t.length; i++) {
				obj[i] = t[i];
			}
		} else {
			obj = (Object[]) array;
		}

		return obj;
	}

	@SuppressWarnings("unchecked")
	public static <T> T warpArray(Object[] array, Class<?> tobuild)
			throws InstantiationException, IllegalAccessException {

		T t = null;

		if (int[].class.equals(tobuild)) {
			int[] temp = new int[array.length];
			for (int i = 0; i < array.length; i++) {
				temp[i] = (Integer) array[i];
			}
			t = (T) temp;
		} else if (short[].class.equals(tobuild)) {
			short[] temp = new short[array.length];
			for (int i = 0; i < array.length; i++) {
				temp[i] = (Short) array[i];
			}
			t = (T) temp;
		} else if (long[].class.equals(tobuild)) {
			long[] temp = new long[array.length];
			for (int i = 0; i < array.length; i++) {
				temp[i] = (Long) array[i];
			}
			t = (T) temp;
		} else if (boolean[].class.equals(tobuild)) {
			boolean[] temp = new boolean[array.length];
			for (int i = 0; i < array.length; i++) {
				temp[i] = (Boolean) array[i];
			}
			t = (T) temp;
		} else if (byte[].class.equals(tobuild)) {
			byte[] temp = new byte[array.length];
			for (int i = 0; i < array.length; i++) {
				temp[i] = (Byte) array[i];
			}
			t = (T) temp;
		} else if (float[].class.equals(tobuild)) {
			float[] temp = new float[array.length];
			for (int i = 0; i < array.length; i++) {
				temp[i] = (Float) array[i];
			}
			t = (T) temp;
		} else if (double[].class.equals(tobuild)) {
			double[] temp = new double[array.length];
			for (int i = 0; i < array.length; i++) {
				temp[i] = (Double) array[i];
			}
			t = (T) temp;
		} else {

			Class<?> componentType = tobuild.getComponentType();
			Object[] temp = (Object[]) Array.newInstance(componentType,
					array.length);
			for (int i = 0; i < array.length; i++) {
				temp[i] = array[i];
			}
			t = (T) temp;
		}

		return t;
	}

	/**
	 * 校验一个Class是否为另一个Class的子类。 通常用于校验child类是否实现了parent接口或者继承了parent类。
	 * 
	 * @param parent
	 * @param child
	 *            被校验的类
	 * @return 如果child是parent的子类则返回true，否则返回false。
	 */
	public static boolean isChild(Class<?> parent, Class<?> child) {
		try {
			child.asSubclass(parent);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static void getAllField(Class<?> clz, List<Field> list) {

		Field[] fields = clz.getDeclaredFields();
		for (int i = 0; null != fields && i < fields.length; i++) {
			list.add(fields[i]);
		}

		// 父类的字段
		Class<?> c = clz.getSuperclass();
		if (null != c) {
			getAllField(c, list);
		}

		// 接口的字段
		Class<?>[] interfaces = clz.getInterfaces();
		if (null != interfaces) {
			for (Class<?> intf : interfaces) {
				getAllField(intf, list);
			}
		}
	}

	public static boolean isDefaultValueOnNum(Class<?> clz, Object value) {
		if (int.class.equals(clz)) {
			int a = (Integer) value;
			if (a == -1) {
				return true;
			}
		} else if (short.class.equals(clz)) {
			short a = (Short) value;
			if (a == -1) {
				return true;
			}
		} else if (long.class.equals(clz)) {
			long a = (Long) value;
			if (a == -1) {
				return true;
			}
		} else if (byte.class.equals(clz)) {
			byte a = (Byte) value;
			if (a == -1) {
				return true;
			}
		} else if (float.class.equals(clz)) {
			float a = (Float) value;
			if (a == -1f) {
				return true;
			}
		} else if (double.class.equals(clz)) {
			double a = (Double) value;
			if (a == -1d) {
				return true;
			}
		}

		return false;
	}

}
