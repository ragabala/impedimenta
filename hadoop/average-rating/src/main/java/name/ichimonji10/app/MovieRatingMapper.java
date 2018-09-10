package name.ichimonji10.app;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Map movie IDs to ratings.
 */
public class MovieRatingMapper
extends Mapper<Object, Text, IntWritable, FloatWritable> {

    /**
     * The regex used to split input lines.
     *
     * Instances of Pattern are immutable and thread-safe.
     */
    private static final Pattern SPLITTER = Pattern.compile(",");

    /**
     * An index into the input line as split by {@link #SPLITTER}.
     */
    private static final Integer MOVIE_ID = 1;

    /**
     * An index into the input line as split by {@link #SPLITTER}.
     */
    private static final Integer RATING = 2;

    /**
     * Map a movie ID to a rating.
     *
     * @param key The input line's index.
     * @param value The input line.
     * @param context The object to which the mapping is written.
     *
     * The first several lines of ratings.csv are:
     *
     *     userId,movieId,rating,timestamp
     *     1,31,2.5,1260759144
     *     1,1029,3.0,1260759179
     */
    public final void map(
        final Object key,
        final Text value,
        final Context context)
    throws IOException, InterruptedException {
        final String[] fields = SPLITTER.split(value.toString());
        if (fields[0].equals("userId")) {
            return;
        }
        context.write(
            new IntWritable(new Integer(fields[MOVIE_ID])),
            new FloatWritable(new Float(fields[RATING]))
        );
    }
}
