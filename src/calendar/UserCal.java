package calendar;

import org.joda.time.DateTime;

public class UserCal extends CalendarGroup<CalendarResponses> {

	public UserCal(DateTime start, DateTime end, CalGroupType type) {
		super(start, end, type);
	}
	
	public CalendarResponses getCalById(String id){
		for(CalendarResponses cal: this.getCalendars()){
			if(cal.getId().equals(id))
				return cal;
		}
		return null;
	}

}
