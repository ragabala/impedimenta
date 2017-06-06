/** Author: Jeremy Audet */
package edu.vtc.cis3720.jxa03200;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

/** The main entry point for this application's CLI. */
@Review(name="Main review #2", date="2017-06-07", issue=Issue.STYLE)
@Review(name="Main review #1", date="2017-06-06", issue=Issue.STYLE, changed=true)
public class Main {
    // This docstring is manually wrapped at 72 characters. It'd be nicer to use
    // a text wrapping lib that can inspect the terminal and wrap the text at
    // runtime.
    //
    // JOpt Simple offers a help text facility. It's not used at this time
    // because doing so requires an investment in learning that I'm unable (or
    // at least unwilling) to make at this time.
    private static final String helpMsg = new String(
        "Usage: java edu.vtc.cis3720.jxa03200.Main [options] class [class ...]\n" +
        "\n" +
        "Check whether the methods belonging to the specified classes have been\n" +
        "reviewed. If all discovered methods have had sufficient reviews, return.\n" +
        "Otherwise, state which classes or methods are lacking reviews and return\n" +
        "a non-zero exit code. A review is designated by the @Review annotation.\n" +
        "A method has been 'sufficiently reviewed' if @Reviews are present for\n" +
        "each --issue.\n" +
        "\n" +
        "Options:\n" +
        "  --help\n" +
        "    Show this message and exit.\n" +
        "  --issue\n" +
        "    An issue that reviews should cumulatively check for. Repeatable.\n" +
        "    Default: " + Issue.defaults + "\n" +
        "    Available: " + Arrays.toString(Issue.values()) + "\n" +
        "\n" +
        "Arguments:\n" +
        "  class\n" +
        "    A class name in dotted notation. For example: com.example.Main\n"
    );

    /** Parse CLI arguments and execute business logic.
     *
     * @param args CLI arguments.
     */
    @Review(name="check option parser", date="2017-06-10", issue=Issue.REQUIREMENTS)
    public static void main(String[] args) {
        OptionParser parser = new OptionParser();
        OptionSpec<Void> helpOption = parser
            .accepts("help")
            .forHelp();
        OptionSpec<Issue> issueOptions = parser
            .accepts("issue")
            .withRequiredArg()
            .ofType(Issue.class)
            .defaultsTo(Issue.defaults.toArray(new Issue[Issue.defaults.size()]));
        OptionSpec<String> classNameOptions = parser
            .nonOptions("Classes to be inspected.")
            .ofType(String.class);
        OptionSet options = parser.parse(args);
        if (args.length == 0) {
            handleNoArguments();
        } else if (options.has(helpOption)) {
            handleHelpArgument();
        } else {
            Set<Issue> issues = new HashSet<>(options.valuesOf(issueOptions));
            List<String> classNames = options.valuesOf(classNameOptions);
            handleGoldenPath(issues, classNames);
        }
    }

    /** Respond to the case where no arguments were passed.
     *
     * Print a message to stderr and exit with a non-zero code.
     */
    private static void handleNoArguments() {
        System.err.println("ERROR: Too few arguments.");
        System.err.println(helpMsg);
        System.exit(1);
    }

    /** Respond to the case where the "help" argument was passed.
     *
     * Print a message to stdout.
     */
    private static void handleHelpArgument() {
        System.out.println(helpMsg);
    }

    /** Respond to the case where valid non-"help" arguments were passed.
     *
     * Print a message to stdout or stderr as appropriate, and possibly exit
     * with a non-zero code.
     *
     * @param issues Each applicable method should be reviewed for these issues,
     * plus those mentioned by <code>@ReviewRequired</code> annotations.
     * @param classNames A list of fully qualified class names, such as
     * com.example.Main. Each method directly declared on these classes is
     * checked to see if it has had necessary reviews.
     */
    private static void handleGoldenPath(
            Set<Issue> issues, List<String> classNames) {
        // Get all of the methods that need to be reviewed for issues.
        List<Class<?>> klasses = null;
        try {
            klasses = getClasses(classNames);
        } catch (ClassNotFoundException exc) {
            System.err.println("Class not found: " + exc.getMessage());
            System.exit(1);
        }
        List<Method> methods = getMethods(klasses);

        // Create a map declaring, for each method, which issues should be
        // checked for.
        Map<Method, Set<Issue>> targets = new HashMap<>();
        for (Method method : methods) {
            Set<Issue> methodIssues = new HashSet<>(issues); // create copy
            for (ReviewRequired reviewRequired : method
                    .getAnnotationsByType(ReviewRequired.class)) {
                methodIssues.add(reviewRequired.issue());
            }
            for (ReviewRequired reviewRequired : method
                    .getDeclaringClass()
                    .getAnnotationsByType(ReviewRequired.class)) {
                methodIssues.add(reviewRequired.issue());
            }
            targets.put(method, methodIssues);
        }

        // For each method in targets.keySet(), figure out which reviews
        // have been done, and remove them from the corresponding set of
        // values.
        for (Method method : targets.keySet()) {
            List<Issue> issuesCheckedFor = new ArrayList<>();
            for (Review review : method.getAnnotationsByType(Review.class)) {
                issuesCheckedFor.add(review.issue());
            }
            for (Review review : method
                    .getDeclaringClass()
                    .getAnnotationsByType(Review.class)) {
                issuesCheckedFor.add(review.issue());
            }
            targets.get(method).removeAll(issuesCheckedFor);
        }

        // Print a report.
        for (Method method : targets.keySet()) {
            Set<Issue> methodIssues = targets.get(method);
            if (methodIssues.size() > 0) {
                System.out.println(method + ": " + methodIssues);
            }
        }
    }

    /** Get the classes corresponding to the given class names.
     *
     * @param classNames Fully qualified class names, such as com.example.Main.
     */
    private static List<Class<?>> getClasses(List<String> classNames)
            throws ClassNotFoundException {
        List<Class<?>> klasses = new ArrayList<>();
        for (String className : classNames) {
            Class<?> klass = Class.forName(className);
            klasses.add(klass);
        }
        return klasses;
    }

    /** Get the methods declared directly on the given classes.
     *
     * Do not return methods declared on parent classes.
     *
     * @param klasses The classes to search.
     */
    @Review(name="getMethods review #1", date="2017-06-06", issue=Issue.DESIGN)
    @ReviewRequired(issue=Issue.REQUIREMENTS)
    private static List<Method> getMethods(List<Class<?>> klasses) {
        List<Method> allMethods = new ArrayList<>();
        for (Class<?> klass : klasses) {
            allMethods.addAll(Arrays.asList(klass.getDeclaredMethods()));
        }
        return allMethods;
    }
}
