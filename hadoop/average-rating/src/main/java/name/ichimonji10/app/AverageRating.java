/**
 * Classes that calculate average movie ratings with Hadoop's MapReduce.
 */
package name.ichimonji10.app;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * Parse arguments and call business logic.
 */
public final class AverageRating {

    /**
     * Do nothing. This is a utility class.
     */
    private AverageRating() { }

    /**
     * Parse arguments and call business logic.
     *
     * @param args A pair of arguments. The former is the path to a CSV file
     * containing movie ratings. The latter is the path to a directory into
     * which results should be placed. Both paths reference the HDFS filesystem.
     */
    public static void main(final String[] args)
    throws ClassNotFoundException, IOException, InterruptedException {
        Path inputPath;
        Path outputPath;
        try {
            inputPath = new Path(args[0]);
            outputPath = new Path(args[1]);
        } catch (ArrayIndexOutOfBoundsException exc) {
            System.err.println("Specify input and output paths.");
            throw exc;
        }

        Configuration conf = new Configuration();

        Job job = Job.getInstance(conf, "average rating");
        job.setJarByClass(AverageRating.class);
        job.setMapperClass(MovieRatingMapper.class);
        job.setCombinerClass(MovieRatingAverager.class);
        job.setReducerClass(MovieRatingAverager.class);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(FloatWritable.class);

        FileInputFormat.addInputPath(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);

        if (job.waitForCompletion(true)) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }
}
