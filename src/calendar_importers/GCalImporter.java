package calendar_importers;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import calendar.CalendarGroup;
import calendar.CalendarResponses;
import calendar.GoogleCalendars;
import calendar.Owner;
import calendar.Response;
import calendar.UserCal;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.gdata.client.calendar.CalendarQuery;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.calendar.CalendarFeed;
import com.google.gdata.data.extensions.When;
import com.google.gdata.util.ServiceException;

/*
 * This class imports a user's Google calendar.  Step one: authenticate user, step two: pull in calendar
 * Class extends CalendarsImporter
 * Class contains GCalAuth
 */

public class GCalImporter implements CalendarsImporter<CalendarResponses> {
	private CalendarService _client;
	int MAX_RESPONSES = Integer.MAX_VALUE;
	private Owner _owner;
	private GCalAuth _auth;
	private JList _calList;
	private JFrame _listFrame;
	private int[] _selectedInd;
	private boolean _buttonClicked;
	
	public GCalImporter() {
		//connect to client
		_client = new CalendarService("yourCompany-yourAppName-v1");
		_auth = new GCalAuth();
	}
	
	/*
	 * Purpose: set authentication (assuming NO response token is stored) and pull in calendars (i.e. transform calendar
	 * data into internal calendar data structure)
	 * Input: start time, end time of calendar you want to pull in
	 * Output: a UserCal holding selected calendars and events in calendars
	 */
	public UserCal importMyGCal(org.joda.time.DateTime startTime, org.joda.time.DateTime endTime) throws IOException, ServiceException, com.google.gdata.util.ServiceException, URISyntaxException {
		//authenticate user
		GoogleTokenResponse toke = (GoogleTokenResponse) _auth.setAuth();
		if (toke == null) {
			return null;
		}
		_client.setAuthSubToken(toke.getAccessToken());
		//import calendars -- make calendar group
		return this.importCalendarGroup(startTime, endTime, null);
	}
	
	/*
	 *Purpose: Assuming authentication has already taken place, makes GET requests to google for "feeds" i.e.
	 *output streams of calendars and events for calendars, transforms this into internal data structures
	 *Only import "selected" calendars, if calgroup null, have user select calendars, else figure out which cals in user
	 *cal are selected and import those
	 *Input: start time and end time of when to import, UserCal if importing has already taken place at least once
	 *Output: newly imported UserCal
	 */
	public UserCal importCalendarGroup(org.joda.time.DateTime st, org.joda.time.DateTime et, UserCal calgroup) throws IOException, ServiceException, com.google.gdata.util.ServiceException {
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
		
		ArrayList<CalendarEntry> selectedCals = new ArrayList<CalendarEntry>();
		ArrayList<String> allCals_titles = new ArrayList<String>();
		ArrayList<CalendarEntry> allCals = new ArrayList<CalendarEntry>();
		
		//if stored calendar already
		if (calgroup != null) {
			//go through feed results and make calendars
	        for (int i = 0; i < resultFeed.getEntries().size(); i++) {
	            CalendarEntry calendar = resultFeed.getEntries().get(i);  
	            allCals.add(calendar);
	            //CalendarResponses thisCal = calgroup.getCalByName(calendar.getTitle().getPlainText());
	            CalendarResponses thisCal = calgroup.getCalById(calendar.getId());
            	if (thisCal != null && thisCal.isSelected()){ // && thisCal.getId().equals(calendar.getId())) { 
	            	selectedCals.add(calendar);
	            }
            	allCals_titles.add(calendar.getTitle().getPlainText());
	        }
		}
		//if importing for the first time
		else {
			_buttonClicked = false;
	        for (int i = 0; i < resultFeed.getEntries().size(); i++) {
	            CalendarEntry calendar = resultFeed.getEntries().get(i);  
	            allCals_titles.add(calendar.getTitle().getPlainText());
	            allCals.add(calendar);
	        }
	        //GUI//////
	    	JButton submit = new JButton("Submit");
	        submit.addActionListener(new SelectCalListener());
	        JPanel buttonPanel = new JPanel();
			buttonPanel.setPreferredSize(new Dimension(300, 45));
	        buttonPanel.add(submit);
	        JPanel directionsPanel = new JPanel();
	        directionsPanel.setPreferredSize(new Dimension(300,45));
	        directionsPanel.setLayout(new BorderLayout());
	        JLabel directions = new JLabel("Select calendars to import");
	        JLabel moreDirections = new JLabel("(use CTRL to select multiple calendars)");
	        directionsPanel.add(directions, BorderLayout.NORTH);
	        directionsPanel.add(moreDirections, BorderLayout.CENTER);
	        _calList = new JList(allCals_titles.toArray());
	        JScrollPane scrollCalList = new JScrollPane(_calList);
	        _calList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	        _listFrame = new JFrame();
	        _listFrame.setLayout(new BorderLayout());
	        _listFrame.add(buttonPanel, BorderLayout.SOUTH);
	        _listFrame.add(scrollCalList, BorderLayout.CENTER);
	        _listFrame.add(directionsPanel, BorderLayout.NORTH);
	        _listFrame.setLocationRelativeTo(null);
	        _listFrame.setVisible(true);
	        _listFrame.pack();
	        ////////////
	        //wait for the button to be clicked
	        while (!_buttonClicked) {
	        	try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
	        }
	        //add cals at selected indices to list of selected calendars
	        for (int i : _selectedInd) {
	        	selectedCals.add(allCals.get(i));
	        }	        
		}

        for (int i = 0; i < allCals.size(); i++) {
            CalendarEntry calendar = allCals.get(i);             
            //make new calendar (for ALL calendars)
            CalendarResponses currCal = new CalendarResponses(st, et, calendar.getTitle().getPlainText(), calendar.getId());          
            for (CalendarEntry c : selectedCals) {
            	if (calendar == c) {
            		currCal.setSelected(true);
            		//get events for calendar (ONLY if selected)
                    ArrayList<Response> calResponses = this.getEvents(st, et, calendar.getId());
                    currCal.setResponses(calResponses);  
            	}
            }
            //add calendar to group of calendars
            allCalendars.addCalendar(currCal);
            
            //TEST
            //currCal.print();
            
          }
        return allCalendars;
	}
	
