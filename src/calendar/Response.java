package calendar;
import org.joda.time.DateTime;

public class Response implements Comparable<Response> {
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

	private String timeToString(DateTime t) {
		return (t.getYear()+"-"+t.getMonthOfYear()+"-"+t.getDayOfMonth()+
				" " + t.getHourOfDay() + ":" + t.getMinuteOfHour());
	}
	
	public void print() {
		System.out.println("Start: " + timeToString(_startTime) 
				+ " End: " + timeToString(_endTime));
	}
	
	@Override
	public int compareTo(Response r) {
		return this.getStartTime().compareTo(r.getStartTime());
	}
}
