package com.gitwrecked.social.mapFunction;

import com.gitwrecked.social.v0.dto.MessageType;
import com.gitwrecked.social.v0.dto.NormalizedMessageDto;
import com.gitwrecked.social.v0.dto.TwitterMessageDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NormalizeTwitterMessageFunctionTest {

	NormalizeTwitterMessageFunction normalizer;

	@Before
	public void setup() {
		normalizer = new NormalizeTwitterMessageFunction(null);
	}

	@Test
	public void testNormalizeFunction() throws Exception {
		TwitterMessageDto twitterMessageDto = new TwitterMessageDto();
		twitterMessageDto.setMessage("hello");

		NormalizedMessageDto normalizedMessageDto = normalizer.map(twitterMessageDto);

		Assert.assertEquals("hello", normalizedMessageDto.getMessage());
		Assert.assertEquals(true, normalizedMessageDto.getIsNormalized());
		Assert.assertEquals(MessageType.TWITTER, normalizedMessageDto.getSource());
	}
}
