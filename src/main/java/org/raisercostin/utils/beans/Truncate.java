package org.raisercostin.utils.beans;

import java.lang.annotation.*;

/**
 * This annotation should be used in combination with length annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Truncate {
    // marker annotation
}
