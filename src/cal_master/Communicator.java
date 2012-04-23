package cal_master;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.joda.time.DateTime;

import calendar.Availability;
import calendar.CalendarGroup;
import calendar.CalendarSlots;
import calendar.When2MeetEvent;
import calendar_exporters.When2MeetExporter;
import calendar_importers.CalendarsImporter;
import calendar_importers.GCalImporter;
import calendar_importers.When2MeetImporter;

public class Communicator {

	private CalendarsImporter _calImporter = null;
	private CalendarGroup _cal = null;
	private HashSet<String> _w2mEventNames = new HashSet<String>();
	private HashMap<String, When2MeetEvent> _w2mEvents = new HashMap<String, When2MeetEvent>();
	private HashMap<String, When2MeetImporter> _w2mImporters = new HashMap<String, When2MeetImporter>();
	private Converter _converter = new Converter();
	private ProgramOwner _owner = new ProgramOwner();
	
	
	public void setCalImporter(CalendarsImporter importer){
		_calImporter = importer;
	}
	
	public void setOwnerName(String name){
		_owner.setName(name);
	}
	
	public void refresh(){
		//TODO update w2m's and cal by repulling data
		_cal = _calImporter.importCalendarGroup();
		for(When2MeetEvent w2me : _w2mEvents.values()){
			When2MeetImporter importer = _w2mImporters.get(w2me.getName());
			w2me = (When2MeetEvent) importer.importCalendarGroup();
		}
	}
	
	public void calToW2M(String eventName){
		//TODO
		When2MeetEvent w2m = _w2mEvents.get(eventName);
		if(_calImporter.getClass() == GCalImporter.class){
			//w2m = _converter.gCalToSlots(_cal, w2m);
		}
	}
	
	public When2MeetEvent getW2M(String name){
		return _w2mEvents.get(name);
	}
	
	public void sendResponse(String eventName, CalendarSlots response, ArrayList<Integer> toBusy, ArrayList<Integer> toFree){
		When2MeetEvent w2m = _w2mEvents.get(eventName);
		if(w2m != null){
			When2MeetExporter exporter = new When2MeetExporter(w2m, response);
			boolean newResponse = true;
			for(CalendarSlots c : w2m.getCalendars()){
				if(c.getOwner().getName().equalsIgnoreCase(response.getOwner().getName()) || c.getOwner().getID() == response.getOwner().getID()){
					newResponse = false;
					break;
				}
			}
			if(newResponse){
				//TODO : "sign in" to event w/ response's owner's name
			}
			
			exporter.updateAvailability(toBusy, Availability.busy);
			exporter.updateAvailability(toFree, Availability.free);
			
			
		}
	}
	
	public CalendarGroup getCal(){
		//TODO
		return _cal;
	}
	
	public void pullCal(DateTime start, DateTime end){
		
	}
}
