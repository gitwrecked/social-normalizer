package com.gitwrecked.social.mapFunction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gitwrecked.social.cdm.TwitterDto;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

public class ParseTwitterMessageFunction extends ProcessFunction<ObjectNode, TwitterDto> {

	@Override
	public void processElement(ObjectNode jsonNodes, Context ctx, Collector<TwitterDto> out) throws Exception {
		try {
			TwitterDto twitterDto = map(jsonNodes);
			System.out.println("Parsed: " + twitterDto.getMessage());
			out.collect(twitterDto);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private TwitterDto map(ObjectNode jsonNodes) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		TwitterDto twitterDto = mapper.treeToValue(jsonNodes, TwitterDto.class);
		return twitterDto;
	}
}
