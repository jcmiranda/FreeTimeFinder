package calendar_importers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import calendar.CalendarImpl;
import calendar.Response;

import com.google.gdata.client.calendar.CalendarQuery;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.calendar.CalendarFeed;
import com.google.gdata.data.extensions.When;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

//import org.joda.time.DateTime;

//auth stuff:
	//http://code.google.com/apis/gdata/docs/auth/clientlogin.html
	//http://www.java-samples.com/java/POST-toHTTPS-url-free-java-sample-program.htm
	//http://www.java-samples.com/java/POST-toHTTPS-url-free-java-sample-program.htm
	//http://code.google.com/apis/gdata/docs/auth/overview.html#ClientLogin


public class GCalImporter {// implements CalendarsImporter {
	private CalendarService _client;
	
	public GCalImporter() throws IOException, ServiceException {
		//TEST start and end dates
		org.joda.time.DateTime startTime = new org.joda.time.DateTime(2011, 6, 28, 8, 0);
		org.joda.time.DateTime endTime = new org.joda.time.DateTime(2011, 6, 28, 23, 0);
		//connect to client
		_client = new CalendarService("yourCompany-yourAppName-v1");
		//authenticate user
		this.setAuth();
		//import calendars -- make calendar group
		this.importCalendarGroup(startTime, endTime);
		System.out.println("done");
	}
	
	public void setAuth() throws AuthenticationException, MalformedURLException {
		//String user = ...
		
		//get username and password from GUI
		String username = "kelly_buckley@brown.edu";
		String password = "99macaroons";
				
		URL feedURL = new URL("https://www.google.com/accounts/ClientLogin");

		
		//HttpsURLConnection connection = (HttpsURLConnection) URL.openConnection();
		//connection.setDoInput(true); 
		//connection.setDoOutput(true);
		
		//connection.setRequestMethod("POST"); 
		//connection.setFollowRedirects(true); 
		
		_client.setUserCredentials(username, password);
	}
	
	//@Override
	//public CalendarGroup importCalendarGroup() throws IOException, ServiceException {
		
	public ArrayList<CalendarImpl> importCalendarGroup(org.joda.time.DateTime st, org.joda.time.DateTime et) throws IOException, ServiceException {
		//calendar group
		ArrayList<CalendarImpl> allCalendars = new ArrayList<CalendarImpl>();
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
            CalendarImpl currCal = new CalendarImpl(st, et, calendar.getTitle().getPlainText());
            
            
          //TEST           
            System.out.println(calendar.getTitle().getPlainText());
            System.out.println("curr call start time = "+currCal.getStartTime());
            System.out.println("cal id = "+calendar.getId());
            System.out.println("===================================================");
            
            
            //get events for calendar
            ArrayList<Response> calResponses = this.getEvents(st, et, calendar.getId());
            //ArrayList<Response> calResponses = this.getEvents(st, et, "http://www.google.com/calendar/feeds/default/calendars/iho4gt7a4fvus6qk1s1qc542q8%40group.calendar.google.com");
            currCal.setResponses(calResponses);
            //add calendar to group of calendars
            allCalendars.add(currCal);
            
            //this.getEvents(currCal);
            
            
          }
        return allCalendars;
	}
	
	public ArrayList<Response> getEvents(org.joda.time.DateTime st, org.joda.time.DateTime et, String calID) throws IOException, ServiceException {
		ArrayList<Response> responseList = new ArrayList<Response>();
		//URL feedUrl = new URL("https://www.google.com/calendar/feeds/default/private/full");
		//get URL
		//URL feedUrl = new URL("http://www.google.com/calendar/feeds/iho4gt7a4fvus6qk1s1qc542q8%40group.calendar.google.com/private/full");
		System.out.println("cal id = "+calID);
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
			
			//TEST
			System.out.println("event response start time = "+eventResponse.getStartTime());
			System.out.println("event title = "+eventResponse.getName());
			System.out.println("...................................................");
		}
		return responseList;
	}
	
    public static void main(String[] args) throws IOException, ServiceException {
    	new GCalImporter();
    }

}

