package com.gitwrecked.social.mapFunction;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gitwrecked.social.v0.dto.TwitterMessageDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ParseTwitterMessageFunctionTest {

	ParseTwitterMessageFunction parser;

	@Before
	public void setup() {
		parser = new ParseTwitterMessageFunction(null);
	}

	@Test
	public void testParseFunction() throws Exception {
		JsonNodeFactory factory = JsonNodeFactory.instance;
		ObjectNode objectNode = factory.objectNode();
		objectNode.put("message", "hello");

		TwitterMessageDto twitterMessageDto = parser.map(objectNode);

		Assert.assertEquals("hello", twitterMessageDto.getMessage());
	}

}
