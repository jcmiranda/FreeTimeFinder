package calendar;
import java.util.Collection;
import org.joda.time.DateTime;

public interface Calendar {
	DateTime getStartTime();
	DateTime getEndTime();
	Owner getOwner();
	Collection<Response> getResponses();
}
