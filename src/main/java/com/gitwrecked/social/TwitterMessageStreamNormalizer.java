package com.gitwrecked.social;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gitwrecked.social.cdm.NormalizedMessageDto;
import com.gitwrecked.social.cdm.TwitterMessageDto;
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
import org.apache.flink.util.OutputTag;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.io.IOException;
import java.util.Properties;

public class TwitterMessageStreamNormalizer {
	public static void main(String[] args) throws Exception {
		OutputTag<ObjectNode> parseErrorOut = new OutputTag<ObjectNode>("twitter-messages-parse-error"){};
		OutputTag<TwitterMessageDto> normalizeErrorOut = new OutputTag<TwitterMessageDto>("twitter-messages-normalize-error"){};

		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		DataStreamSource<ObjectNode> messageStream = env.addSource(buildTwitterSource());
		messageStream.name("twitter-message-stream");

		SingleOutputStreamOperator<TwitterMessageDto> parseTwitterStream = messageStream.process(new ParseTwitterMessageFunction(parseErrorOut));
		parseTwitterStream.name("parse");
		parseTwitterStream.getSideOutput(parseErrorOut);
//		parseTwitterStream.addSink(buildParseFailureSink()).name("parse-error");

		SingleOutputStreamOperator<NormalizedMessageDto> normalizedMessageStream = parseTwitterStream.process(new NormalizeTwitterMessageFunction(normalizeErrorOut));
		normalizedMessageStream.name("normalize");
		normalizedMessageStream.getSideOutput(normalizeErrorOut);
//		normalizedMessageStream.addSink(buildNormalizeFailureSink(normalizeErrorOut)).name("normalize-error");

		normalizedMessageStream.addSink(buildNormalizedSink()).name("normalized-message-stream");

		env.execute();
	}

	private static SourceFunction<ObjectNode> buildTwitterSource() throws IOException {
		Properties properties = new Properties();
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "social-normalizer");

		FlinkKafkaConsumer010<ObjectNode> kafkaConsumer = new FlinkKafkaConsumer010<>(
				"twitter-message-stream",
				new JSONDeserializationSchema(),
				properties
		);
		return kafkaConsumer;
	}

	private static SinkFunction<ObjectNode> buildParseFailureSink() {
		Properties producerProperties = new Properties();
		producerProperties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

		FlinkKafkaProducer010<ObjectNode> kafkaProducer = new FlinkKafkaProducer010<ObjectNode>(
				"twitter-message-parse-error-out",
				new JsonSerializationSchema<ObjectNode>(),
				producerProperties
		);
		return kafkaProducer;
	}

	private static SinkFunction<TwitterMessageDto> buildNormalizeFailureSink() {
		Properties producerProperties = new Properties();
		producerProperties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

		FlinkKafkaProducer010<TwitterMessageDto> kafkaProducer = new FlinkKafkaProducer010<TwitterMessageDto>(
				"twitter-message-normalize-error-out",
				new JsonSerializationSchema<TwitterMessageDto>(),
				producerProperties
		);
		return kafkaProducer;
	}

	private static SinkFunction<NormalizedMessageDto> buildNormalizedSink() {
		Properties properties = new Properties();
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

		FlinkKafkaProducer010<NormalizedMessageDto> kafkaProducer = new FlinkKafkaProducer010<NormalizedMessageDto>(
				"normalized-message-stream",
				new JsonSerializationSchema<NormalizedMessageDto>(),
				properties
		);
		return kafkaProducer;
	}
}