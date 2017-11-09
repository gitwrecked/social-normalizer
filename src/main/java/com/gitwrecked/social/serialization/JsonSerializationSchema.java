package com.gitwrecked.social.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.util.serialization.KeyedSerializationSchema;

import java.io.Serializable;

public class JsonSerializationSchema<T> implements KeyedSerializationSchema<T>, Serializable {
	@Override
	public byte[] serializeKey(T element) {
		return "KEY".getBytes();
	}

	@Override
	public byte[] serializeValue(T element) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				return mapper.writeValueAsBytes(element);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				return null;
			}
	}

	@Override
	public String getTargetTopic(T element) {
		return null;
	}
}
