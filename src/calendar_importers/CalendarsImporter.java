package calendar_importers;
import calendar.CalendarGroup;

public interface CalendarsImporter {
	CalendarGroup importFresh();
	void updateCalGrp(CalendarGroup cg);
}
