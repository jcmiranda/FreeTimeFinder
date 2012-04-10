package calendar;

import java.util.ArrayList;
import java.util.Collection;

import org.joda.time.DateTime;

public class GoogleCalendars {
	private DateTime _st;
	private DateTime _et;
	private Owner _owner;
	private ArrayList<CalendarImpl> _cals = new ArrayList<CalendarImpl>();
	
	public GoogleCalendars(DateTime st, DateTime et) {
		_st = st;
		_et = et;
	}
	
	public ArrayList<CalendarImpl> getCalendars() {
		return  _cals;
	}

	public void clearCalendars() {
		_cals.clear();
	}

	public void addCalendar(CalendarImpl c) {
		_cals.add(c);
	}

	public DateTime getStartTime() {
		return _st;
	}
	
	public DateTime getEndTime() {
		return _et;
	}
	
	public Owner getOwner(){
		return _owner;
	}

	public void addCalendar(CalendarSlotsImpl c) {
		// TODO Auto-generated method stub
		
	}

	public void addCalendars(ArrayList<CalendarSlotsImpl> cals) {
		// TODO Auto-generated method stub
		
	}
}
