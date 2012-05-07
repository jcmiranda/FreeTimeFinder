package calendar;

import org.joda.time.DateTime;

/**
 * 
 * Superclass for all user calendars (which are all CalendarGroups of CalendarResponses)
 *
 */

public class UserCal extends CalendarGroup<CalendarResponses> {

	public UserCal(DateTime start, DateTime end, CalGroupType type) {
		super(start, end, type);
	}
	
	// each CalendarResponses has a unique ID returned by importer
	public CalendarResponses getCalById(String id){
		for(CalendarResponses cal: this.getCalendars()){
			if(cal.getId().equals(id))
				return cal;
		}
		return null;
	}

}
