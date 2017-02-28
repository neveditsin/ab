package com.nc.ws.resources;


import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;

import com.nc.action.LocalExec;
import com.nc.events.Event;

@Path("exec")
public class Exec {

    @GET
    @Path("localexec")
    @Produces(MediaType.TEXT_PLAIN)
    //http://localhost:7719/ws/exec/localexec;command=java%20-version
    public String localExec(@MatrixParam("command") String command,
    						@MatrixParam("timeout") String timeout,
    						@MatrixParam("force_destroy_in") String fdi) {
    	long ltimeout = -1L;
    	long lfdi = -1L;
    	
    	try{
    		ltimeout = Long.parseUnsignedLong(StringUtils.defaultIfEmpty(timeout, "-1L"));
    	} catch (NumberFormatException nfe){
    		//in log
    		//in response
    	}
    	
    	try{
    		lfdi = Long.parseUnsignedLong(StringUtils.defaultIfEmpty(fdi, "-1L"));
    	} catch (NumberFormatException nfe){
    		//in log
    		//in response
    	}
    	
    	
    	LocalExec le = new LocalExec(command, ltimeout, lfdi, false);
    	Event e = le.exec();
        return e.getEventType().toString() + ":" + e.getEventInfo();
    } 
    
    

}

