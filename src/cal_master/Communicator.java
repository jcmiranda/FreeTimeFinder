package cal_master;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.joda.time.DateTime;

import calendar.*;
import calendar.Event.*;
import calendar_exporters.*;
import calendar_exporters.When2MeetExporter.*;
import calendar_importers.*;
import calendar_importers.EventImporter.*;

import com.google.gdata.util.ServiceException;
import com.thoughtworks.xstream.XStream;

import ftf.TimeFinderSlots;
import gui.GuiConstants;

/**
 * Represents the main “server class” that receives requests from the GUI (client) and delegates to the 
 * appropriate class (e.g. importers, exporters, converters, XML storage)	
 *
 */

public class Communicator {

	private CalendarsImporter<CalendarResponses> _userCalImporter = null;
	private StoredDataType _userCalImporterType = null;
	
	private UserCal _userCal = null;
	private String _userCalID = "userCal", _indexID = "index", 
			_userCalImporterID = "userCalImporter", _progOwnerID = "progOwner";

	private HashMap<String, Event> _events = new HashMap<String, Event>();
	private EventImporter _eventImporter = new EventImporter();
	private When2MeetExporter _exporter = new When2MeetExporter();

	private Converter _converter = new Converter();
	private TimeFinderSlots _timeFinder = new TimeFinderSlots();
	private ProgramOwner _progOwner = new ProgramOwner();
	private JDialog _loadingDialog;
//	private JFrame _loadingFrame;
	private JLabel _loadingLabel;
//	private JPanel _loadingPanel;
	
	private ImageIcon _kairosIcon = new ImageIcon("KairosIcon.png");
		
	private XStream _xstream = new XStream();

	private static final double ATTENDEE_PERCENTAGE = 0;
	private static final int NUM_SUGGESTIONS = 5;


	public Communicator() {

		_loadingDialog = new JDialog(new JFrame());
		_loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		_loadingLabel = new JLabel();

		_loadingLabel.setFont(new Font(GuiConstants.FONT_NAME, _loadingLabel.getFont().getStyle(), _loadingLabel.getFont().getSize()));
		JPanel loadingPanel = new JPanel();
		
		loadingPanel.add(new JLabel(new ImageIcon("KairosIcon.png")));
		loadingPanel.add(_loadingLabel);
		_loadingDialog.add(loadingPanel);
		_loadingDialog.setSize(300, 50);
		_loadingDialog.setUndecorated(true);
	}
	
	/**
	 * Display loading message to the user while refreshing/starting the program
	 * 
	 * @param msg -- message to display to the user
	 */
	private void showLoadingLabel(String msg){

		
		_loadingDialog.setLocationRelativeTo(null);
		_loadingDialog.setVisible(true);	
		_loadingLabel.setText(msg);
//		_loadingLabel.repaint();
//		
//		_loadingDialog.getContentPane().invalidate();
//		_loadingDialog.getContentPane().validate();
//		_loadingDialog.getContentPane().repaint();
//		
//		_loadingDialog.repaint();
		_loadingDialog.pack();
//		_loadingDialog.invalidate();
//		_loadingDialog.validate();
//		_loadingDialog.repaint();
//		_loadingDialog.pack();
		
	}


	private void hideLoadingLabel(){
		_loadingDialog.setVisible(false);
	}

	/**
	 * TODO
	 */
	private void setUpXStream() {
		
		_xstream.alias("index", Index.class);
		_xstream.alias("storeddatatype", StoredDataType.class);
		_xstream.alias("calendarslots", CalendarSlots.class);
		_xstream.alias("calendarresponses", CalendarResponses.class);
		_xstream.alias("calendargroup", CalendarGroup.class);
		_xstream.alias("calendar", Calendar.class);
		_xstream.alias("when2meetevent", When2MeetEvent.class);
		_xstream.alias("when2meetowner", When2MeetOwner.class);
		_xstream.alias("eventupdate", EventUpdate.class);
		_xstream.alias("avail", Availability.class);
	}
	
