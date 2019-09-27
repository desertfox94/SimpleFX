package de.simplefx.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Bind {

	public String field() default "";

	public boolean biDirectional() default true;

	public Class<?>[] applicableFor() default {};

}
