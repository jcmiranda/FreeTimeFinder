package calendar;

import java.util.ArrayList;
import java.util.Collection;
import org.joda.time.DateTime;

public class CalendarGroup<C extends Calendar> {

	protected ArrayList<C> _calendars = new ArrayList<C>();
	protected DateTime _start, _end;
	private CalGroupType _calGroupType = CalGroupType.Unset;
	
	public CalendarGroup(DateTime start, DateTime end, CalGroupType type){
		_start = start;
		_end = end;
		_calGroupType = type;
	}
	
	public CalendarGroup(DateTime start, DateTime end, Collection<C> cals, CalGroupType type){
		this(start, end, type);
		for(C c : cals)
			_calendars.add(c);
	}
	
	public DateTime getStartTime(){
		return _start;
	}
	
	public DateTime getEndTime(){
		return _end;
	}
	
	public ArrayList<C> getCalendars(){
		return _calendars;
	}
	
	public void addCalendar(C calendar){
		_calendars.add(calendar);
	}
	
	public void addCalendars(Collection<C> cals){
		_calendars.addAll(cals);
	}
	
	public void removeCalendar(C calendar){
		_calendars.remove(calendar);
	}
	
	public void removeCalendars(Collection<C> calendars){
		_calendars.removeAll(calendars);
	}
	
	public void clearCalendars(){
		_calendars.clear();
	}
	
	public CalGroupType getCalGroupType() {
		return _calGroupType;
	}
	
}
