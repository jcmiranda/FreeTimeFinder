package calendar_importers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class LocalServer {
	
	private static String _code;
	private HttpServer _server;

    public LocalServer() throws Exception {
        _server = HttpServer.create(new InetSocketAddress(8000), 0);
        _server.createContext("/", new MyHandler());
        _server.setExecutor(null); // creates a default executor
        _server.start();
    }
    
    public String getCode() {
    	return _code;
    }
    
    public void exit() {
    	_server.stop(10);
    }

    static class MyHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
        	URI requestedUri = t.getRequestURI();
        	String query = requestedUri.getRawQuery();
        	String[] codeQuery = query.split("=");
        	if (codeQuery[0].equals("code")) {
        		_code = codeQuery[1];
        	}
        	else if (codeQuery[0].equals("error")) {
        		String response = "<html><head><title>Test</title></head><body><p>Kairos will not import your Google Calendar. </p></body></html>";
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
                t.close();
                _code = "error";
        	}
        	System.out.println("code = "+_code);
            String response = "<html><head><title>Test</title></head><body><p>Thank you, this window can now be closed.</p></body></html>";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
            t.close();
        }
    }
}