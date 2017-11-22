package com.nc.ws;


import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;




import java.net.URI;


public class WsServer {
	private static String BASE_URI; 
    private static HttpServer server;
    
    private WsServer(){    	
    }
    
    //TODO do not allow 2 servers to be created
    public static HttpServer startServer(int port, boolean isPublic) {
    	BASE_URI = String.format("http://%s:%d/ws/", isPublic ? "0.0.0.0" : "localhost", port);
    	
        final ResourceConfig rc = new ResourceConfig().packages("com.nc.ws.resources", "com.nc.ws.auth");        
        return server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
        
        
    }
    
	public static void stopServer() {
    	server.shutdownNow();
    }
	

//    public static void main(String[] args)  {
//        final HttpServer server = startServer();
//        System.out.println(String.format("Jersey app started with WADL available at "
//                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
//        System.in.read();
//        server.stop();
//    }
}

