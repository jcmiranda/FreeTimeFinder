package calendar_importers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import javax.swing.JOptionPane;

//import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;

public class GCalAuth {
	private String CLIENT_ID;
	private String CLIENT_SECRET;
	private String _refreshToken;
	
	
	public GCalAuth() {
		
	}
	
	public TokenResponse getRefreshToken() {
		 try {
			return new GoogleRefreshTokenRequest(new NetHttpTransport(), new JacksonFactory(), _refreshToken,CLIENT_ID, CLIENT_SECRET).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public TokenResponse setAuth() {
		//the info
		CLIENT_ID = "1034117539945.apps.googleusercontent.com";
		String redirect_uri = "urn:ietf:wg:oauth:2.0:oob";
		String redirect_uri_local = "http://localhost:8080";
		String scope = "http://www.google.com/calendar/feeds/";
		CLIENT_SECRET = "ygIy2-y40S1Fer0B3oU_coVn"; 
		String code = null;
		String response_type = "code";
		//encoded
		String redirect_uri_local_en = null;
		String scope_en = null;
		String redirect_uri_en = null;
		try {
			redirect_uri_en = URLEncoder.encode(redirect_uri, "UTF-8");
			redirect_uri_local_en = URLEncoder.encode(redirect_uri_local, "UTF-8");		
			scope_en = URLEncoder.encode(scope, "UTF-8");
		}
		catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//STEP ONE: getting the code
		try {
		    String query = "response_type="+response_type+"&"+"client_id="+CLIENT_ID+"&"+"redirect_uri="+redirect_uri_en+"&"+"scope="+scope_en;		    
			String url = "https://accounts.google.com/o/oauth2/auth?"+query;
		    URI uri = new URI(url);
		    
		    //display webpage
		    java.awt.Desktop.getDesktop().browse(uri);
		    //get code
		    code = (String) new JOptionPane().showInputDialog("Paste code:");		    
		    
//		    //SOCKET
//		    //Socket listener = new Socket(InetAddress.getLocalHost(), 80);
//		    //InetSocketAddress sa = new InetSocketAddress(InetAddress.getLocalHost(), 80);
//		    //listener.bind(sa);
//		    //local
		    //Socket listener = this.serverReady(InetAddress.getLocalHost(), 80); 
//		    //uri based
////		    System.out.println(r_uri.getHost());
////	    	Socket listener = this.serverReady(InetAddress., 80); 
	    	//System.out.println("connected");
		    
//		    //HTTPURLConnection
//		    HttpURLConnection listener = (HttpURLConnection) uri.toURL().openConnection();
//		    listener.setDoInput(true); 
//			listener.setDoOutput(true);
//			listener.setFollowRedirects(true);
//		    while (true) {
////		    	listener = (HttpURLConnection) uri.toURL().openConnection();
////		    	listener.setDoInput(true); 
////				listener.setDoOutput(true);
////				listener.setFollowRedirects(true);
//		    	int response = listener.getResponseCode();
//				System.out.println("resp = "+response);
//				System.out.println("url = "+listener.getURL());
//				if (response == 302) {
//					break;
//				}
//		    }
			
			//int response = listener.getResponseCode();
			//System.out.println("resp = "+response);
		    
	    	//READ INPUT
//		    BufferedReader rd = new BufferedReader(new InputStreamReader(listener.getInputStream()));		    
//		    String line;
//			while ((line = rd.readLine()) != null) {
//				System.out.println(line);
//			}
//			rd.close();
		    
			//STEP TWO: GET ACCESS TOKEN

		    //getting the access token
		    GoogleAuthorizationCodeTokenRequest toke = new GoogleAuthorizationCodeTokenRequest(new NetHttpTransport(), new JacksonFactory(), CLIENT_ID, CLIENT_SECRET, code, redirect_uri);
		    GoogleTokenResponse request = toke.execute();
		    request.setExpiresInSeconds((long) 9000);
		    _refreshToken = request.getRefreshToken();
		    
		    //return token for client
		    return request;
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
