package calendar;
import java.util.Collection;

public interface CalendarGroup {
	Collection<CalendarImpl> getCalendars();
	void addCalendar(CalendarImpl c);
	void clearCalendars();
}
