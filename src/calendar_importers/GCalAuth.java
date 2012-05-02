package calendar_importers;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import javax.swing.JEditorPane;
import javax.swing.JFrame;

import com.google.api.client.auth.oauth2.TokenResponse;
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
		CLIENT_ID = "1034117539945.apps.googleusercontent.com";
		CLIENT_SECRET = "ygIy2-y40S1Fer0B3oU_coVn"; 
	}
	
	public TokenResponse getRefreshToken() {
		 try {
			 System.out.println("refresh toke = "+_refreshToken);
			return new GoogleRefreshTokenRequest(new NetHttpTransport(), new JacksonFactory(), _refreshToken,CLIENT_ID, CLIENT_SECRET).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public TokenResponse setAuth() {
		//server
		LocalServer server = null;
		try {
			server = new LocalServer();
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		//the info
		String redirect_uri = "urn:ietf:wg:oauth:2.0:oob";
		String redirect_uri_local = "http://localhost:8000";
		String scope = "http://www.google.com/calendar/feeds/";
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
		    String query = "response_type="+response_type+"&"+"client_id="+CLIENT_ID+"&"+"redirect_uri="+redirect_uri_local_en+"&"+"scope="+scope_en;		    
			String url = "https://accounts.google.com/o/oauth2/auth?"+query;
		    URI uri = new URI(url);
		    //display webpage
		    java.awt.Desktop.getDesktop().browse(uri);
		    
		    //JEditorPane urlPane = new JEditorPane(url);
		    //urlPane.setEditable(false);
//		    JFrame frame = new JFrame();
//		    frame.getContentPane().add(urlPane, BorderLayout.CENTER);
//		    frame.setSize(200, 200);
//		    frame.setVisible(true);
		    
		    //get code
		    while (code == null) {
		    	try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	code = server.getCode();
		    }		
		    if (code.equals("error")) {
		    	return null;
		    }
		    //System.out.println("here");
			//STEP TWO: GET ACCESS TOKEN
		    //getting the access token
		    GoogleAuthorizationCodeTokenRequest toke = new GoogleAuthorizationCodeTokenRequest(new NetHttpTransport(), new JacksonFactory(), CLIENT_ID, CLIENT_SECRET, code, redirect_uri_local);
		    GoogleTokenResponse request = toke.execute();
		    request.setExpiresInSeconds((long) 9000);
		    _refreshToken = request.getRefreshToken();
		    System.out.println("refresh toke = "+_refreshToken);
		    //return token for client
		    server.exit();
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
