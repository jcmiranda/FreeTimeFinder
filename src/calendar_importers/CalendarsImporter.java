package calendar_importers;
import calendar.Calendar;
import calendar.CalendarGroup;

public interface CalendarsImporter<C extends Calendar> {
	CalendarGroup<C> importCalendarGroup();
}
