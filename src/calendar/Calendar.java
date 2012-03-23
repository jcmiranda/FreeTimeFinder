package calendar;
import java.util.Collection;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public interface Calendar {
	LocalTime getStartTime();
	LocalTime getEndTime();
	LocalDate getStartDate();
	LocalDate getEndDate();
	Owner getOwner();
	Collection<Response> getResponses();
}
