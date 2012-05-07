package calendar;

import java.util.ArrayList;

import org.joda.time.DateTime;

/**
 * Representation of a Google calendar (a CalendarGroup of CalendarResponses with unique IDs)
 */

public class GoogleCalendars extends UserCal{
	
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
