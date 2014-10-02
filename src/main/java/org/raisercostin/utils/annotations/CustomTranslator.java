package org.raisercostin.utils.annotations;

import java.lang.annotation.*;

/**
 * Annotation used for specifying the length of a field
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CustomTranslator {

    /**
     * Name of custom translator to be used by the field
     */
    public String translator();
}
