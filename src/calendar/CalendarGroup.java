package calendar;
import java.util.Collection;

public interface CalendarGroup {
	Collection<? extends Calendar> getCalendars();
	void addCalendar(Calendar c);
}
