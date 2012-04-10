package cal_master;

import java.util.HashMap;
import java.util.HashSet;

import calendar.CalendarGroup;
import calendar.When2MeetEvent;
import calendar_importers.CalendarsImporter;

public class Communicator {

	private CalendarsImporter _calImporter;
	private CalendarGroup _cal;
	private HashSet<String> _w2mEventNames;
	private HashMap<String, When2MeetEvent> _w2mEvents;
	
	public void refresh(){
		//TODO update w2m's and cal by repulling data
	}
	
	public void calToW2M(String eventName){
		//TODO
	}
	
	public When2MeetEvent getW2M(String name){
		//TODO
		return null;
	}
	
	public CalendarGroup getCal(){
		//TODO
		return null;
	}
}
