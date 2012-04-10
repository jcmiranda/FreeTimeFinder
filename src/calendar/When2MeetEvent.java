package calendar;

import java.util.ArrayList;
import java.util.Collection;

import org.joda.time.DateTime;

public class When2MeetEvent implements CalendarGroup {
	private DateTime _st;
	private DateTime _et;
	private ArrayList<CalendarSlotsImpl> _cals = new ArrayList<CalendarSlotsImpl>();
	
	public When2MeetEvent(DateTime st, DateTime et) {
		_st = st;
		_et = et;
	}
	
	public DateTime getStartTime(){
		return _st;
	}
	
	public DateTime getEndTime(){
		return _et;
	}
	
	@Override
	public ArrayList<CalendarSlotsImpl> getCalendars() {
		return  _cals;
	}

	@Override
	public void clearCalendars() {
		_cals.clear();
	}

	@Override
	public void addCalendar(CalendarSlotsImpl c) {
		_cals.add(c);
	}

	@Override
	public void addCalendars(ArrayList<CalendarSlotsImpl> cals) {
		// TODO Auto-generated method stub
		
	}

}
