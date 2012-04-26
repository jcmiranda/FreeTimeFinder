package calendar_importers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import calendar.CalendarGroup;
import calendar.CalendarResponses;
import calendar.GoogleCalendars;
import calendar.Owner;
import calendar.OwnerImpl;
import calendar.Response;


import com.google.gdata.client.calendar.CalendarQuery;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.*;
import com.google.gdata.data.calendar.*;
import com.google.gdata.data.extensions.When;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import com.google.gdata.client.*;
import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.draft10.*;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessTokenRequest.GoogleAuthorizationCodeGrant;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;

//auth stuff:
	//http://code.google.com/apis/gdata/docs/auth/clientlogin.html
	//http://www.java-samples.com/java/POST-toHTTPS-url-free-java-sample-program.htm
	//http://www.java-samples.com/java/POST-toHTTPS-url-free-java-sample-program.htm
	//http://code.google.com/apis/gdata/docs/auth/overview.html#ClientLogin

	//https://developers.google.com/accounts/docs/OAuth2
	//https://developers.google.com/accounts/docs/OAuth2InstalledApp
	//http://code.google.com/p/google-api-java-client/wiki/OAuth2
	//https://code.google.com/apis/console/#project:1034117539945:stats:calendar
	//https://developers.google.com/google-apps/calendar/
//edit recurring events

//edit all day

/*
 * Function: GoogleCalendars importMyGCal(org.joda.time.DateTime startTime, org.joda.time.DateTime endTime)
 * Function: GoogleCalendars refresh(org.joda.time.DateTime startTime, org.joda.time.DateTime endTime)
 */

public class GCalImporter implements CalendarsImporter {
	private CalendarService _client;
	int MAX_RESPONSES = 100;
	private Owner _owner;
	
	public GCalImporter() {
		//connect to client
		_client = new CalendarService("yourCompany-yourAppName-v1");
	}
	
	public GoogleCalendars importMyGCal(org.joda.time.DateTime startTime, org.joda.time.DateTime endTime) throws IOException, ServiceException, com.google.gdata.util.ServiceException {
		//authenticate user
		this.setAuth();
		//import calendars -- make calendar group
		return this.importCalendarGroup(startTime, endTime);
	}
	
	public void setAuth() throws AuthenticationException, MalformedURLException, com.google.gdata.util.AuthenticationException {
		//Get username and password from GUI
		String username = (String) new JOptionPane().showInputDialog("Please type Google Calendar username:");
		_owner = new OwnerImpl(username);
		JOptionPane pwordPane = new JOptionPane();
		JPasswordField jpf = new JPasswordField();
		pwordPane.showConfirmDialog(null, jpf, "Please type Google Calendar password:", pwordPane.OK_CANCEL_OPTION);
		char[] passwordArray = jpf.getPassword();
		StringBuffer passwordString = new StringBuffer();
		for (int i = 0; i < passwordArray.length; i++) {
			passwordString.append(passwordArray[i]);
		}
		String password = passwordString.toString();
		
		//TODO: set up cookie/token stuff			
		//URL feedURL = new URL("https://www.google.com/accounts/ClientLogin");
		//HttpsURLConnection connection = (HttpsURLConnection) URL.openConnection();
		//connection.setDoInput(true); 
		//connection.setDoOutput(true);
		//connection.setRequestMethod("POST"); 
		//connection.setFollowRedirects(true); 
		
		_client.setUserCredentials(username, password);
	}
	
