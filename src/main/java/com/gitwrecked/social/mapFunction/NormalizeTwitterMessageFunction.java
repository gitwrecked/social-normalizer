package com.gitwrecked.social.mapFunction;

import com.gitwrecked.social.v0.dto.MessageType;
import com.gitwrecked.social.v0.dto.NormalizedMessageDto;
import com.gitwrecked.social.v0.dto.TwitterMessageDto;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

public class NormalizeTwitterMessageFunction extends ProcessFunction<TwitterMessageDto, NormalizedMessageDto> {

	private OutputTag<TwitterMessageDto> normalizeErrorOut;

	public NormalizeTwitterMessageFunction(OutputTag<TwitterMessageDto> normalizeErrorOut) {
		this.normalizeErrorOut = normalizeErrorOut;
	}

	@Override
	public void processElement(TwitterMessageDto twitterMessageDto, Context ctx, Collector<NormalizedMessageDto> out) throws Exception {
		try {
			NormalizedMessageDto normalizedMessageDto = map(twitterMessageDto);
			System.out.println("Normalized: " + normalizedMessageDto.getMessage());
			out.collect(normalizedMessageDto);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			ctx.output(normalizeErrorOut, twitterMessageDto);
		}
	}

	public NormalizedMessageDto map(TwitterMessageDto twitterMessageDto) throws Exception {
		NormalizedMessageDto normalizedMessageDto = new NormalizedMessageDto();
		normalizedMessageDto.setMessage(twitterMessageDto.getMessage());
		normalizedMessageDto.setSource(MessageType.TWITTER);
		return normalizedMessageDto;
	}
}
