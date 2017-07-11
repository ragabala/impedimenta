package edu.vtc.cis3720.jxa03200;

import java.util.Arrays;
import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

/** The CLI entry point for this application. */
public class Echo {
    private static final List<String> helpMsg = Arrays.asList(
        "Usage: java edu.vtc.cis3720.jxa03200.Echo [options] arguments",
        "",
        "Print the given arguments to stdout, one per line.",
        "",
        "Options:",
        "  --help",
        "    Shot this message and exit.",
        "  --upper-case",
        "    Fully uppercase each argument."
    );

    /**
     * Parse CLI arguments and execute business logic.
     *
     * @param params CLI arguments.
     */
    public static void main(String[] args) {
        OptionParser parser = new OptionParser();
        OptionSpec<Void> helpOption = parser
            .accepts("help")
            .forHelp();
        OptionSpec<Void> upperCaseOption = parser
            .accepts("upper-case");
        OptionSpec<String> nonOptions = parser
            .nonOptions("Text to be upper-cased.")
            .ofType(String.class);
        OptionSet options = parser.parse(args);
        if (args.length == 0) {
            handleNoArguments();
        } else if (options.has(helpOption)) {
            handleHelpArgument();
        } else {
            handleGoldenPath(
                options.valuesOf(nonOptions),
                options.has(upperCaseOption)
            );
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
     * @param upperCase True if --upper-case was passed, or false otherwise.
     */
    private static void handleGoldenPath(List<String> args, boolean upperCase) {
        if (upperCase) {
            for (String arg: args) {
                System.out.println(arg.toUpperCase());
            }
        } else {
            for (String arg: args) {
                System.out.println(arg);
            }
        }
    }
}
