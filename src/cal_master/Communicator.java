package cal_master;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.joda.time.DateTime;

import calendar.Availability;
import calendar.CalendarGroup;
import calendar.CalendarResponses;
import calendar.CalendarSlots;
import calendar.When2MeetEvent;
import calendar_exporters.When2MeetExporter;
import calendar_exporters.When2MeetExporter.NameAlreadyExistsException;
import calendar_importers.CalendarsImporter;
import calendar_importers.GCalImporter;
import calendar_importers.When2MeetImporter;

public class Communicator {

	private CalendarsImporter<CalendarResponses> _userCalImporter = null;
	
	private CalendarGroup<CalendarResponses> _userCal = null;
	private HashMap<String, When2MeetEvent> _w2mEvents = new HashMap<String, When2MeetEvent>();
	// TODO only have a single importer
	// TODO edit 
	//private HashMap<String, When2MeetImporter> _w2mImporters = new HashMap<String, When2MeetImporter>();
	private Converter _converter = new Converter();
	private ProgramOwner _owner = new ProgramOwner();
	
	public void startUp() {
		// If have an index
		
		// Pull in index -> list of event IDs with extra letters, 
		// information for users calendar
		// Pull in XML and create when2meet events from XML
		// Pull in XML for user cal, and create user cal
		
		// Refresh when2meet events
		// Refresh calendars -> if no calendar, pull in users calendar	
	}
	
	/** SAVING **/
	
	public void rebuildIndex() {
		// Rebuilds the index given the current list of events and calendar
	}
	
	// Saves a when2meet
	public void saveWhen2Meet(When2MeetEvent w2m) {
		// Check index to see if it exists
		// If it does, save over existing
		// If it does not, create new object and update index
	}
	
	// Saves a users calendar
	public void saveUserCal() {
		// Saves updated index and saves user cal
	}
	
	public void saveAll() {
		// Store XML for when2meet events
		// Store XML for calendar
		// Store some form of an update index
	}
	
	public void addWhen2Meet(String url) {
		// Check if we have this url already
		// If we do, throw an error
		
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
