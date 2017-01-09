package com.nc.inform;

public enum InformerType {
	EMAIL("emails"), CONSOLE("console");
	
	private String confString;
	InformerType(String confString){
		this.confString = confString;
	}
	
	public String toCongfigString(){
		return confString;
	}
}
