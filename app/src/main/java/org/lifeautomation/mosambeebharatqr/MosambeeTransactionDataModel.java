package org.lifeautomation.mosambeebharatqr;

import com.google.gson.annotations.SerializedName;

public class MosambeeTransactionDataModel {

	@SerializedName("result")
	private String result;

	@SerializedName("transactionType")
	private int transactionType;

	@SerializedName("transType")
	private String transType;

	@SerializedName("sessionId")
	private String sessionId;

	@SerializedName("message")
	private String message;

	@SerializedName("deviceId")
	private int deviceId;

	@SerializedName("transactionId")
	private int transactionId;

	public void setResult(String result){
		this.result = result;
	}

	public String getResult(){
		return result;
	}

	public void setTransactionType(int transactionType){
		this.transactionType = transactionType;
	}

	public int getTransactionType(){
		return transactionType;
	}

	public void setTransType(String transType){
		this.transType = transType;
	}

	public String getTransType(){
		return transType;
	}

	public void setSessionId(String sessionId){
		this.sessionId = sessionId;
	}

	public String getSessionId(){
		return sessionId;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setDeviceId(int deviceId){
		this.deviceId = deviceId;
	}

	public int getDeviceId(){
		return deviceId;
	}

	public void setTransactionId(int transactionId){
		this.transactionId = transactionId;
	}

	public int getTransactionId(){
		return transactionId;
	}

	@Override
 	public String toString(){
		return 
			"MosambeeTransactionDataModel{" + 
			"result = '" + result + '\'' + 
			",transactionType = '" + transactionType + '\'' + 
			",transType = '" + transType + '\'' + 
			",sessionId = '" + sessionId + '\'' + 
			",message = '" + message + '\'' + 
			",deviceId = '" + deviceId + '\'' + 
			",transactionId = '" + transactionId + '\'' + 
			"}";
		}
}