	/*
	 * Purpose: get event feed for a specific calendar and transform it into a response
	 * Input: start time and end time of importing, the calendar ID so only import events from that calendar
	 * Output: an array of responses i.e. events
	 */
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
		myQuery.setMaxResults(MAX_RESPONSES);
		//send request and get result feed
		CalendarEventFeed resultFeed = null;
		try {
			resultFeed = _client.query(myQuery, CalendarEventFeed.class);
			//go through feed and get events to make responses
			for (int i = 0; i < resultFeed.getEntries().size(); i++) {
				CalendarEventEntry event = resultFeed.getEntries().get(i);
				List<When> times = event.getTimes();
				long startTime = times.get(0).getStartTime().getValue();
				long endTime = times.get(0).getEndTime().getValue();
				if (endTime - startTime < 86400000) {
					Response eventResponse = new Response(new org.joda.time.DateTime(startTime), new org.joda.time.DateTime(endTime), event.getTitle().getPlainText());
					responseList.add(eventResponse);
				}
			}
		} 
		catch (ServiceException e) {
			System.out.println("Calendar query failed: service exception");
		}
		
		return responseList;
	}
	
	/*
	 * Purpose: if user has already authenticated, get refresh token to authenticate (no sign-in necessary) and
	 * re-import selected calendars (just calls importCalendarGroup)
	 * Input: start time, end time, old calendar group (says what's selected)
	 * Output: newly imported UserCal
	 */
	public UserCal refresh(org.joda.time.DateTime st, org.joda.time.DateTime et, UserCal calgroup) {
		TokenResponse toke = _auth.getRefreshToken();
		_client.setAuthSubToken(toke.getAccessToken());
		try {
			return this.importCalendarGroup(st, et, calgroup);
		} catch (IOException e) {
		} catch (ServiceException e) {
		}
		return null;
	}
	/*
	 * Purpose: listener for when user first importing calendar and must choose from a list of their calendars which
	 * to import
	 * Gets what indices were selected, returns an array of them, and importCalendarGroup matches indices to calendars
	 */
	private class SelectCalListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			Object[] titles = _calList.getSelectedValues();
			int[] ints = _calList.getSelectedIndices();
			_selectedInd = ints;
			_listFrame.setVisible(false);
			_buttonClicked = true;
		}
	}
	
	@Override
	public CalendarGroup<CalendarResponses> importNewEvent(String url)
			throws MalformedURLException, IOException {
		return null;
	}
}

