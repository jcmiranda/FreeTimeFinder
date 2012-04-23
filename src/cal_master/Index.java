package cal_master;

import java.util.HashMap;

import calendar.CalType;
import calendar.CalendarGroup;
import calendar.CalendarResponses;
import calendar.CalendarSlots;
import calendar.EventType;

public class Index {
	private HashMap<String, EventType> _events = new HashMap<String, EventType>();
	private HashMap<String, CalType> _userCal = new HashMap<String, CalType>();
	
	public void addEvent(CalendarGroup<CalendarSlots> event, EventType type) {
		// Fill in here
		// If event already exists, replace it
		// If event isn't in index, add it
	}
	
	public void setCal(CalendarGroup<CalendarResponses> userCal, CalType type) {
		// Set tracked userCal to be userCal
	}
}
