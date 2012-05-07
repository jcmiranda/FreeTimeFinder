package calendar_importers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;

/*
 * Authenticates user using OAuth.  Keeps track of refresh token. 
 * This program NEVER knows user's Google username and password
 */

public class GCalAuth {
	private String CLIENT_ID; //from registering Kairos with Google APIs
	private String CLIENT_SECRET; //from registering Kairos with Google APIs
	private String _refreshToken; //recieved after authentication request granted
	
	public GCalAuth() {
		CLIENT_ID = "1034117539945.apps.googleusercontent.com";
		CLIENT_SECRET = "ygIy2-y40S1Fer0B3oU_coVn"; 
	}
	
	/*
	 * Purpose: procure refresh token so user doesn't need to re-sign-in
	 * Input: null
	 * Output: refresh token
	 */
	public TokenResponse getRefreshToken() {
		 try {
			return new GoogleRefreshTokenRequest(new NetHttpTransport(), new JacksonFactory(), _refreshToken,CLIENT_ID, CLIENT_SECRET).execute();
		} catch (IOException e) {
		}
		return null;
	}
	
	/*
	 * Set up authentication for first time
	 * Steps:
	 * 		1) Form URL GET request for Google
	 * 		2) Open browser displaying request for permission to access calendars page
	 * 		3) Start LocalServer to listen for response
	 * 		4) If user clicks "Allow Access"
	 * 				5a) server will pick up access code
	 * 					program will exchange the access code for refresh token and access token
	 * 					return access token, will be used for authentication
	 * 					refresh token will be stored for later
	 *		6) If user clicks "No thanks"
	 *				5b) return null calendar
	 *		7) Kill server
	 */
	public TokenResponse setAuth() throws MalformedURLException {
		//server
		LocalServer server = null;
		try {
			server = new LocalServer();
		} catch (Exception e2) {
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
		}
		
		//STEP ONE: getting the code
		try {
		    String query = "response_type="+response_type+"&"+"client_id="+CLIENT_ID+"&"+"redirect_uri="+redirect_uri_local_en+"&"+"scope="+scope_en;		    
			String url = "https://accounts.google.com/o/oauth2/auth?"+query;
		    URI uri = new URI(url);
		    //display webpage
		    java.awt.Desktop.getDesktop().browse(uri);
		    
		    //get code
		    while (code == null) {
		    	try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
		    	code = server.getCode();
		    }		
		    if (code.equals("error")) {
		    	return null;
		    }

		    //STEP TWO: GET ACCESS TOKEN
		    //getting the access token
		    GoogleAuthorizationCodeTokenRequest toke = new GoogleAuthorizationCodeTokenRequest(new NetHttpTransport(), new JacksonFactory(), CLIENT_ID, CLIENT_SECRET, code, redirect_uri_local);
		    GoogleTokenResponse request = toke.execute();
		    request.setExpiresInSeconds((long) 9000);
		    _refreshToken = request.getRefreshToken();
		    //return token for client
		    server.exit();
		    return request;
		} 
		catch (IOException e) {
		}
		catch (URISyntaxException e) {
		}
		return null;
	}
}