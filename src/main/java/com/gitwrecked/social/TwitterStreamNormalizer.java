package com.gitwrecked.social;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gitwrecked.social.cdm.NormalizedDto;
import com.gitwrecked.social.cdm.TwitterDto;
import com.gitwrecked.social.mapFunction.NormalizeTwitterMessageFunction;
import com.gitwrecked.social.mapFunction.ParseTwitterMessageFunction;
import com.gitwrecked.social.serialization.JsonSerializationSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010;
import org.apache.flink.streaming.util.serialization.JSONDeserializationSchema;

import java.io.IOException;
import java.util.Properties;

public class TwitterStreamNormalizer {
	public static void main(String[] args) throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		DataStreamSource<ObjectNode> messageStream = env.addSource(buildTwitterSource());
		messageStream.name("twitter-messages");

		SingleOutputStreamOperator<TwitterDto> parseTwitterStream = messageStream.process(new ParseTwitterMessageFunction());
		parseTwitterStream.name("parse");
		SingleOutputStreamOperator<NormalizedDto> normalizedMessageStream = parseTwitterStream.process(new NormalizeTwitterMessageFunction());
		normalizedMessageStream.name("normalize");

		normalizedMessageStream.addSink(buildNormalizedSink()).name("normalized-messages");

		env.execute();
	}

	private static SourceFunction<ObjectNode> buildTwitterSource() throws IOException {
		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers", "localhost:9092");
		properties.setProperty("group.id", "social-aggregator");
		properties.setProperty("topic", "twitter-messages");

		FlinkKafkaConsumer010<ObjectNode> kafkaConsumer = new FlinkKafkaConsumer010<>(
				"twitter-messages",
				new JSONDeserializationSchema(),
				properties
		);
		return kafkaConsumer;
	}

	private static SinkFunction<NormalizedDto> buildNormalizedSink() {
		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers", "localhost:9092");
		properties.setProperty("topic", "normalized-messages");

		FlinkKafkaProducer010<NormalizedDto> kafkaProducer = new FlinkKafkaProducer010<NormalizedDto>(
				"normalized-messages",
				new JsonSerializationSchema<NormalizedDto>(),
				properties
		);
		return kafkaProducer;
	}
}