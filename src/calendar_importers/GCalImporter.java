package calendar_importers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
//import com.google.gdata.util.AuthenticationException;
//import com.google.gdata.util.ServiceException;
import com.google.gdata.client.*;

//auth stuff:
	//http://code.google.com/apis/gdata/docs/auth/clientlogin.html
	//http://www.java-samples.com/java/POST-toHTTPS-url-free-java-sample-program.htm
	//http://www.java-samples.com/java/POST-toHTTPS-url-free-java-sample-program.htm
	//http://code.google.com/apis/gdata/docs/auth/overview.html#ClientLogin

//edit recurring events

//edit all day

//make interface??

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
	
/*	public void secureToken() {
		https://accounts.google.com/o/oauth2/auth?
		scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile&
		redirect_uri=urn:ietf:wg:oauth:2.0:oob&
		response_type=code&
		client_id=812741506391-h38jh0j4fv0ce1krdkiq0hfvt6n5amrf.apps.googleusercontent.com
	}*/
	
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
        return allCalendars;
	}
	
	public ArrayList<Response> getEvents(org.joda.time.DateTime st, org.joda.time.DateTime et, String calID) throws IOException, ServiceException, com.google.gdata.util.ServiceException {
		ArrayList<Response> responseList = new ArrayList<Response>();
		//get URL
		String sub1 = calID.substring(0,37);
		String sub2 = calID.substring(55,calID.length());
		calID = sub1+sub2;
		URL feedUrl = new URL(calID+"/private/full");
		
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
		org.joda.time.DateTime endTime = new org.joda.time.DateTime(2011, 7, 15, 23, 0);
    	myImporter.importMyGCal(startTime, endTime);
    }

	@Override
	public CalendarGroup importCalendarGroup() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public GoogleCalendars refresh(org.joda.time.DateTime st, org.joda.time.DateTime et) throws IOException, ServiceException, com.google.gdata.util.ServiceException {
		return this.importCalendarGroup(st, et);
	}
	
	
	
}

