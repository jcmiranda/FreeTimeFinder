package calendar;

import java.util.ArrayList;
import java.util.Collection;

import org.joda.time.DateTime;

public class When2MeetEvent implements CalendarGroup {
	private DateTime _st;
	private DateTime _et;
	private ArrayList<CalendarImpl> _cals = new ArrayList<CalendarImpl>();
	
	public When2MeetEvent(DateTime st, DateTime et) {
		_st = st;
		_et = et;
	}
	
	@Override
	public ArrayList<CalendarImpl> getCalendars() {
		return  _cals;
	}

	@Override
	public void clearCalendars() {
		_cals.clear();
	}

	@Override
	public void addCalendar(CalendarImpl c) {
		_cals.add(c);
	}

	@Override
	public void addCalendars(ArrayList<CalendarImpl> cals) {
		// TODO Auto-generated method stub
		
	}

}
