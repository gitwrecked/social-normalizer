package com.gitwrecked.social.mapFunction;

import com.gitwrecked.social.cdm.NormalizedDto;
import com.gitwrecked.social.cdm.SourceType;
import com.gitwrecked.social.cdm.TwitterDto;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

public class NormalizeTwitterMessageFunction extends ProcessFunction<TwitterDto, NormalizedDto> {

	@Override
	public void processElement(TwitterDto twitterDto, Context ctx, Collector<NormalizedDto> out) throws Exception {
		try {
			NormalizedDto normalizedDto = map(twitterDto);
			System.out.println("Normalized: " + normalizedDto.getMessage());
			out.collect(normalizedDto);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public NormalizedDto map(TwitterDto twitterDto) throws Exception {
		NormalizedDto normalizedDto = new NormalizedDto();
		normalizedDto.setMessage(twitterDto.getMessage());
		normalizedDto.setSource(SourceType.TWITTER);
		return normalizedDto;
	}
}
