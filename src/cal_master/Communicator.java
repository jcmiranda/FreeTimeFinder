package cal_master;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.io.BufferedWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import org.joda.time.DateTime;

import com.thoughtworks.xstream.XStream;

import cal_master.Index.IndexType;
import calendar.*;
import calendar_importers.CalendarsImporter;
import calendar_importers.When2MeetImporter;


public class Communicator {

	private CalendarsImporter<CalendarResponses> _userCalImporter = null;
	
	private CalendarGroup<CalendarResponses> _userCal = null;
	private String _userCalID = "userCal", _indexID = "index";
	private HashMap<String, When2MeetEvent> _w2mEvents = new HashMap<String, When2MeetEvent>();
	private When2MeetImporter _importer = new When2MeetImporter();
	// TODO only have a single importer
	// TODO edit 
	//private HashMap<String, When2MeetImporter> _w2mImporters = new HashMap<String, When2MeetImporter>();
	private Converter _converter = new Converter();
	private ProgramOwner _owner = new ProgramOwner();
	private XStream _xstream = new XStream();
	private Index _index = new Index();
	
	
	private void setUpXStream() {
		_xstream.alias("index", Index.class);
		_xstream.alias("indextype", Index.IndexType.class);
		_xstream.alias("calendarslots", CalendarSlots.class);
		_xstream.alias("calendarresponses", CalendarResponses.class);
		_xstream.alias("calendargroup", CalendarGroup.class);
		_xstream.alias("calendar", Calendar.class);
		_xstream.alias("when2meetevent", When2MeetEvent.class);
		_xstream.alias("when2meetowner", When2MeetOwner.class);
		_xstream.alias("avail", Availability.class);
	}
	
	public void startUp() {
		
		setUpXStream();
		// If have an index, recreate index
		File indexFile = new File(_indexID+".xml");
		if(indexFile.exists())
			_index = (Index) _xstream.fromXML(indexFile);
		
		// Pull in XML and create when2meet events from XML
		// Pull in XML for user cal, and create user cal
		for(String id : _index.getFiles()) {
			IndexType type = _index.getType(id);
			Object o = _xstream.fromXML(new File(id + ".xml"));
			switch(type) {
			case When2MeetEvent: {
				_w2mEvents.put(id,  (When2MeetEvent) o);
				break;
			}
			case GCal: {
				if(_userCal != null)
					System.out.println("Two google calendars were recreated from index");
				_userCal = (GoogleCalendars) o;
				break;
			}
			case ProgramOwner: {
				_owner = (ProgramOwner) o;
			}
			default: {
				System.out.println("Default condition triggered on recreating index.");
			}
			}
				
		}
		
		if(_userCal == null) {
			// TODO add a never option
			Object[] options = {"Yes", "Not now"};
			int n = JOptionPane.showOptionDialog(null, "Would you like to import your calendar?",
					"", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
					options, options[0]);
			if(n == 0) {
				// TODO import the users calendar
				JOptionPane.showMessageDialog(null, "Now I should import a calendar");
			}
		}
		
		// TODO
		// Refresh when2meet events
		// Refresh calendars -> if no calendar, pull in users calendar	
		refresh();
			
	}
	
	/** SAVING **/
	
	public void updateIndex() {
		// Rebuilds the index given the current list of events and calendar
		for(String id : _w2mEvents.keySet())
			_index.addItem(id, IndexType.When2MeetEvent);
		_index.addItem(_userCalID, IndexType.GCal);

		writeToFile(_indexID, _index);
	}
	
	private void writeToFile(String filename, Object o) {
		Writer out = null;
		try {
			out = new BufferedWriter(new FileWriter(filename+".xml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		_xstream.toXML(o, out);	
	}
	
	// Saves one item
	public void saveOneItem(Object o, String id) {
		// Check index to see if it exists
		updateIndex();
		writeToFile(id, o);
	}
	
	public void saveAll() {
		// Store some form of an update index
		updateIndex();
		
		// Store XML for when2meet events
		for(String id : _w2mEvents.keySet())
			writeToFile(id, _w2mEvents.get(id));
		
		// Store XML for calendar
		writeToFile(_userCalID, _userCal);
	}
	
	public class URLAlreadyExistsException extends Exception {
		
	}
	
	public void addWhen2Meet(String url) throws URLAlreadyExistsException, MalformedURLException {
		// Check if we have this url already
		// If we do, throw an error
		for(When2MeetEvent w2me : _w2mEvents.values())
			if(w2me.getURL().equals(url)) {
				System.out.println("URL already found");
				throw new URLAlreadyExistsException();
			}

		When2MeetEvent newEvent = _importer.importCalendarGroup(url);
		String id = newEvent.getID() + "";
		_w2mEvents.put(id, newEvent);
		saveOneItem(newEvent, id);

		// If we don't, check that it's a valid url
		// If it's a valid url, pull in that when2meet using an importer
		
		// If it's not a valid url, error message to user
	}
	
	public void removeWhen2Meet(String eventID) {
		// Check that we do have an event with this ID
		// If we do, remove it form our list of events
		// Save this when2meet, and our new index
		
		// If we didn't have it, huh? confused, how did this happen
	}
	
	public void setCalImporter(CalendarsImporter<CalendarResponses> importer){
		_userCalImporter = importer;
	}
	
	public void setOwnerName(String name){
		_owner.setName(name);
	}
	
	public void refresh(){
		// Update all when2meet events
		// Update user calendar
		// Rebuild index and store all files (saveall)
	}
	
	public void calToW2M(String eventID){
		
	}
	
	public When2MeetEvent getW2M(String id){
		return _w2mEvents.get(id);
	}
	
	public void submitResponse(String eventID, CalendarSlots response){
		When2MeetEvent w2m = _w2mEvents.get(eventID);
//		if(w2m != null){
//			When2MeetExporter exporter = new When2MeetExporter(w2m);
//			boolean newResponse = true;
//			for(CalendarSlots c : w2m.getCalendars()){
//				if(c.getOwner().getName().equalsIgnoreCase(response.getOwner().getName()) || c.getOwner().getID() == response.getOwner().getID()){
//					exporter.postAllAvailability(response);
//					newResponse = false;
//					break;
//				}
//			}
//			if(newResponse) {
//				try {
//					exporter.createNewUserNoPassword(response);
//				} catch (NameAlreadyExistsException e) {
//					e.printStackTrace();
//				}
//			}
//			
//		}
	}
	
	
	public ArrayList<NameIDPair> getNameIDPairs() {
		// Return the list of name ID pairs associated with this event
		return null;
	}
	
	public CalendarGroup<CalendarResponses> getCal(){
		//TODO
		return _userCal;
	}
	
	public void pullCal(DateTime start, DateTime end){
		
	}
}
