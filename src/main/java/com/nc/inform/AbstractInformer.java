package com.nc.inform;

abstract class AbstractInformer implements Informer {

	private final String id;
	
	public AbstractInformer(String id) {
		super();
		this.id = id;
	}

	@Override
	public final String getId() {
		return id;
	}

}
