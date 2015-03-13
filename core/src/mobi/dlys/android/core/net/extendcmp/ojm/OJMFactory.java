/*
 * 文件名称 : OJMFactory.java
 * <p>
 * 作者信息 : admin
 * <p>
 * 创建时间 : 2013-10-15, 下午3:22:55
 * <p>
 * 版权声明 : Copyright (c) 2012-2013 Xunlei Network Ltd. All rights reserved
 * <p>
 * 评审记录 :
 * <p>
 */

package mobi.dlys.android.core.net.extendcmp.ojm;

import mobi.dlys.android.core.net.extendcmp.ojm.impl.SampleOJMImpl;

/**
 * Object-Json mapping实现类的生成器
 * <p>
 */
public class OJMFactory {
	public static OJM createOJM() {
		OJM ojm = new SampleOJMImpl();
		return ojm;
	}
}