	public void post() {
		
		//the info
		String client_id = "1034117539945.apps.googleusercontent.com";
		String redirect_uri = "urn:ietf:wg:oauth:2.0:oob";
		String redirect_uri_local = "http://localhost:1337";
		String scope = "https://www.googleapis.com/auth/calendar";
		String client_secret = "ygIy2-y40S1Fer0B3oU_coVn"; 
		String code = "4/sDpXijSVGCPeC6dGec8OBh2PlKdF";
		
		//getting the code
		try {
			//form URL
		    GoogleAuthorizationRequestUrl url_string = new GoogleAuthorizationRequestUrl(client_id, redirect_uri_local, scope);
		    URL url = new URL(url_string.build());
		    URI uri = new URI(url_string.build());
		    java.awt.Desktop.getDesktop().browse(uri);
		    //Socket listener = new Socket(InetAddress.getLocalHost(), 1337);
		    //BufferedReader rd = new BufferedReader(new InputStreamReader(listener.getInputStream()));		    
		    
		    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		    conn.setFollowRedirects(false);
		    conn.connect();
		    int responseCode = conn.getResponseCode();
		    System.out.println("response code = "+responseCode);
		    String new_url = null;
		    while (true) {
		    	responseCode = conn.getResponseCode();
		    	//System.out.println("response code = "+responseCode);
		    	if (responseCode != 200) {
		    		System.out.println("NOT 200! = "+responseCode);
		    	}
			    if (responseCode == 302) {
			    	new_url = conn.getHeaderField("location");
			    	break;
			    }
		    }
		    System.out.println("URL new = " + new_url);
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//		    String line;
//			while ((line = rd.readLine()) != null) {
//				System.out.println(line);
//			}
//		    URL currUrl = conn.getURL();
//		    while (true) {
//		    		currUrl = conn.getURL();
//		    		System.out.println("URL = "+currUrl);
//		    }
			//close
			//rd.close();
		    
		    
		    
		    //HttpServletResponseWrapper .sendRedirect(builder.build());
			
			
//			//getting the access token
//			GoogleAuthorizationCodeGrant request = new GoogleAccessTokenRequest.GoogleAuthorizationCodeGrant(new NetHttpTransport(), new JacksonFactory(), client_id, client_secret, code, redirect_uri);
//			
//			AccessTokenResponse response = request.execute();
//			System.out.println("Access token: " + response.accessToken);
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
		
		 
		
		
//			URL url;
//			try {
//				//data
//				url = new URL("https://accounts.google.com/o/oauth2/token");
//				String type = "application/x-www-form-urlencoded";
//				String rawData = "code=4/IgpQJSUJB_Z12Qpu6UvJybYYy319&" +
//						"cliend_id=1034117539945.apps.googleusercontent.com&" +
//						"client_secret=ygIy2-y40S1Fer0B3oU_coVn&" +
//						"redirect_uri=urn:ietf:wg:oauth:2.0:oob&" +
//						//"redirect_uri=http://localhost&" +
//						"grant_type=authorization_code";
//				String encodedData = java.net.URLEncoder.encode(rawData, "UTF-8");
//				//System.out.println(encodedData);
//				
//				//connect to url
//				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//			    conn.setDoOutput(true);
//				conn.setRequestMethod("POST");
//				conn.setRequestProperty("Content-Type", type);
//				
//				//send info
//				OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
//				wr.write(rawData);
//				wr.flush();		
//				
//				//System.out.println(conn.getResponseCode() + " "+conn.getResponseMessage());
//				//read back
//				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//				String line;
//				while ((line = rd.readLine()) != null) {
//					System.out.println(line);
//				}
//				
//				//close
//				wr.close();
//				rd.close();
//			} 
//			catch (MalformedURLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			catch (UnsupportedEncodingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//			}
//			catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}	
	}
	
	public void getCode() throws MalformedURLException {
//		//url
//		URL url = new URL("https://accounts.google.com/o/oauth2/auth?" +
//				"response_type=code&" +
//				"client_id=1034117539945.apps.googleusercontent.com&" +
//				"redirect_uri=http%3A%2F%2Flocalhost" +
//				"scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fcalendar");
//				//"https://accounts.google.com/o/oauth2/auth?");//+"scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile"
//				//https://accounts.google.com/o/oauth2/auth?response_type=code&client_id=1034117539945.apps.googleusercontent.com&redirect_uri=http%3A%2F%2Flocalhost&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fcalendar
//				//https://accounts.google.com/o/oauth2/auth?response_type=code&client_id=1034117539945.apps.googleusercontent.com&redirect_uri=urn%3Aietf%3Awg%3Aoauth%3A2.0%3Aoob&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fcalendar
//				
//		try {
//			//connect to localhost
//			//HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//			//conn.
//			Socket listener = new Socket(InetAddress.getLocalHost(), 80);
//			InputStream input = listener.getInputStream();
//			System.out.println("input = "+input.read());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		
		
	}
	
	public GoogleCalendars importCalendarGroup(org.joda.time.DateTime st, org.joda.time.DateTime et) throws IOException, ServiceException, com.google.gdata.util.ServiceException {
		//calendar group
		GoogleCalendars allCalendars = new GoogleCalendars(st, et, _owner);
		//set URL to get calendars
		URL feedUrl = new URL("https://www.google.com/calendar/feeds/default/");
		//write query
		CalendarQuery myQuery = new CalendarQuery(feedUrl);
		myQuery.setMinimumStartTime(new com.google.gdata.data.DateTime(st.getMillis()));
		myQuery.setMaximumStartTime(new com.google.gdata.data.DateTime(et.getMillis()));
		//send request and receive feed
		CalendarFeed resultFeed = _client.query(myQuery, CalendarFeed.class);
		
		//NOTE:
		//calendarentry = calendar in a list of calendars
		//calendar event entry = event in single calendar
		
		//go through feed results and make calendars
        for (int i = 0; i < resultFeed.getEntries().size(); i++) {
            CalendarEntry calendar = resultFeed.getEntries().get(i);
            System.out.println("cal access level = "+calendar.getAccessLevel().EDITOR.getValue());
            
            if (calendar.getAccessLevel().EDITOR.getValue().equals("editor")) {
            
	            //NOTE:
	            //for java.util.date month zero indexed, year is something + 1900
	            
	            //make new calendar
	            CalendarResponses currCal = new CalendarResponses(st, et, calendar.getTitle().getPlainText());          
	           
	            //get events for calendar
	            ArrayList<Response> calResponses = this.getEvents(st, et, calendar.getId());
	            currCal.setResponses(calResponses);            
	            
	            //add calendar to group of calendars
	            allCalendars.addCalendar(currCal);
	            
	            //TEST
	            currCal.print();
            }
          }
        return allCalendars;
	}
	
	public ArrayList<Response> getEvents(org.joda.time.DateTime st, org.joda.time.DateTime et, String calID) throws IOException, ServiceException, com.google.gdata.util.ServiceException {
		ArrayList<Response> responseList = new ArrayList<Response>();
		//get URL
		String sub1 = calID.substring(0,37);
		String sub2 = calID.substring(55,calID.length());
		calID = sub1+sub2;
		URL feedUrl = new URL(calID+"/private/full");
		System.out.println("URL for event feed = "+feedUrl);
		
		//make query
		CalendarQuery myQuery = new CalendarQuery(feedUrl);
		myQuery.setMinimumStartTime(new com.google.gdata.data.DateTime(st.getMillis()));
		myQuery.setMaximumStartTime(new com.google.gdata.data.DateTime(et.getMillis()));
		myQuery.setStringCustomParameter("orderby", "starttime");
		myQuery.setStringCustomParameter("sortorder", "ascending");
		myQuery.setStringCustomParameter("singleevents", "true");
		myQuery.setMaxResults(100);
		//send request and get result feed
		CalendarEventFeed resultFeed = _client.query(myQuery, CalendarEventFeed.class);
		
		//go through feed and get events to make responses
		for (int i = 0; i < resultFeed.getEntries().size(); i++) {
			CalendarEventEntry event = resultFeed.getEntries().get(i);
			List<When> times = event.getTimes();
			long startTime = times.get(0).getStartTime().getValue();
			long endTime = times.get(0).getEndTime().getValue();
			Response eventResponse = new Response(new org.joda.time.DateTime(startTime), new org.joda.time.DateTime(endTime), event.getTitle().getPlainText());
			responseList.add(eventResponse);
		}
		return responseList;
	}
	
    public static void main(String[] args) throws IOException, ServiceException, com.google.gdata.util.ServiceException {
    	GCalImporter myImporter = new GCalImporter();
    	org.joda.time.DateTime startTime = new org.joda.time.DateTime(2011, 6, 28, 8, 0);
		org.joda.time.DateTime endTime = new org.joda.time.DateTime(2011, 6, 30, 23, 0);
    	myImporter.importMyGCal(startTime, endTime);
//    	myImporter.getCode();
//    	myImporter.post();
    	System.out.println("DONE");
    }

	@Override
	public CalendarGroup importCalendarGroup() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public GoogleCalendars refresh(org.joda.time.DateTime st, org.joda.time.DateTime et) throws IOException, ServiceException, com.google.gdata.util.ServiceException {
		return this.importMyGCal(st, et);
	}
	
	
	
}

