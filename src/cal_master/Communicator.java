package cal_master;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.joda.time.DateTime;

import calendar.Availability;
import calendar.CalGroupType;
import calendar.Calendar;
import calendar.CalendarGroup;
import calendar.CalendarResponses;
import calendar.CalendarSlots;
import calendar.Event;
import calendar.Event.CalByThatNameNotFoundException;
import calendar.EventUpdate;
import calendar.GoogleCalendars;
import calendar.UserCal;
import calendar.When2MeetEvent;
import calendar.When2MeetOwner;
import calendar_exporters.When2MeetExporter;
import calendar_exporters.When2MeetExporter.NameAlreadyExistsException;
import calendar_importers.CalendarsImporter;
import calendar_importers.EventImporter;
import calendar_importers.EventImporter.InvalidURLException;
import calendar_importers.GCalImporter;

import com.google.gdata.util.ServiceException;
import com.thoughtworks.xstream.XStream;

import ftf.TimeFinderSlots;


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

	private JFrame _loadingFrame;
	private JLabel _loadingLabel;
	private JPanel _loadingPanel;

	private XStream _xstream = new XStream();

	private static final double ATTENDEE_PERCENTAGE = 0;
	private static final int NUM_SUGGESTIONS = 5;


	public Communicator() {

		_loadingLabel = new JLabel();
		_loadingFrame = new JFrame();
		_loadingFrame.setSize(150, 50);
		//_loadingPanel = new JPanel();
		//_loadingPanel.add(_loadingLabel);
		_loadingFrame.add(_loadingLabel);
		_loadingFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}
	
	/**
	 * Display loading message to the user while refreshing/starting the program
	 * 
	 * @param msg -- message to display to the user
	 */
	private void showLoadingLabel(String msg){
		
		System.out.println("MESSAGE: " + msg);
		//_loadingLabel.setText("");
		_loadingLabel.setText(msg);
//		_loadingLabel.revalidate();
		//_loadingFrame.invalidate();
//		_loadingFrame.validate();
		//_loadingFrame.pack();
		//_loadingFrame.remove(_loadingLabel);
		_loadingFrame.add(_loadingLabel);
		_loadingFrame.validate();
		
		
		_loadingFrame.setLocationRelativeTo(null);
		_loadingFrame.setVisible(true);
		
		System.out.println("MESSAGE MAGIC: " + _loadingLabel.getText());
	}


	private void hideLoadingLabel(){
		_loadingFrame.setVisible(false);
	}

	private void setUpXStream() {
		// TODO add Kelly's gcal and date time classes
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
	
	
	public Index recreateIndex() {
		File indexFile = new File(_indexID+".xml");
		if(indexFile.exists())
			return (Index) _xstream.fromXML(indexFile);
		else
			return new Index();
	}
	
	/**
	 * Initializes program by pulling in all saved data and creating objects from it
	 */
	public void startUp() {

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
			// TODO add a never option
			Object[] options = {"Yes", "Not now"};
			int n = JOptionPane.showOptionDialog(null, "Would you like to import your calendar?",
					"", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
					options, options[0]);
			if(n == 0) {

				// from where would you like to import?
				Object[] calOptions = {"Google Calendar" };
				Object selectedValue = JOptionPane.showInputDialog(null, "Choose a calendar type to import.", "", 
						JOptionPane.INFORMATION_MESSAGE, null, calOptions, calOptions[0]);

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
				
		// Refresh when2meet events and calendars

		refresh();

	}
	
	/**
	 * Returns whether there are any events stored in the program
	 */
	public boolean hasEvent() {
		return _events != null && _events.size() > 0;
	}
	
	/**
	 * 
	 * @return whether the user has chosen to store their calendar in the program
	 */
	public boolean hasUserCal() {
		return _userCal != null;
	}

	public CalendarGroup<CalendarSlots> getFirstEvent() {
		for(CalendarGroup<CalendarSlots> cal : _events.values())
			return cal;
				return null;
	}

	public String getFirstEventID() {
		for(String id : _events.keySet())
			return id;
				return null;
	}

	public UserCal getUserCal() {
		return _userCal;
	}
	
	public void setSelectedInUserCal(String calRespName, boolean selected){
		if(_userCal != null){
			ArrayList<CalendarResponses> cals = _userCal.getCalendars();
			for(CalendarResponses calResp : cals){
				if(calResp.getName().equals(calRespName)){
					// TODO 
					calResp.setSelected(selected);
				}
			}
		}
	}

	/** SAVING **/

	/*
	public void updateIndex() {
		// Rebuilds the index given the current list of events and calendar
		for(String id : _events.keySet())
			_index.addItem(id, IndexType.When2MeetEvent);
		_index.addItem(_userCalID, IndexType.GCal);
		_index.addItem(_importerID, _userCalImporterType);
		writeToFile(_indexID, _index);
	}
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

	private void saveIndex(Index temp) {
		writeToFile(_indexID, temp);
	}

	// Saves one item
	public void saveOneItem(Object o, String id, StoredDataType type) {
		// Check index to see if it exists
		Index temp = recreateIndex();
		temp.addItem(id, type);
		saveIndex(temp);
		//writeToFile(_indexID,temp);
		// updateIndex();
		writeToFile(id, o);
	}

	public void removeOneItem(String id) {
		Index temp = recreateIndex();
		temp.removeItem(id);
		saveIndex(temp);
	}

	/*
	public void saveAll() {
		// Store some form of an update index
		updateIndex();

		// Store XML for when2meet events
		for(String id : _events.keySet()) {
			writeToFile(id, _events.get(id));
		}

		// Store XML for calendar
		writeToFile(_userCalID, _userCal);

	}*/

	public class URLAlreadyExistsException extends Exception {

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
		// If we do, throw an error
		for(Event event : _events.values())
			if(event.getURL().equals(url)) {
				System.out.println("URL already found");
				throw new URLAlreadyExistsException();
			}

		// If we don't, check that it's a valid url
		// If it's a valid url, pull in that when2meet using an importer
		// If it's not a valid url, error message to user

		Event newEvent;
		newEvent = _eventImporter.importNewEvent(url);

		// add event to the xml for storage
		StoredDataType eventType = calGroupTypeToIndexType(newEvent.getCalGroupType());
		String id = newEvent.getID() + "";
		_events.put(id, newEvent);
		saveOneItem(newEvent, id, eventType);

		return newEvent;
	}

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

	public void removeWhen2Meet(String eventID) {
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

	public void refresh() {
		//TODO: deal with URL exception
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

	public void calToW2M(String eventID){
		Event w2m = _events.get(eventID);

		if(w2m != null && !w2m.userHasSubmitted()){
			CalendarSlots cal = _converter.calToSlots(_userCal, w2m);
			w2m.setUserResponse(cal);
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
	
	
	public Event getW2M(String id){
		Event toReturn = _events.get(id);
		
		//if we don't have a stored response for the user
		if(toReturn.getUserResponse() == null && !toReturn.getCalendars().isEmpty()){

			//ask user if they've responded to the event
			int resp = JOptionPane.showConfirmDialog(null, "Have you already responded to this When2Meet?", "", JOptionPane.YES_NO_OPTION);

			//if they have, ask them to select their response from the list of all responses
			if(resp == JOptionPane.YES_OPTION){

				Object[] responseNames = new Object[toReturn.getCalendars().size()];

				for(int i=0; i< toReturn.getCalendars().size(); i++){
					responseNames[i] = toReturn.getCalendars().get(i).getOwner().getName();
				}

				Object selected = JOptionPane.showInputDialog(null, "Please select the name that represents your response from the list below",
						"", JOptionPane.INFORMATION_MESSAGE, null,
						responseNames, responseNames[0]);

				//take the selected cal, remove it from the list, and set it to be the userResponse
				if(selected != null){
					CalendarSlots user = null;
					try {
						user = toReturn.getCalByName(selected.toString());
					} catch (CalByThatNameNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					assert user.getOwner() != null;
					toReturn.setUserResponse(user);
					toReturn.setUserSubmitted(true);
					saveOneItem(toReturn, toReturn.getID()+"", calGroupTypeToIndexType(toReturn.getCalGroupType()));
					// ASDF
				}
			}
			//if they haven't, set userResponse to be a new CalendarSlots with them as the owner
			else{
				toReturn.setUserResponse(new CalendarSlots(toReturn.getStartTime(), toReturn.getEndTime(), 15, Availability.free));
				toReturn.getUserResponse().setOwner(new When2MeetOwner(_progOwner.getName(), -1));
				saveOneItem(toReturn, toReturn.getID()+"", calGroupTypeToIndexType(toReturn.getCalGroupType()));
			}

		}

		checkUserCal(id);

		if(!toReturn.userHasSubmitted() && _userCal != null){
			calToW2M(id);
		}

		//save user response
		saveOneItem(toReturn, String.valueOf(toReturn.getID()), calGroupTypeToIndexType(toReturn.getCalGroupType())); 

		return toReturn;
	}

	private void getNewUserName(When2MeetEvent event, String currName){
		String newName = JOptionPane.showInputDialog("The name '" + currName + "' has already been used. Please enter another name.");
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
	}

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
		
		//When2MeetEvent newEvent = new When2MeetEvent(startTime, endTime, name, -1, null, null, null);
		
		String URL = _exporter.postNewEvent(name, startTime, endTime);
		if(URL != null)
			return addEvent(URL);
		else{
			//TODO : deal with return value SaveNewEvent.php not matching
			System.out.println("Whoops...");
			return null;
		}
			
	}

	public void submitResponse(String eventID, CalendarSlots response) {
		Event event = _events.get(eventID);	

		// If this response did not come from an existing response on the web, and was
		// created entirely in our program. Need to get a user name to associate with this response
		if(response.getOwner() == null) {
			if(getOwnerName() == null)
				getNewOwnerName();
			response.setOwner(new When2MeetOwner(getOwnerName(), -1));
		} else if(response.getOwner().getName() == null) {
			if(getOwnerName() == null)
				getNewOwnerName();
			response.getOwner().setName(getOwnerName());
		} 
		
		if(event.getCalGroupType() == CalGroupType.When2MeetEvent) {
			boolean didNotPost = true;
			while(didNotPost){
				try {
					_exporter.postAllAvailability((When2MeetEvent) event);
					didNotPost = false;
				} catch (NameAlreadyExistsException e) {
					getNewUserName((When2MeetEvent) event, event.getUserResponse().getOwner().getName());
				}
			}
		} else {
			System.out.println("Tried to submit a response for event type not when2meet");
		}

		//save to index
		saveOneItem(event, String.valueOf(event.getID()), calGroupTypeToIndexType(event.getCalGroupType()));
	}

	public CalendarResponses getBestTimes(String eventID, int duration){
		//TODO make more generic (hard-coding 15, limiting to w2m)

		Event w2m = _events.get(eventID);
		CalendarResponses toReturn = null;

		if(w2m != null){
			int minAttendees = (int) (w2m.getCalendars().size() * ATTENDEE_PERCENTAGE);
			toReturn = _timeFinder.findBestTimes(w2m, 15, duration, NUM_SUGGESTIONS, minAttendees);
		}

		return toReturn;
	}


	public ArrayList<NameIDPair> getNameIDPairs() {
		// Return the list of name ID pairs associated with all events
		ArrayList<NameIDPair> toReturn = new ArrayList<NameIDPair>();

		for(Event e : _events.values())
			toReturn.add(new NameIDPair(e.getName(), String.valueOf(e.getID())));

				return toReturn;
	}

	public Collection<String> getEventIDs() {
		return _events.keySet();
	}

	public void pullCal(DateTime start, DateTime end){

		_userCal = (UserCal) _userCalImporter.refresh(start, end, _userCal);
		//_userCal = _userCalImporter.refresh(start, end);
		saveOneItem(_userCal, _userCalID, calGroupTypeToIndexType(_userCal.getCalGroupType()));
		saveOneItem(_userCalImporter, _userCalImporterID, _userCalImporterType);

	}
}
