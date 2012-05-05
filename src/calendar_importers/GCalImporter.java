package calendar_importers;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
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

import org.joda.time.DateTime;

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

//edit recurring events
//return null if no access

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
	
	public UserCal importMyGCal(org.joda.time.DateTime startTime, org.joda.time.DateTime endTime) throws IOException, ServiceException, com.google.gdata.util.ServiceException {
		//authenticate user
		GoogleTokenResponse toke = (GoogleTokenResponse) _auth.setAuth();
		if (toke == null) {
			return null;
		}
		_client.setAuthSubToken(toke.getAccessToken());
		//import calendars -- make calendar group
		return this.importCalendarGroup(startTime, endTime, null);
	}
	
//	private synchronized void timeOut() {
//		notifyAll();
//	}
//	
//	public synchronized Socket serverReady(InetAddress ad, int port) throws IOException {  
//        Socket listener = null;  
//        while (true) {  
//            try {  
//                listener = new Socket(ad, port);  
//            } 
//            catch (ConnectException ignore) {}  
//            if (listener == null) {  
//                new Timer(true).schedule(new TimerTask() {  
//                                    public void run() {  
//                                        timeOut();  
//                                    }  
//                                },  
//                                1000);  
//                 try {  
//                     wait();  
//                 } 
//                 catch (InterruptedException ignore) {}  
//            } 
//            else {  
//                break;  
//            }  
//        }  
//        return listener;  
//    }  
	
//	private String get(String urlString) {
//		URL url;
//		StringBuffer resp = new StringBuffer();
//		try {
//			url = new URL(urlString);
//			URLConnection conn = url.openConnection();
//			conn.setDoOutput(true);
//		    
//		    // Get the response
//		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//		    
//		    String line;
//		    while ((line = rd.readLine()) != null) 
//		        resp = resp.append(line);
//		    
//		} catch (Exception e) {
//
//		}
//		// TODO implement properly
//		return resp.toString();
//	}
	
	public UserCal importCalendarGroup(org.joda.time.DateTime st, org.joda.time.DateTime et, CalendarGroup calgroup) throws IOException, ServiceException, com.google.gdata.util.ServiceException {
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
		
		ArrayList<CalendarEntry> selectedCals = new ArrayList<CalendarEntry>();
		ArrayList<String> allCals_titles = new ArrayList<String>();
		ArrayList<CalendarEntry> allCals = new ArrayList<CalendarEntry>();
		
		if (calgroup != null) {
			//go through feed results and make calendars
	        for (int i = 0; i < resultFeed.getEntries().size(); i++) {
	            CalendarEntry calendar = resultFeed.getEntries().get(i);  
	            allCals_titles.add(calendar.getTitle().getPlainText());
	            allCals.add(calendar);
	            if (((CalendarResponses)calgroup.getCalendars().get(i)).isSelected()) {
	            	selectedCals.add(calendar);
	            }
	        }
		}
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
					// TODO Auto-generated catch block
					e.printStackTrace();
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
            CalendarResponses currCal = new CalendarResponses(st, et, calendar.getTitle().getPlainText());          
           
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
	
    public static void main(String[] args) throws IOException, ServiceException, com.google.gdata.util.ServiceException {
    	GCalImporter myImporter = new GCalImporter();
    	org.joda.time.DateTime startTime = new org.joda.time.DateTime(2012, 4, 20, 8, 0);
		org.joda.time.DateTime endTime = new org.joda.time.DateTime(2012, 4, 29, 23, 0);
    	myImporter.importMyGCal(startTime, endTime);
    	myImporter.refresh(startTime, endTime);
    }
	
	public UserCal refresh(org.joda.time.DateTime st, org.joda.time.DateTime et, CalendarGroup calgroup) {
		TokenResponse toke = _auth.getRefreshToken();
		_client.setAuthSubToken(toke.getAccessToken());
		try {
			return this.importCalendarGroup(st, et, calgroup);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	//@Override
	public CalendarGroup<CalendarResponses> importCalendarGroup(String url)
			throws MalformedURLException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CalendarGroup<CalendarResponses> importNewEvent(String url)
			throws MalformedURLException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	
	private class SelectCalListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			Object[] titles = _calList.getSelectedValues();
			int[] ints = _calList.getSelectedIndices();
			for (Object i : titles) {
				System.out.println(i.toString());
			}
			for (int i : ints) {
				System.out.println(i);
			}
			_selectedInd = ints;
			_listFrame.setVisible(false);
			_buttonClicked = true;
		}
	}


	@Override
	public CalendarGroup<CalendarResponses> refresh(DateTime st, DateTime et) {
		// TODO Auto-generated method stub
		return null;
	}
}

