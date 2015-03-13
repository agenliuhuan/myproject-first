/*
 * 文件名称 : OJM.java
 * <p>
 * 作者信息 : admin
 * <p>
 * 创建时间 : 2013-10-15, 下午3:17:32
 * <p>
 * 版权声明 : Copyright (c) 2012-2013 Xunlei Network Ltd. All rights reserved
 * <p>
 * 评审记录 :
 * <p>
 */

package mobi.dlys.android.core.net.extendcmp.ojm;

import org.json.JSONException;

/**
 * 
 * Object JSon Mapping转换接口
 * 
 * @author yangqiang
 * 
 */
public interface OJM {

	/**
	 * 将Json字符串转换为Java对象
	 * 
	 * @param json
	 *            json字符串
	 * @param clz
	 *            目标对象类型，可以是基本类型、扩展Object, 如果是集合类型，请使用fromJson(String json,
	 *            Class<T> clz)方法。
	 * @return 目标对象
	 * @throws IllegalAccessException
	 *             如果clz的属性没有对应的标准get和set方法，则抛出该异常。属性的get和set方法请使用eclipse自动生成：
	 *             右键工作区 --> Source --> Generate getter and setter --> ...
	 * @throws InstantiationException
	 *             如果clz类不存在默认的构造函数，则抛出该异常。
	 * @throws JSONException
	 *             如果Json格式有问题，则抛出该异常。
	 */
	public <T> T fromJson(String json, Class<T> clz) throws JSONException,
			InstantiationException, IllegalAccessException;

	/**
	 * 将Json字符串转换为Java对象
	 * 
	 * @param json
	 *            json字符串
	 * @param token
	 *            目标对象类型的描述信息，可以是基本类型、扩展Object、也可以是List。
	 * @return
	 * @throws JSONException
	 *             如果Json格式有问题，则抛出该异常。
	 * @throws InstantiationException
	 *             如果clz类不存在默认的构造函数或者不可实例化，则抛出该异常。
	 * @throws IllegalAccessException
	 *             如果clz的属性没有对应的标准get和set方法，则抛出该异常。属性的get和set方法请使用eclipse自动生成：
	 *             右键工作区 --> Source --> Generate getter and setter --> ...
	 */
	public <T> T fromJson(String json, TypedToken token) throws JSONException,
			InstantiationException, IllegalAccessException;

	/**
	 * 将Java Object转换为JSon字符串
	 * 
	 * @param obj
	 *            Java Object
	 * @return Java Object 对应的 JSon字符串
	 */
	public String toJson(Object obj);

}
