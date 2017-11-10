package com.gitwrecked.social;

import com.gitwrecked.social.cdm.TwitterMessageDto;
import com.gitwrecked.social.serialization.JsonSerializationSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Date;
import java.util.Properties;

public class MockTwitterMessageStreamProducer {

	public static void main(String[] args) throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		DataStreamSource<TwitterMessageDto> messageStream = env.addSource(new TwitterMessageGenerator());
		messageStream.name("twitter-message-stream-generator");

		messageStream.addSink(buildTwitterMessageSink()).name("twitter-message-stream");

		env.execute();
	}

	public static class TwitterMessageGenerator implements SourceFunction<TwitterMessageDto> {
		private static final long serialVersionUID = 2174904787118597072L;
		boolean running = true;
		long i = 0;

		@Override
		public void run(SourceContext<TwitterMessageDto> ctx) throws Exception {
			while (running) {
				String output = "message-" + new Date().getTime();
				TwitterMessageDto twitterMessageDto = new TwitterMessageDto();
				twitterMessageDto.setMessage(output);
				ctx.collect(twitterMessageDto);
				System.out.println("MockTwitterMessage: " + output);
				Thread.sleep(5000);
			}
		}

		@Override
		public void cancel() {
			running = false;
		}
	}

	private static SinkFunction<TwitterMessageDto> buildTwitterMessageSink() {
		Properties properties = new Properties();
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

		return new FlinkKafkaProducer010<TwitterMessageDto>(
				"twitter-message-stream",
				new JsonSerializationSchema<TwitterMessageDto>(),
				properties);
	}
}
