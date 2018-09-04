package com.wondertek.mam.util.others;

public class AgentEngineException extends Exception
{
	private static final long serialVersionUID = 1L;
	private String errorCode = "100001";

	public String getErrorCode()
	{
		return errorCode;
	}

	public void setErrorCode(String errorCode)
	{
		this.errorCode = errorCode;
	}

	/**
	 * 构造器描述：构造带指定详细消息的新异常
	 * 
	 * @param cause
	 *            根据指定的原因和的详细消息构造新异常
	 */
	public AgentEngineException(Throwable cause)
	{
		super(cause);
	}

	public AgentEngineException(String message)
	{
		super(message);
	}

	public AgentEngineException(String message, String errorCode)
	{
		super(message);
		this.errorCode = errorCode;
	}

	public AgentEngineException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public AgentEngineException(String message, Throwable cause, String errorCode)
	{
		super(message, cause);
		this.errorCode = errorCode;
	}

	public AgentEngineException(Throwable cause, String errorCode)
	{
		super(cause);
		this.errorCode = errorCode;
	}
}
