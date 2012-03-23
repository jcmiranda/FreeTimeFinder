package calendar;
import java.util.Collection;

public interface CalendarGroup {
	Collection<Calendar> getCalendars();
	void addCalendar(Calendar c);
	void clearCalendars();
}
