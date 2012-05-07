package calendar;

import org.joda.time.DateTime;

/**
 * Interface representing a general representation of a Calendar (i.e. something with a start and end time that specify time/date range)
 *
 */

public interface Calendar {
	DateTime getStartTime();
	DateTime getEndTime();
}
