/*
 * 文件名称 : SampleOJMImpl.java
 * <p>
 * 作者信息 : admin
 * <p>
 * 创建时间 : 2013-10-15, 下午3:33:56
 * <p>
 * 版权声明 : Copyright (c) 2012-2013 Xunlei Network Ltd. All rights reserved
 * <p>
 * 评审记录 :
 * <p>
 */

package mobi.dlys.android.core.net.extendcmp.ojm.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import mobi.dlys.android.core.net.extendcmp.ojm.OJM;
import mobi.dlys.android.core.net.extendcmp.ojm.TypedToken;
import mobi.dlys.android.core.net.extendcmp.ojm.annotations.IgnoreOnToJson;
import mobi.dlys.android.core.net.extendcmp.ojm.annotations.RowType;
import mobi.dlys.android.core.net.extendcmp.ojm.util.InvokeUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * JSon and Object mapping implements. 1.对象转JSon调用toJson方法，传入这个对象即可得到对应的Json。
 * 2.JSon字符串转对象，调用fromJson方法，使用方法看具体的Method注释。
 * <p>
 */
public class SampleOJMImpl implements OJM {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T fromJson(String json, Class<T> clz) throws JSONException,
			InstantiationException, IllegalAccessException {
		TypedToken token = new TypedToken(clz);
		return (T) json2Obj(json, token);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T fromJson(String json, TypedToken token) throws JSONException,
			InstantiationException, IllegalAccessException {
		return (T) json2Obj(json, token);
	}

	/**
	 * 1.对象转JSon调用toJson方法，传入这个对象即可得到对应的Json。
	 */
	@Override
	public String toJson(Object obj) {
		return obj2json(obj);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T> T json2Obj(String json, TypedToken token) throws JSONException,
			InstantiationException, IllegalAccessException {
		T obj = null;
		Class<?> tobuild = token.getmType();
		// 如果转换结果是原始数据类型，则直接转换并返回。
		obj = (T) OJMUtil.json2SampleObject(json, tobuild);
		if (null != obj) {
			return obj;
		}

		if ("null".equals(json)) {
			return null;
		}

		// 如果是个数据
		if (tobuild.isArray()) {
			JSONArray array = new JSONArray(json);
			Object[] tempArray = new Object[array.length()];
			for (int i = 0; i < array.length(); i++) {
				String valueStr = array.getString(i);
				Object value = json2Obj(valueStr,
						new TypedToken(tobuild.getComponentType()));
				tempArray[i] = value;
			}
			// 包装数组，将Object类型的数组对象转换为目标类型的数组对象
			obj = (T) InvokeUtil.warpArray(tempArray, tobuild);

			return obj;
		}

		// 如果这是一个集合
		if (InvokeUtil.isChild(Collection.class, tobuild)) {

			// 如果不是接口，这直接创建对象
			if (!tobuild.isInterface()) {
				obj = (T) tobuild.newInstance();
			} else if (InvokeUtil.isChild(List.class, tobuild)) { // 创建相应的集合对象
				obj = (T) new ArrayList();
			} else if (InvokeUtil.isChild(Set.class, tobuild)) {
				obj = (T) new HashSet();
			} else if (InvokeUtil.isChild(Queue.class, tobuild)) {
				obj = (T) new LinkedList();
			} else {
				return null;
			}

			Collection collection = (Collection) obj;

			// 创建Json数组
			JSONArray array = new JSONArray(json);
			for (int i = 0; i < array.length(); i++) {
				// 获取Json数组的每一个组件
				String js = array.getString(i);
				// 将这个Json组件封装成一个对象。
				Object item = json2Obj(js, new TypedToken(token.getmRowType()));
				collection.add(item);
			}

			return obj;
		}

		JSONObject jobj = new JSONObject(json);

		// 创建这个对象
		obj = (T) tobuild.newInstance();
		// 获取这个对象声明的属性数组
		// Field[] fields = tobuild.getDeclaredFields();
		List<Field> list = new ArrayList<Field>();
		InvokeUtil.getAllField(tobuild, list);

		for (int j = 0; j < list.size(); j++) {
			Field field = list.get(j);
			// 属性的名字和类型
			String fieldName = field.getName();
			Class<?> filedType = field.getType();

			// 这个属性在Json中对应的字符串值
			String valueStr = jobj.optString(fieldName);
			// 判断valueStr是否为null或者长度为0(去掉首尾空格和换行符)
			if (null != valueStr && (valueStr = valueStr.trim()).length() > 0) {
				// 递归：解析这个属性的值。
				Object valueObj = null;
				// 这个属性是集合
				if (InvokeUtil.isChild(Collection.class, filedType)) {
					RowType rowType = field.getAnnotation(RowType.class);
					if (null == rowType) {
						throw new RuntimeException(
								"you must declear the RowType of field "
										+ fieldName + " in class "
										+ token.getmType().getName());
					}
					Class<?> rt = rowType.clz();
					valueObj = json2Obj(valueStr, new TypedToken(filedType, rt));
				} else {
					valueObj = json2Obj(valueStr, new TypedToken(filedType));
				}

				// 将属性的值设置到对象o中
				InvokeUtil.setFieldValue(obj, field, valueObj);
			}
		}

		return obj;
	}

	/**
	 * 获取该对象的Json字符串。 如果obj有嵌套的对象，将通过递归调用该方法继续获取Json字符串，知道基本数据类型位置。
	 * 
	 * @param obj
	 * @return
	 */
	private String obj2json(Object obj) {

		Class<?> clz = obj.getClass();
		StringBuffer sb = new StringBuffer(32);
		String json = null;
		if ((json = OJMUtil.sampleType2Json(obj)) != null) {
			return json;
		}

		// 该对象是否为数组
		if (clz.isArray()) {
			Object[] array = InvokeUtil.toArray(obj);
			sb.append("[");
			for (int i = 0; i < array.length; i++) {
				Object o = array[i];
				sb.append(obj2json(o));
				if (i < array.length - 1) {
					sb.append(",");
				}
			}
			sb.append("]");
			return sb.toString();
		}

		// 该对象是否为集合类
		try {
			Collection<?> co = Collection.class.cast(obj);
			sb.append("[");
			Iterator<?> iter = co.iterator();
			while (iter.hasNext()) {
				Object o = iter.next();
				sb.append(obj2json(o));
				if (iter.hasNext()) {
					sb.append(",");
				}
			}
			sb.append("]");
			return sb.toString();
		} catch (ClassCastException c) {
		}

		// 处理对象

		ArrayList<Field> temp = new ArrayList<Field>();
		InvokeUtil.getAllField(clz, temp);

		// 剔除一些不符合规则的字段。
		List<Field> fields = new ArrayList<Field>();

		for (int i = 0; i < temp.size(); i++) {
			Field f = temp.get(i);
			// 标记为Ignore的字段，不生产为Json
			IgnoreOnToJson ignore = f.getAnnotation(IgnoreOnToJson.class);
			if (null != ignore) {
				continue;
			}

			// 忽略这个三个字段
			String name = f.getName();
			if ("errorCode".equals(name) || "errorMsg".equals(name)
					|| "serialVersionUID".equals(name)) {
				continue;
			}

			try {
				// 如果该字段是static的，则忽略
				String genericType = f.toGenericString();
				if (null == genericType
						|| (genericType.contains("static") && genericType
								.contains("final"))) {
					continue;
				}
			} catch (TypeNotPresentException e) {
				continue;
			}

			fields.add(f);
		}

		sb.append("{");
		for (int i = 0; i < fields.size(); i++) {
			Field field = fields.get(i);
			String name = field.getName();

			Object o = InvokeUtil.getFieldValue(obj, field);
			// 属性值不为null，并且，如果属性类型是数值类型，则不能为默认值-1； 否则不要生成json
			if (null != o
					&& !InvokeUtil.isDefaultValueOnNum(field.getType(), o)) {
				sb.append("\"").append(name).append("\"").append(":");
				// 通过递归再次获取对象的Json字符串
				String value = obj2json(o);
				if (OJMUtil.isPrimitive(o.getClass())) {
					sb.append("\"").append(value).append("\"");
				} else {
					sb.append(value);
				}

				if (i < fields.size() - 1) {
					sb.append(",");
				}
			}
		}
		sb.append("}");

		return sb.toString();
	}
}
