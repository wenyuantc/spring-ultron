package org.springultron.swagger.knife4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启Knife4j增强注解
 *
 * @author brucewuu
 * @date 2020/1/6 11:40
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ConditionalOnWebApplication
@Import({Knife4jAutoConfiguration.class})
public @interface EnableKnife4j {

}
