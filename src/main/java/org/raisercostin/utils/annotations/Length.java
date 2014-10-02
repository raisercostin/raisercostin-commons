package org.raisercostin.utils.annotations;

import java.lang.annotation.*;

/**
 * Annotation used for specifying the length of a field
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Length {

    /**
     * Length of the field
     */
    public int value();
}
