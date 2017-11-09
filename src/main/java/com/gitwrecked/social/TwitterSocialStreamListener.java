package com.gitwrecked.social;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;

import java.io.IOException;
import java.util.Properties;

public class TwitterSocialStreamListener {
	public static void main(String[] args) throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		Properties properties = buildProperties();

		DataStream<String> messageStream = env.addSource(new FlinkKafkaConsumer010<String>(properties.getProperty("topic"), new SimpleStringSchema(), properties));

		messageStream.rebalance().map(new MapFunction<String, Object>() {
			private static final long serialVersionUID = -6867736771747690202L;

			@Override
			public String map(String value) throws Exception {
				String output = "Consumed: " + value;
				System.out.println(output);
				return output;
			}
		}).print();


		env.execute();
	}

	private static Properties buildProperties() throws IOException {
		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers", "localhost:9092");
		properties.setProperty("group.id", "social-aggregator");
		properties.setProperty("topic", "twitter-messages");
		return properties;
	}
}