package com.wondertek.mam.util.others;

import java.io.Serializable;

public class ErrorVO implements Serializable {
	private static final long serialVersionUID = -1866745776331297894L;
	private String resourceID;
	private String msgCode;
	private String errorMessage;
	private boolean isError;
	
	public String getResourceID() {
		return resourceID;
	}
	
	public void setResourceID(String resourceID) {
		this.resourceID = resourceID;
	}
	
	public String getMsgCode() {
		return msgCode;
	}
	
	public void setMsgCode(String msgCode) {
		this.msgCode = msgCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean getIsError() {
		return isError;
	}

	public void setIsError(boolean isError) {
		this.isError = isError;
	}
	
	
	
	
}
