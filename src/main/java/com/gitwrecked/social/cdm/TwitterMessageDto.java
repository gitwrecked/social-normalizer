package com.gitwrecked.social.cdm;

import java.io.Serializable;

public class TwitterMessageDto implements Serializable{

	private static final long serialVersionUID = -6867736771747690202L;

	String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}

