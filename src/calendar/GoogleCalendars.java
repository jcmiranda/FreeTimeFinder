package calendar;

import java.util.ArrayList;
import java.util.Collection;

import org.joda.time.DateTime;

public class GoogleCalendars extends CalendarGrp<CalendarImpl>{
	
	private Owner _owner;
	private ArrayList<CalendarImpl> _cals = new ArrayList<CalendarImpl>();
	
	public GoogleCalendars(DateTime st, DateTime et, Owner o) {
		super(st, et);
		_owner = o;
	}
	
	public Owner getOwner(){
		return _owner;
	}

}
