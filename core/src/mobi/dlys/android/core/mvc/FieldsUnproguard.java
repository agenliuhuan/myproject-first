package mobi.dlys.android.core.mvc;

import java.io.Serializable;

/**
 * 实现该接口的子类和子接口都不会被代码混淆器混淆。 用于方便管理混淆，免得写入很多配置到proguard.cfg中，也防止配置漏缺。。
 */
public interface FieldsUnproguard extends Serializable {

}
