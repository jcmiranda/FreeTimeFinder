package calendar;

import java.util.ArrayList;

import org.joda.time.DateTime;

public class GoogleCalendars extends CalendarGroup<CalendarResponses>{
	
	private Owner _owner;
	private ArrayList<CalendarResponses> _cals = new ArrayList<CalendarResponses>();
	
	public GoogleCalendars(DateTime st, DateTime et, Owner o) {
		super(st, et, CalGroupType.GCal);
		_owner = o;
	}
	
	public Owner getOwner(){
		return _owner;
	}

}
