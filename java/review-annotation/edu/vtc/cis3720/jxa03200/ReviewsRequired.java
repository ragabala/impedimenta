/** Author: Jeremy Audet */
package edu.vtc.cis3720.jxa03200;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** An annotation for indicating that additional reviews should be performed. */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ReviewsRequired {
    /** What additional reviews should be performed?
     *
     * @return The additional reviews to perform.
     */
    public ReviewRequired[] value();
}
