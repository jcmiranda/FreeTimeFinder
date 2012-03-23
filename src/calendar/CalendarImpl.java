package calendar;

import java.util.Collection;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public class CalendarImpl implements Calendar {

	private LocalTime _startTime;
	private LocalTime _endTime;
	private LocalDate _startDate;
	private LocalDate _endDate;
	private String _name;
	
	public CalendarImpl(LocalTime st, LocalTime et, LocalDate sd, LocalDate ed, String name) {
		_startTime = st;
		_endTime = et;
		_startDate = sd;
		_endDate = ed;
		_name = name;
	}
	
	public LocalTime getStartTime() {
		return _startTime;
	}
	public LocalTime getEndTime() {
		return _endTime;
	}
	public LocalDate getStartDate() {
		return _startDate;
	}
	public LocalDate getEndDate() {
		return _endDate;
	}
	public String getName() {
		return _name;
	}
	public Owner getOwner() {
		return null;
	}
	public Collection<Response> getResponses() {
		return null;
	}

}
