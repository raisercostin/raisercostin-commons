package org.raisercostin.utils.hibernate;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.hibernate.validator.ValidatorClass;

/**
 * max restriction on a numeric annotated element
 */
@Documented
@ValidatorClass(AmountValidator.class)
@Target( { METHOD, FIELD })
@Retention(RUNTIME)
public @interface Amount {
	String field();

	int precision();

	int scale();

	String message() default "{validator.max}";
}
