package org.raisercostin.utils.beans;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD, ElementType.LOCAL_VARIABLE, ElementType.PARAMETER })
public @interface GenericType {
	Class<?> value();
}
