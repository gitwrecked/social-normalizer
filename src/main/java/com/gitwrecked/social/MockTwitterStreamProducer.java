package com.gitwrecked.social;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;

import java.io.IOException;
import java.util.Properties;

public class MockTwitterStreamProducer {

	public static void main(String[] args) throws Exception {
		// create execution environment
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		// parse parameters
		Properties properties = buildProperties();

		// add a simple source which is writing some strings
		DataStream<String> messageStream = env.addSource(new SimpleStringGenerator());

		// write stream to Kafka
		messageStream.addSink(new FlinkKafkaProducer010<String>(properties.getProperty("bootstrap.servers"),
				properties.getProperty("topic"),
				new SimpleStringSchema()));

		env.execute();
	}

	public static class SimpleStringGenerator implements SourceFunction<String> {
		private static final long serialVersionUID = 2174904787118597072L;
		boolean running = true;
		long i = 0;

		@Override
		public void run(SourceContext<String> ctx) throws Exception {
			while (running) {
				String output = "{message:'" + (i++) + "'}";
				ctx.collect(output);
				System.out.println("MockTwitterMessage: " + output);
				Thread.sleep(5000);
			}
		}

		@Override
		public void cancel() {
			running = false;
		}
	}

	private static Properties buildProperties() throws IOException {
		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers", "localhost:9092");
		properties.setProperty("topic", "twitter-messages");
		return properties;
	}
}
