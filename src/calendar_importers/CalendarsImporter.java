package calendar_importers;
import calendar.Calendar;
import calendar.CalendarGroup;

public interface CalendarsImporter {
	CalendarGroup<? extends Calendar> importCalendarGroup();
}
