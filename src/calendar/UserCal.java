package calendar;

import java.util.ArrayList;

import org.joda.time.DateTime;

public class UserCal extends CalendarGroup<CalendarResponses> {

	public UserCal(DateTime start, DateTime end, CalGroupType type) {
		super(start, end, type);
		// TODO Auto-generated constructor stub
	}
	
	public CalendarResponses getCalByName(String name){
		
		for(CalendarResponses cal: this.getCalendars()){
			if(cal.getName().equals(name))
				return cal;
		}
		
		return null;
	}

}
