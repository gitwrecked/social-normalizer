package com.gitwrecked.social.cdm;

import java.io.Serializable;

public class NormalizedMessageDto implements Serializable {

	private static final long serialVersionUID = -6867736771747690203L;

	String message;

	Boolean isNormalized = true;

	MessageType source;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Boolean getIsNormalized() {
		return isNormalized;
	}

	public void setIsNormalized(Boolean isNormalized) {
		this.isNormalized = isNormalized;
	}

	public MessageType getSource() {
		return source;
	}

	public void setSource(MessageType source) {
		this.source = source;
	}
}
