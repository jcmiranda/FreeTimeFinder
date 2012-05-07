package calendar;

import java.util.ArrayList;
import java.util.Collection;

import org.joda.time.DateTime;

/**
 * Represents a collection of calendars, grouped by some commonality (from same usercal, same event)
 *
 * @param <C>
 */

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
		if(cals != null)
			for(C c : cals)
				_calendars.add(c);
	}
	
	public DateTime getStartTime(){
		return _start;
	}
	
	public DateTime getEndTime(){
		return _end;
	}
	
	
	private int lenDayInMinutes() {
		// midnight defaults to 0, but if the end time it should be the last minute of the day, not the first (for positive results after subtraction)
		if(_end.getMinuteOfDay() == 0)
			return 24*60 - _start.getMinuteOfDay();
		else
			return _end.getMinuteOfDay() - _start.getMinuteOfDay();
	}
	
	public int getNumHours(){
		return (lenDayInMinutes() + 1) / 60;
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
	
	public void resetCalendars(Collection<C> calendars) {
		clearCalendars();
		addCalendars(calendars);
	}
	
	public void removeCalendars(Collection<C> calendars){
		_calendars.removeAll(calendars);
	}
	
	public void clearCalendars(){
		_calendars.clear();
	}
	
	/**
	 * @return the type of calendar group this is based on what type of calendars it's storing
	 */
	public CalGroupType getCalGroupType() {
		return _calGroupType;
	}
}