	/**
	 * TODO
	 * @return
	 */
	public Index recreateIndex() {
		File indexFile = new File(_indexID+".xml");
		if(indexFile.exists())
			return (Index) _xstream.fromXML(indexFile);
		else
			return new Index();
	}
	
	/**
	 * Initializes program by pulling in all saved data and creating objects from it
	 * @throws URISyntaxException 
	 */
	public void startUp() throws URISyntaxException {

		setUpXStream();
		
		// If have an index, recreate index
		Index index = recreateIndex();

		showLoadingLabel("Loading...");

		// Pull in XML and create when2meet events from XML
		// Pull in XML for user cal, and create user cal
		for(String id : index.getFiles()) {
			StoredDataType type = index.getType(id);
			assert type != null;
			Object o = _xstream.fromXML(new File(id + ".xml"));
			switch(type) {
			case When2MeetEvent: {
				_events.put(id,  (When2MeetEvent) o);
				break;
			}
			case GCal: {
				if(_userCal != null)
					System.out.println("Two google calendars were recreated from index");
				_userCal = (GoogleCalendars) o;
				break;
			}
			case ProgramOwner: {
				_progOwner = (ProgramOwner) o;
				break;
			} 
			case GCalImporter: {
				_userCalImporter = (GCalImporter) o;
				_userCalImporterType = StoredDataType.GCalImporter;
				break;
			}
			default: {
				System.out.println("Default condition triggered on recreating index.");
			}
			}

		}
		hideLoadingLabel();

		if(_progOwner == null) {
			getNewOwnerName();
		}

		
		//if no calendar, pull in users calendar	
		if(_userCal == null) {
			URL googleTestURL = null;
			try {
				googleTestURL = new URL("http://www.google.com");
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (webConnected(googleTestURL)) {
				// TODO add a never option
				Object[] options = {"Yes", "Not now"};
				int n = JOptionPane.showOptionDialog(null, "Would you like to import your calendar?",
						"", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, _kairosIcon,
						options, options[0]);
								
				if(n == 0) {
	
					// from where would you like to import?
					Object[] calOptions = {"Google Calendar" };
					Object selectedValue = JOptionPane.showInputDialog(null, "Choose a calendar type to import.", "", 
							JOptionPane.INFORMATION_MESSAGE, _kairosIcon, calOptions, calOptions[0]);
	
					// switch on user response
					if(selectedValue == "Google Calendar"){
						this.setCalImporter(new GCalImporter());
						_userCalImporterType = StoredDataType.GCalImporter;
						saveOneItem(_userCalImporter, _userCalImporterID, _userCalImporterType);
						
						try {
							showLoadingLabel("Retrieving calendar...");
	
							_userCal = ((GCalImporter)_userCalImporter).importMyGCal(DateTime.now(), DateTime.now().plusDays(30));
	
							// Save their calendar importer to save their updated auth codes
							saveOneItem(_userCalImporter, _userCalImporterID, _userCalImporterType);
	
							hideLoadingLabel();
	
						} catch (IOException e) {
						} catch (ServiceException e) {
						}
					}
				}
			}
		}
		
		URL testInternetURL = null;
		try {
			testInternetURL = new URL("http://www.google.com");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Refresh when2meet events and calendars
		if (webConnected(testInternetURL)) {
			refresh();	
		}
		else {
			JOptionPane.showMessageDialog(null, "You are not connected to the Internet.\nKairos cannot import current data.", "Connection Error", JOptionPane.ERROR_MESSAGE, _kairosIcon);
		}
	}
	
	/**
	 * Returns whether there are any events stored in the program
	 */
	public boolean hasEvent() {
		return _events != null && _events.size() > 0;
	}
	
	
	public boolean hasUserCal() {
		return _userCal != null;
	}

	public UserCal getUserCal() {
		return _userCal;
	}
	
	/**
	 * Stores whether a calendar in the userCalendar should be repulled/displayed 
	 * @param calRespId -- calendar to set
	 * @param selected -- whether it should be displayed/repulled
	 */
	public void setSelectedInUserCal(String calRespId, boolean selected){
		if(_userCal != null){
			ArrayList<CalendarResponses> cals = _userCal.getCalendars();
			for(CalendarResponses calResp : cals){
				if(calResp.getId().equals(calRespId)){
					calResp.setSelected(selected);
					break;
				}
			}
		}
	}

	/** SAVING **/

	/**
	 * TODO
	 */
	private void writeToFile(String filename, Object o) {
		Writer out = null;
		try {
			out = new BufferedWriter(new FileWriter(filename+".xml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		_xstream.toXML(o, out);	
	}

	/**
	 * TODO
	 * @param temp
	 */
	private void saveIndex(Index temp) {
		writeToFile(_indexID, temp);
	}

	/**
	 * Saves one item
	 * @param o -- item to save
	 * @param id -- name of the file to which to save it (identified by the object's ID)
	 * @param type -- type of data the item represents
	 */
	public void saveOneItem(Object o, String id, StoredDataType type) {
		// Check index to see if it exists
		Index temp = recreateIndex();
		temp.addItem(id, type);
		saveIndex(temp);
		writeToFile(id, o);
	}

	/**
	 * Removes an item from the index
	 * @param id -- id of the item to remove
	 */
	public void removeOneItem(String id) {
		Index temp = recreateIndex();
		temp.removeItem(id);
		saveIndex(temp);
	}

	
	/**
	 * Adds an event to the program to be stored based on a URL
	 * 
	 * @param url -- URL of the event you want to add
	 * @return -- Object representation of the event you want
	 * @throws URLAlreadyExistsException
	 * @throws IOException
	 * @throws InvalidURLException 
	 */
	public Event addEvent(String url) throws URLAlreadyExistsException, IOException, InvalidURLException {
		
		// Check if we have this url already
		for(Event event : _events.values())
			// If we do, throw an error
			if(event.getURL().equals(url)) {
				System.out.println("URL already found");
				throw new URLAlreadyExistsException();
			}

		showLoadingLabel("Retrieving event...");
		
		// pull in that when2meet using an importer, bubbling up any exceptions the importer throws for display to the user
		Event newEvent;
		newEvent = _eventImporter.importNewEvent(url);

		// add event to the xml for storage
		StoredDataType eventType = calGroupTypeToIndexType(newEvent.getCalGroupType());
		String id = newEvent.getID() + "";
		_events.put(id, newEvent);
		saveOneItem(newEvent, id, eventType);
		
		hideLoadingLabel();

		return newEvent;
	}

	/**
	 * Converts from type of CalendarGroup to type of data to store 
	 */
	public StoredDataType calGroupTypeToIndexType(CalGroupType type) {
		switch(type) {
		case When2MeetEvent: { return StoredDataType.When2MeetEvent; }
		case GCal: {return StoredDataType.GCal;}
		}
		return null;
	}

	public void setUserCal(UserCal userCal){
		_userCal = userCal;
		StoredDataType type = calGroupTypeToIndexType(_userCal.getCalGroupType());
		saveOneItem(_userCal, _userCalID, type);
	}

	/**
	 * Remove event, erasing it from storage
	 * @param eventID -- ID of event to remove
	 */
	public void removeEvent(String eventID) {
		// Check that we do have an event with this ID
		Event toRemove = _events.get(eventID);

		// If we do, remove it form our list of events
		// Save this when2meet, and our new index
		if(toRemove != null){
			String id = eventID;
			_events.remove(id);
			removeOneItem(id);
		}

		// If we didn't have it, huh? confused, how did this happen
	}

	/**
	 * Set the importer used for importing the user calendar, saving it to file
	 */
	public void setCalImporter(CalendarsImporter<CalendarResponses> importer){
		//System.out.println("Cal importer set");
		_userCalImporter = importer;
		StoredDataType type = null;
		if(_userCalImporter.getClass() == GCalImporter.class)
			type = StoredDataType.GCalImporter;
		else {
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Wrong type of cal importer");
			}

		}
		this.saveOneItem(_userCalImporter, _userCalImporterID, type);
	}

	public void setOwnerName(String name){
		_progOwner.setName(name);
	}

	/**
	 * Refreshes display by repulling all events and the userCal and saving the new versions
	 */
	public void refresh() {
		
		// Update and save all when2meet events
		When2MeetEvent temp = null;
		showLoadingLabel("Retrieving Events...");

		for(Event event : _events.values()){
			//repull info
			if(event.getCalGroupType() == CalGroupType.When2MeetEvent) {
				event = _eventImporter.refreshEvent((When2MeetEvent) event);
				saveOneItem(event, event.getID()+"", StoredDataType.When2MeetEvent);
			}
			else
				System.out.println("Invalid event type - not when2meet");
		}

		hideLoadingLabel();

		// Update and save user calendar
		if(_userCal != null){
			showLoadingLabel("Retrieving calendar...");
			pullCal(_userCal.getStartTime(), _userCal.getEndTime());
			saveOneItem(_userCal, _userCalID, StoredDataType.GCal);
			hideLoadingLabel();
		}

	}

	/**
	 * Converts userCal to a CalendarSlots representation determined in part by the event in which it will be used
	 * @param eventID -- event for which the CalSlots rep will be used
	 */
	public void userCalToEvent(String eventID){
		Event event = _events.get(eventID);

		if(event != null && !event.userHasSubmitted()){
			CalendarSlots cal = _converter.calToSlots(_userCal, event);
			event.setUserResponse(cal);
		}
	}

	/**
	 * Check to make sure the data we have stored for the userCal includes the range of dates of the event
	 * @param eventID -- ID of the event we want to check
	 */
	private void checkUserCal(String eventID){
		Event w2m = _events.get(eventID);
		if(w2m != null && _userCal != null){
			
			DateTime wStart = w2m.getStartTime();
			DateTime wEnd = w2m.getEndTime();
			DateTime cStart = _userCal.getStartTime();
			DateTime cEnd = _userCal.getEndTime();
			
			//check to see that w2m in range of userCal
			//If it isn't, pullCal to include the largest range given the two start and two end times 
			if(wStart.isBefore(cStart) || wEnd.isAfter(cEnd)){
				DateTime start, end;

				if(wStart.isBefore(cStart))
					start = wStart;
				else
					start = cStart;

				if(wEnd.isAfter(cEnd))
					end = wEnd;
				else
					end = cEnd;

				pullCal(start, end);
			}
		}
	}
	
	public Event getEventByID(String id){
		return _events.get(id);
	}
	
	
	/**
	 * Return the event that matches the given id
	 * @param id -- id of the event to return
	 * @return -- Event matching id, making sure that the appropriate user response is stored
	 */
	public Event getEvent(String id){
		Event toReturn = _events.get(id);
		
		//if we don't have a stored response for the user
		if(toReturn.getUserResponse() == null){
			
			boolean makeNewUserResponse = true;
			
			if(!toReturn.getCalendars().isEmpty()){
				//ask user if they've responded to the event
				int resp = JOptionPane.showConfirmDialog(null, "Have you already responded to this When2Meet?", "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, _kairosIcon);
					
				//if they have, ask them to select their response from the list of all responses
				if(resp == JOptionPane.YES_OPTION){
	
					makeNewUserResponse = false;
					
					Object[] responseNames = new Object[toReturn.getCalendars().size()];
	
					for(int i=0; i< toReturn.getCalendars().size(); i++){
						responseNames[i] = toReturn.getCalendars().get(i).getOwner().getName();
					}
	
					Object selected = JOptionPane.showInputDialog(null, "Please select the name that represents your response from the list below",
							"", JOptionPane.INFORMATION_MESSAGE, _kairosIcon,
							responseNames, responseNames[0]);
	
					//take the selected cal, remove it from the list, and set it to be the userResponse
					if(selected != null){
						CalendarSlots user = null;
						try {
							user = toReturn.getCalByName(selected.toString());
						} catch (CalByThatNameNotFoundException e) {
							// TODO
						}
						assert user.getOwner() != null;
						toReturn.setUserResponse(user);
						toReturn.setUserSubmitted(true);
						saveOneItem(toReturn, toReturn.getID()+"", calGroupTypeToIndexType(toReturn.getCalGroupType()));
					}
				}
			}
			
			//if they haven't already responded (including the case where NO ONE has responded), set userResponse to be a new CalendarSlots with them as the owner
			if(makeNewUserResponse){
				toReturn.setUserResponse(new CalendarSlots(toReturn.getStartTime(), toReturn.getEndTime(), 15, Availability.free));
				toReturn.getUserResponse().setOwner(new When2MeetOwner(_progOwner.getName(), -1));
				saveOneItem(toReturn, toReturn.getID()+"", calGroupTypeToIndexType(toReturn.getCalGroupType()));
			}

		}

		checkUserCal(id);

		// auto-fill user response based on user calendar, if applicable
		if(!toReturn.userHasSubmitted() && _userCal != null){
			userCalToEvent(id);
		}

		//save user response
		saveOneItem(toReturn, String.valueOf(toReturn.getID()), calGroupTypeToIndexType(toReturn.getCalGroupType())); 

		return toReturn;
	}

	/**
	 * Called if the name the user has chosen to represent their response has been used by another respondee to the given event
	 * User asked as long as they don't press "cancel" and the name they enter has already been used
	 * @param event -- event to which the user is trying to respond
	 * @param currName -- current name they're trying to use
	 * @return -- true if the user completes the process, false if they pres "cancel" at any time
	 */
	private boolean getNewUserName(When2MeetEvent event, String currName){
		String newName = JOptionPane.showInputDialog("The name '" + currName + "' has already been used. Please enter another name.");
		if(newName == null)
			return false;
		boolean isValidName = true;
		CalendarSlots userResp = event.getUserResponse();
		for(CalendarSlots c : event.getCalendars()){
			if(c.getOwner().getName().equalsIgnoreCase(newName)){
				isValidName = false;
				getNewUserName(event, newName);
				break;
			}
		}
		if(isValidName)
			userResp.setOwner(new When2MeetOwner(newName, userResp.getOwner().getID()));
		saveOneItem(event, event.getID()+"", StoredDataType.When2MeetEvent);
		return true;
	}

	/**
	 * Called if the user hasn't yet chosen a name to use to respond to events
	 * @return -- true if the user entered a name, false otherwise
	 */
	private boolean getNewOwnerName(){

		String newName = JOptionPane.showInputDialog("Please enter the name you would like to use in your When2Meet responses");
		if(newName != null){ 
			_progOwner.setName(newName);
			//save program owner
			saveOneItem(_progOwner, _progOwnerID, StoredDataType.ProgramOwner);
			return true;
		}

		return false;
	}

	private String getOwnerName() {
		return _progOwner.getName();
	}

	/**
	 * Create a new event from the program, posting it to the web and returning our representation
	 * @param name -- name of the event to create
	 * @param selectedDates -- dates the event should span
	 * @param startHour -- earliest time for which the event should be scheduled
	 * @param endHour -- latest time at which the event should end
	 * @return -- Event representation of the event created by the web
	 * @throws URLAlreadyExistsException
	 * @throws IOException
	 * @throws InvalidURLException
	 */
	public Event createEvent(String name, ArrayList<DateTime> selectedDates, int startHour, int endHour) throws URLAlreadyExistsException, IOException, InvalidURLException{
		
		DateTime startDay = selectedDates.get(0);
		DateTime startTime = new DateTime (startDay.getYear(), startDay.getMonthOfYear(), startDay.getDayOfMonth(),
				startHour, 0);
		
		DateTime endDay = selectedDates.get(selectedDates.size()-1);
		DateTime endTime;
		if(endHour == 24)
			endTime = new DateTime (endDay.getYear(), endDay.getMonthOfYear(), endDay.getDayOfMonth(),
					23, 59);
		else
			endTime = new DateTime (endDay.getYear(), endDay.getMonthOfYear(), endDay.getDayOfMonth(),
				endHour, 0);
		
		showLoadingLabel("Creating event...");
		
		String URL = _exporter.postNewEvent(name, startTime, endTime);
		
		hideLoadingLabel();
		
		if(URL != null)
			return addEvent(URL);
		else{
			//TODO : deal with return value SaveNewEvent.php not matching
			System.out.println("Whoops...");
			return null;
		}
			
	}

	/**
	 * Post a user's response to the appropriate event
	 * @param eventID -- id of the event to which to post
	 * @param response -- user's response
	 */
	public void submitResponse(String eventID, CalendarSlots response) {
		Event event = _events.get(eventID);	

		boolean didNotCancel = true;
		
		// If this response did not come from an existing response on the web, and was
		// created entirely in our program. Need to get a user name to associate with this response
		if(response.getOwner() == null) {
			if(getOwnerName() == null)
				didNotCancel = getNewOwnerName();
			if(didNotCancel)
				response.setOwner(new When2MeetOwner(getOwnerName(), -1));
		} else if(response.getOwner().getName() == null) {
			if(getOwnerName() == null)
				didNotCancel = getNewOwnerName();
			if(didNotCancel)
				response.getOwner().setName(getOwnerName());
		} 

		if(didNotCancel){

			if(event.getCalGroupType() == CalGroupType.When2MeetEvent) {
				boolean didNotPost = true;
				while(didNotPost){
					try {
						_exporter.postAllAvailability((When2MeetEvent) event);
						didNotPost = false;
					} catch (NameAlreadyExistsException e) {
						//allows the user to cancel submit when asked for a new name to use
						didNotPost = getNewUserName((When2MeetEvent) event, event.getUserResponse().getOwner().getName());
					} catch  (EmptyEventException e){
						//TODO
					}
				}
			} else {
				System.out.println("Tried to submit a response for event type not when2meet");
			}
			
		}
			
		//save to index
		saveOneItem(event, String.valueOf(event.getID()), calGroupTypeToIndexType(event.getCalGroupType()));
	}

	/**
	 * Return a CalendarResponses storing a response for each suggestion for the best time to meet
	 * @param eventID -- ID of event for which to find times
	 * @param duration -- length of the event being schedule
	 */
	public CalendarResponses getBestTimes(String eventID, int duration){
		//TODO make more generic (hard-coding 15, limiting to w2m)

		Event event = _events.get(eventID);
		CalendarResponses toReturn = null;

		if(event != null){
			int minAttendees = (int) (event.getCalendars().size() * ATTENDEE_PERCENTAGE);
			toReturn = _timeFinder.findBestTimes(event, 15, duration, NUM_SUGGESTIONS, minAttendees);
		}

		return toReturn;
	}


	/**
	 * @return a NameIDPair for every event stored
	 */
	public ArrayList<NameIDPair> getNameIDPairs() {
		
		ArrayList<NameIDPair> toReturn = new ArrayList<NameIDPair>();

		for(Event e : _events.values())
			toReturn.add(new NameIDPair(e.getName(), String.valueOf(e.getID())));

				return toReturn;
	}

	
	/**
	 * @return the ids of all events stored
	 */
	public Collection<String> getEventIDs() {
		return _events.keySet();
	}

	/**
	 * Repull the user calendar so that we store all events (from selected calendars) that occur between
	 * start and end
	 */
	public void pullCal(DateTime start, DateTime end){

		_userCal = (UserCal) _userCalImporter.refresh(start, end, _userCal);
		saveOneItem(_userCal, _userCalID, calGroupTypeToIndexType(_userCal.getCalGroupType()));
		saveOneItem(_userCalImporter, _userCalImporterID, _userCalImporterType);

	}
	
	/**
	 * TODO
	 * @param url
	 * @return
	 */
	public static boolean webConnected(URL url) {
		try {
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(3000);  
			conn.setReadTimeout(3000);  
			InputStream in = conn.getInputStream();
			in.close();
		} 
		catch (IOException e) {
			return false;
		}
		
		return true;
	}
	
	/** EXCEPTIONS */ 
	public class URLAlreadyExistsException extends Exception {

	}
}
