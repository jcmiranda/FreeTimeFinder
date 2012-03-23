package calendar;

import java.util.ArrayList;
import java.util.Collection;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public class When2MeetEvent implements CalendarGroup {
	private LocalTime _st;
	private LocalTime _et;
	private LocalDate _sd;
	private LocalDate _ed;
	private Collection<? extends Calendar> _cals = new ArrayList<CalendarImpl>();
	
	public When2MeetEvent(LocalTime st, LocalTime et, LocalDate sd, LocalDate ed) {
		_st = st;
		_et = et;
		_sd = sd;
		_ed = ed;
	}
	
	@Override
	public Collection<Calendar> getCalendars() {
		return _cals;
	}

	@Override
	public void addCalendar(Calendar c) {
		_cals.add(c);
	}

	@Override
	public void clearCalendars() {
		_cals.clear();
	}

}
