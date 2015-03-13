package mobi.dlys.android.core.net.extendcmp.ojm.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 如果将某个属性标记为Ignore，则跳过该属性的Json化。
 * 
 * @author admin
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IgnoreOnToJson {

}
