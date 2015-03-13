/*
 * 文件名称 : RowType.java
 * <p>
 * 作者信息 : admin
 * <p>
 * 创建时间 : 2013-10-21, 下午2:17:47
 * <p>
 * 版权声明 : Copyright (c) 2012-2013 Xunlei Network Ltd. All rights reserved
 * <p>
 * 评审记录 :
 * <p>
 */

package mobi.dlys.android.core.net.extendcmp.ojm.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对集合对象的注解，用于描述集合中组件的类型
 * <p>
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RowType {
	/**
	 * 该类型的类
	 * 
	 * @return
	 */
	Class<?> clz();
}
