package cal_master;

import java.util.HashMap;
import java.util.HashSet;

import org.joda.time.DateTime;

import calendar.CalendarGroup;
import calendar.When2MeetEvent;
import calendar_importers.CalendarsImporter;
import calendar_importers.GCalImporter;
import calendar_importers.When2MeetImporterSlots;

public class Communicator {

	private CalendarsImporter _calImporter = null;
	private CalendarGroup _cal = null;
	private HashSet<String> _w2mEventNames = new HashSet<String>();
	private HashMap<String, When2MeetEvent> _w2mEvents = new HashMap<String, When2MeetEvent>();
	private HashMap<String, When2MeetImporterSlots> _w2mImporters = new HashMap<String, When2MeetImporterSlots>();
	private Converter _converter = new Converter();
	
	
	
	public void setCalImporter(CalendarsImporter importer){
		_calImporter = importer;
	}
	
	public void refresh(){
		//TODO update w2m's and cal by repulling data
		_cal = _calImporter.importFresh();
		for(When2MeetEvent w2me : _w2mEvents.values()){
			When2MeetImporterSlots importer = _w2mImporters.get(w2me.getName());
			w2me = (When2MeetEvent) importer.importFresh();
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
	
	public CalendarGroup getCal(){
		//TODO
		return _cal;
	}
	
	public void pullCal(DateTime start, DateTime end){
		
	}
}
