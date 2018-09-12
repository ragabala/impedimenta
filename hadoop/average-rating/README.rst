Average Rating
==============

Calculate the average rating of each movie in MovieLens' latest-small dataset,
with Hadoop.

Hadoop deployments can significantly vary. I've tested this application on a
pseudo-distributed deployment as follows:

1. Start sshd, and ensure ``ssh localhost`` works without a password.
2. Format the HDFS filesystem.
3. Start the Hadoop daemons.
4. Populate the HDFS filesystem.
5. Optionally check code quality with ``mvn verify``.
6. Create a jar with ``mvn package``.
7. Run the jar'd code and view the results.

To clean up, stop the daemons and ``rm -rf "/tmp/hadoop-$(whoami)"``. The
scripts in the ``scripts`` directory help with many of these tasks.
