package edu.vtc.cis3720.jxa03200;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

/** The CLI entry point for this application. */
public class Echo {
    private static final List<String> helpMsg = Arrays.asList(
        "Usage: java edu.vtc.cis3720.jxa03200.Echo [options] integers",
        "",
        "Print the given integers to stdout, one per line.",
        "",
        "Options:",
        "  --help",
        "    Shot this message and exit.",
        "  --multiply <integer>",
        "    Multiply each non-option integer by the given integer."
    );

    /**
     * Parse CLI arguments and execute business logic.
     *
     * @param params CLI arguments.
     */
    public static void main(String[] args) {
        OptionParser parser = new OptionParser("h*m:");
        OptionSet options = parser.parse(args);

        List<Integer> integers = new ArrayList<>();
        for (Object obj: options.nonOptionArguments()) {
            integers.add(Integer.valueOf((String)obj));
        }
        Integer multiply;
        Object rawMultiply = options.valueOf("m");
        if (rawMultiply == null) {
            multiply = new Integer(1);
        } else {
            multiply = Integer.valueOf((String)rawMultiply);
        }

        if (args.length == 0) {
            handleNoArguments();
        } else if (options.has("h")) {
            handleHelpArgument();
        } else {
            handleGoldenPath(integers, multiply);
        }
    }

    /**
     * Respond to the case where no arguments were passed.
     *
     * Print a help message to stderr and exit with a non-zero code.
     */
    private static void handleNoArguments() {
        System.err.println("ERROR: Too few arguments.");
        for (String line: helpMsg) {
            System.err.println(line);
        }
        System.exit(1);
    }

    /**
     * Respond to the case where the "help" argument was passed.
     *
     * Print a help message to stdout.
     */
    private static void handleHelpArgument() {
        for (String line: helpMsg) {
            System.out.println(line);
        }
    }

    /**
     * Respond to the case where valid non-"help" arguments were passed.
     *
     * @param args The CLI arguments passed to this program.
     * @param multiply The value to multiply each non-option argument by.
     */
    private static void handleGoldenPath(List<Integer> args, Integer multiply) {
        for (Integer arg: args) {
            System.out.println(multiply * arg);
        }
    }
}
