package calendar;
import org.joda.time.DateTime;

public class Response {
	private DateTime _startTime;
	private DateTime _endTime;
	
	public Response(DateTime st, DateTime et) {
		_startTime = st;
		_endTime = et;
	}
	
	public DateTime getStartTime() {
		// TODO
		return _startTime;
	}
	public DateTime getEndTime() {
		// TODO
		return _endTime;
	}
}
