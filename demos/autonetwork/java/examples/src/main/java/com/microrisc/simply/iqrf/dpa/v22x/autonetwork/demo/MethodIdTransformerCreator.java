

package com.microrisc.simply.iqrf.dpa.v22x.autonetwork.demo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates, that the annotated method serves as a creator of {@link MethodIdTransformer} 
 * object.
 * 
 * @author Michal Konopa
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MethodIdTransformerCreator {
}
