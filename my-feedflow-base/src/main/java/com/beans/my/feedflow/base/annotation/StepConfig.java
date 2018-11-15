package com.beans.my.feedflow.base.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.beans.my.feedflow.base.enums.FormType;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StepConfig {
	/** 页面表单Label */
	String label();
	/** 参数名 */
	String name();
	/** 类型 */
	FormType type() default FormType.TEXT;
	/** 是否必须 */
	boolean required() default false;
	/** 默认值 */
	String defaultValue() default "";
}
