/*
 * 文件名称 : TypedToken.java
 * <p>
 * 作者信息 : admin
 * <p>
 * 创建时间 : 2013-10-21, 下午2:26:38
 * <p>
 * 版权声明 : Copyright (c) 2012-2013 Xunlei Network Ltd. All rights reserved
 * <p>
 * 评审记录 :
 * <p>
 */

package mobi.dlys.android.core.net.extendcmp.ojm;

/**
 * 用于描述一个类型的类型。 1.mType描述这个目标对象的数据类型；
 * 2.mRowType可选字段，如果这个目标对象是数组或者集合，这个字段用于描述其组件的类型。
 * <p>
 */
public class TypedToken {
	/**
	 * 这个类的Class
	 */
	private Class<?> mType;

	/**
	 * 如果这个类是集合，这个属性描述集合中规定的对象的类型。 如果这个类是数组，这个属性描述其组件的类型。
	 */
	private Class<?> mRowType;

	public TypedToken(Class<?> type) {
		mType = type;
	}

	public TypedToken(Class<?> type, Class<?> rowtype) {
		mType = type;
		mRowType = rowtype;
	}

	public Class<?> getmType() {
		return mType;
	}

	public void setmType(Class<?> mType) {
		this.mType = mType;
	}

	public Class<?> getmRowType() {
		return mRowType;
	}

	public void setmRowType(Class<?> mRowType) {
		this.mRowType = mRowType;
	}
}
