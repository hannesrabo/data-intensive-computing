package sparkstreaming

import java.util.HashMap
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka._
import kafka.serializer.{DefaultDecoder, StringDecoder}
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.apache.spark.storage.StorageLevel
import java.util.{Date, Properties}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, ProducerConfig}
import scala.util.Random

import org.apache.spark.sql.cassandra._
import com.datastax.spark.connector._
import com.datastax.driver.core.{Session, Cluster, Host, Metadata}
import com.datastax.spark.connector.streaming._

object KafkaSpark {
  def main(args: Array[String]) {
    // connect to Cassandra and make a keyspace and table as explained in the document
    val cluster = Cluster.builder().addContactPoint("127.0.0.1").build()
    val session = cluster.connect()

    session.execute("CREATE KEYSPACE IF NOT EXISTS avg_space WITH REPLICATION =" +
                    "{'class': 'SimpleStrategy', 'replication_factor': 1};")
    session.execute("CREATE TABLE IF NOT EXISTS avg_space.avg (word text PRIMARY KEY, count float);");

    // make a connection to Kafka and read (key, value) pairs from it
    val kafkaConf = Map(
      "metadata.broker.list" -> "localhost:9092",
      "zookeeper.connect" -> "localhost:2181",
      "group.id" -> "kafka-spark-streaming",
      "zookeeper.connection.timeout.ms" -> "1000"
    )

    // Create the spark context
    val conf = new SparkConf().setMaster("local[4]").setAppName("AverageStreamApp")
    val ssc = new StreamingContext(conf, Seconds(1))

    // Attach context to kafka
    val messages = KafkaUtils.createDirectStream[ String, Integer, StringDecoder, DefaultDecoder](
                              ssc, kafkaConf, Array("avg"))

    // Mapping the output records
    val pairs = messages.map(record => (record.key, record.value)) // Reduce by key here?

    // measure the average value for each key in a stateful manner
    def mappingFunc(key: String, value: Option[Double], state: State[Double]): (String, Double) = {
      // TODO ADD THIS!
    }

    // Attach the mapping function
    val stateDstream = pairs.mapWithState(StateSpec.function(mappingFunc _))

    // store the result in Cassandra
    stateDstream.saveToCassandra("avg_space", "avg")

    ssc.start()
    ssc.awaitTermination()
  }
}
