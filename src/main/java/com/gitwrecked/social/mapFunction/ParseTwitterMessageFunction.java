package com.gitwrecked.social.mapFunction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gitwrecked.social.cdm.TwitterMessageDto;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

public class ParseTwitterMessageFunction extends ProcessFunction<ObjectNode, TwitterMessageDto> {

	private OutputTag<ObjectNode> parseErrorOut;

	public ParseTwitterMessageFunction (OutputTag<ObjectNode> parseErrorOut) {
		this.parseErrorOut = parseErrorOut;
	}

	@Override
	public void processElement(ObjectNode jsonNodes, Context ctx, Collector<TwitterMessageDto> out) throws Exception {
		try {
			TwitterMessageDto twitterMessageDto = map(jsonNodes);
			System.out.println("Parsed: " + twitterMessageDto.getMessage());
			out.collect(twitterMessageDto);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			ctx.output(parseErrorOut, jsonNodes);
		}
	}

	private TwitterMessageDto map(ObjectNode jsonNodes) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		TwitterMessageDto twitterMessageDto = mapper.treeToValue(jsonNodes, TwitterMessageDto.class);
		return twitterMessageDto;
	}
}
