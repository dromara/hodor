package org.dromara.hodor.server.restservice;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

/**
 * hodor rest service mark interface
 *
 * @author tomgs
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface HodorRestService {

    @AliasFor(annotation = Component.class)
    String value() default "";

    String desc() default "";

}
