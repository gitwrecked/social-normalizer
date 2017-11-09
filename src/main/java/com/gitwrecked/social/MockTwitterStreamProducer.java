package com.gitwrecked.social;

import com.gitwrecked.social.cdm.TwitterDto;
import com.gitwrecked.social.serialization.JsonSerializationSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010;

import java.util.Properties;

public class MockTwitterStreamProducer {

	public static void main(String[] args) throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		DataStreamSource<TwitterDto> messageStream = env.addSource(new TwitterMessageGenerator());
		messageStream.name("mock-twitter-messages");

		messageStream.addSink(buildTwitterMessageSink()).name("twitter-messages");

		env.execute();
	}

	public static class TwitterMessageGenerator implements SourceFunction<TwitterDto> {
		private static final long serialVersionUID = 2174904787118597072L;
		boolean running = true;
		long i = 0;

		@Override
		public void run(SourceContext<TwitterDto> ctx) throws Exception {
			while (running) {
				String output = "message-" + (i++);
				TwitterDto twitterDto = new TwitterDto();
				twitterDto.setMessage(output);
				ctx.collect(twitterDto);
				System.out.println("MockTwitterMessage: " + output);
				Thread.sleep(5000);
			}
		}

		@Override
		public void cancel() {
			running = false;
		}
	}

	private static SinkFunction<TwitterDto> buildTwitterMessageSink() {
		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers", "localhost:9092");
		properties.setProperty("topic", "twitter-messages");

		return new FlinkKafkaProducer010<TwitterDto>(
				properties.getProperty("topic"),
				new JsonSerializationSchema<TwitterDto>(),
				properties);
	}
}
