package calendar;
import java.util.Date;

public interface Calendar {
	Date getStartTime();
	Date getEndTime();
	Owner getOwner();
}
