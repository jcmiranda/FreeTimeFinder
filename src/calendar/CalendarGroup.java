package calendar;
import java.util.ArrayList;
import java.util.Collection;

public interface CalendarGroup {

	ArrayList<CalendarImpl> getCalendars();
	void addCalendar(CalendarImpl c);
	void addCalendars(ArrayList<CalendarImpl> cals);
	void clearCalendars();
}
