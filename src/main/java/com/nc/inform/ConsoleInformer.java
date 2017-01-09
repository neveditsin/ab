package com.nc.inform;


public class ConsoleInformer extends AbstractInformer{

	public ConsoleInformer(String id) {
		super(id);
	}

	@Override
	public void inform(Info message) throws Exception {
		System.out.println(message.getShortMessage());
		System.out.println(message.getFullDescription());
	}

	
}
