/** Author: Jeremy Audet */
package edu.vtc.cis3720.jxa03200;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** An annotation for indicating that a code review has been performed. */
@Repeatable(Reviews.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Review {
    /** Who performed the review?
     *
     * @return The name of the person who performed the review.
     */
    public String name();

    /** When was the review performed?
     *
     * NOTE: The <code>String</code> type is used because annotations may use a
     * tightly restricted set of types. But strings have no restrictions on
     * formatting, which means that the dates passed in by users may be
     * completely bogus.
     *
     * @return The date on which the review was performed.
     */
    public String date();

    /** What issue was checked for?
     *
     * @return The issue that was checked for.
     */
    public Issue issue();

    /** Were changes made during this review?
     *
     * @return Whether changes were made during this review.
     */
    public boolean changed() default false;
}
