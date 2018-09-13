package topten;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Collections;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.filter.FirstKeyOnlyFilter;

public class TopTen {
    // This helper function parses the stackoverflow into a Map for us.
    public static Map<String, String> transformXmlToMap(String xml) {
	Map<String, String> map = new HashMap<String, String>();
	try {
	    String[] tokens = xml.trim().substring(5, xml.trim().length() - 3).split("\"");
	    for (int i = 0; i < tokens.length - 1; i += 2) {
		String key = tokens[i].trim();
		String val = tokens[i + 1];
		map.put(key.substring(0, key.length() - 1), val);
	    }
	} catch (StringIndexOutOfBoundsException e) {
	    System.err.println(xml);
	}

	return map;
    }

    public static class TopTenMapper extends Mapper<Object, Text, NullWritable, Text> {
	// Stores a map of user reputation to the record
	TreeMap<Integer, Text> repToRecordMap = new TreeMap<Integer, Text>(Collections.reverseOrder());

	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		try {
			String tempVal = value.toString().trim();
			// If it is not a user row, we will discard it.
			if (tempVal.startsWith("<row")) {
				Map<String, String> xmlMap 	= transformXmlToMap(tempVal);

				int id 		= Integer.parseInt(xmlMap.get("Id")); 
				int rating 	= Integer.parseInt(xmlMap.get("Reputation"));

				// Discarding the invalid row.
				if (id != -1) {
					Text entryText = new Text(value);
					repToRecordMap.put(rating, entryText);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void cleanup(Context context) throws IOException, InterruptedException {
	    // Output our ten records to the reducers with a null key
		try {
			int i = 0;
			for(Map.Entry<Integer,Text> entry : repToRecordMap.entrySet()) {
				if (i < 10) {
					Text entryText = new Text(entry.getValue());
					context.write(NullWritable.get(), entryText);
					i++;
				} else {
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	}

    public static class TopTenReducer extends TableReducer<NullWritable, Text, NullWritable> {
	// Stores a map of user reputation to the record
	private TreeMap<Integer, Integer> repToRecordMap = new TreeMap<Integer, Integer>();
	private Text putText = new Text();
	public void reduce(NullWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		try {
			// Pushing the record into the map
			for (Text entry : values) {
				String tempVal = entry.toString().trim();
				Map<String, String> xmlMap = transformXmlToMap(tempVal);
				int id 		= Integer.parseInt(xmlMap.get("Id"));
				int rating 	= Integer.parseInt(xmlMap.get("Reputation"));
				repToRecordMap.put(id, rating);
			}

			// Pushing the results into the db
			for(Map.Entry<Integer,Integer> entry : repToRecordMap.entrySet()) {
				// Parsing the xml
				System.out.println("----------> " + entry);

				// create hbase put with rowkey as the id
				Put insHBase = new Put(Bytes.toBytes(entry.getKey()));

				// insert rating to hbase
				insHBase.add(Bytes.toBytes("info"), Bytes.toBytes("id"), Bytes.toBytes(entry.getKey()));
				insHBase.add(Bytes.toBytes("info"), Bytes.toBytes("rep"), Bytes.toBytes(entry.getValue()));

				// write data to Hbase table
				context.write(null, insHBase);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    }

    public static void main(String[] args) throws Exception { 
		Configuration conf = HBaseConfiguration.create();

		Job job = Job.getInstance(conf);
		job.setJarByClass(TopTen.class);
		job.setNumReduceTasks(1);
		
		job.setMapperClass(TopTenMapper.class);
		// job.setCombinerClass(TopTenReducer.class);
		job.setReducerClass(TopTenReducer.class);

		job.setMapOutputKeyClass(NullWritable.class);
		job.setMapOutputValueClass(Text.class);
		// job.setOutputKeyClass(NullWritable.class);
		// job.setOutputValueClass(Put.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));

		// define output table
		TableMapReduceUtil.initTableReducerJob("topten", TopTenReducer.class, job);
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);

    }
}
