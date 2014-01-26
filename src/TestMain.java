import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.reduce.IntSumReducer;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.idryman.combinefiles.CFInputFormat;
import org.idryman.combinefiles.FileLineWritable;


public class TestMain extends Configured implements Tool{

  /**
   * @param args
   * @throws Exception 
   */
  public static void main(String[] args) throws Exception {
    System.exit(ToolRunner.run(new Configuration(), new TestMain (), args));
  }

  @Override
  public int run(String[] args) throws Exception {
    // TODO Auto-generated method stub
    Configuration conf = getConf();
    Job job = new Job(conf);
    job.setJobName("CombineFile Demo");
    job.setJarByClass(TestMain.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    job.setInputFormatClass(CFInputFormat.class);
    job.setMapperClass(TestMapper.class);
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(IntWritable.class);
    job.setReducerClass(IntSumReducer.class);
    job.setNumReduceTasks(13);
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    job.submit();
    job.waitForCompletion(true);
    
    return 0;
  }
  
  public static class TestMapper extends Mapper<FileLineWritable, Text, Text, IntWritable>{
    private Text txt = new Text();
    private IntWritable count = new IntWritable(1);
    public void map (FileLineWritable key, Text val, Context context) throws IOException, InterruptedException{
      StringTokenizer st = new StringTokenizer(val.toString());
        while (st.hasMoreTokens()){
          txt.set(st.nextToken());          
          context.write(txt, count);
        }
    }
  }

}
