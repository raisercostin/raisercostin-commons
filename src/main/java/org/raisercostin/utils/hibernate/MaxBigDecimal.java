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
@ValidatorClass(MaxBigDecimalValidator.class)
@Target( { METHOD, FIELD })
@Retention(RUNTIME)
public @interface MaxBigDecimal {
	int precision();

	int scale();

	String message() default "{validator.max}";
}
