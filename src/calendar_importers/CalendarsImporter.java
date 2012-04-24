package calendar_importers;
import java.net.MalformedURLException;

import calendar.Calendar;
import calendar.CalendarGroup;

public interface CalendarsImporter<C extends Calendar> {
	CalendarGroup<C> importCalendarGroup(String url) throws MalformedURLException;
}
