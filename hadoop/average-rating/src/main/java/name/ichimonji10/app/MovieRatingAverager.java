package name.ichimonji10.app;

import java.io.IOException;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Average movie ratings.
 */
public class MovieRatingAverager
extends Reducer<IntWritable, FloatWritable, IntWritable, FloatWritable> {

    /**
     * Average the ratings for a single movie.
     *
     * @param key A movie ID.
     * @param values Ratings for that movie.
     * @param context The object to which the reduced value is written.
     */
    public final void reduce(
        final IntWritable key,
        final Iterable<FloatWritable> values,
        final Context context)
    throws IOException, InterruptedException {
        float sum = 0;
        int ratings = 0;
        for (FloatWritable rating: values) {
            sum += rating.get();
            ratings += 1;
        }
        FloatWritable avg = new FloatWritable(sum / ratings);
        context.write(key, avg);
    }
}
