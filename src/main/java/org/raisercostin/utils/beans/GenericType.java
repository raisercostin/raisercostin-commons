package org.raisercostin.utils.beans;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD, ElementType.LOCAL_VARIABLE, ElementType.PARAMETER })
public @interface GenericType {
	Class<?> value();
}
