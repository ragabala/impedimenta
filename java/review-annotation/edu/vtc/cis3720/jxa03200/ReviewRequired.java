/** Author: Jeremy Audet */
package edu.vtc.cis3720.jxa03200;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** An annotation for indicating that an additional review should be performed.
 *
 * By default, reviews should be done for each issue listed in
 * {@link Issue#defaults}. (The user may modify the default set of issues to
 * check for via CLI flags.) This annotation can be used to indicate that a
 * review should be done for an additional non-default issue.
 */
@Repeatable(ReviewsRequired.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ReviewRequired {
    /** What issue should be checked for?
     *
     * @return The issue that should be checked for.
     */
    public Issue issue();
}
