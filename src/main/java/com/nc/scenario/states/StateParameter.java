package com.nc.scenario.states;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME) 
@interface StateParameter {
	String xmlName();
	boolean isOptional();
}
