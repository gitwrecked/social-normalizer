package com.gitwrecked.social.cdm;

import java.io.Serializable;

public class NormalizedDto implements Serializable {

	private static final long serialVersionUID = -6867736771747690203L;

	String message;

	Boolean isNormalized = true;

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
}
