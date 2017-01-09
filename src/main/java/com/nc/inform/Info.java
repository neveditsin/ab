package com.nc.inform;

public class Info {
	private String shortMessage;
	private String fullDescription;
	

	public Info(String shortMessage, String fullDescription){
		this.shortMessage = shortMessage;
		this.fullDescription = fullDescription;
	}
	
	public Info(){
		this("", "");
	}
	
	public String getShortMessage() {
		return shortMessage;
	}
	public void setShortMessage(String shortMessage) {
		this.shortMessage = shortMessage;
	}
	public String getFullDescription() {
		return fullDescription;
	}
	public void setFullDescription(String fullDescription) {
		this.fullDescription = fullDescription;
	}

}
