package com.nc.ws.auth;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.nc.config.GlobalConfig;

@Provider
public class AuthorizationRequestFilter implements ContainerRequestFilter {
 
    @Override
    public void filter(ContainerRequestContext requestContext)
                    throws IOException {
    	

    	String auth = requestContext.getHeaderString("authorization");
    	
        
		if (auth == null || !auth.replace("Basic ", "").equals(
				GlobalConfig.getConfig().getAuthString())) {
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
			.entity("Access denied").build());
		}
 

        
    }
}
