package calendar_importers;
import java.io.IOException;
import java.net.MalformedURLException;

import org.joda.time.DateTime;

import calendar.Calendar;
import calendar.CalendarGroup;
import calendar.UserCal;

public interface CalendarsImporter<C extends Calendar> {
	CalendarGroup<C> importNewEvent(String url) throws MalformedURLException, IOException;
	CalendarGroup<C> refresh(DateTime st, DateTime et);
	CalendarGroup<C> refresh(DateTime st, DateTime et, UserCal calgroup);
}
