package calendar;
import org.joda.time.DateTime;

public class Response implements Comparable<Response> {
	private DateTime _startTime;
	private DateTime _endTime;
	private String _name;
	
	public Response(DateTime st, DateTime et) {
		_startTime = st;
		_endTime = et;
		_name = null;
	}
	
	public Response(DateTime st, DateTime et, String name) {
		_startTime = st;
		_endTime = et;
		_name = name;
	}
	
	public DateTime getStartTime() {
		// TODO
		return _startTime;
	}
	public DateTime getEndTime() {
		// TODO
		return _endTime;
	}
	public String getName() {
		return _name;
	}
	private String timeToString(DateTime t) {
		return (t.getYear()+"-"+t.getMonthOfYear()+"-"+t.getDayOfMonth()+
				" " + t.getHourOfDay() + ":" + t.getMinuteOfHour());
	}
	
	public void print() {
		System.out.println("Start: " + timeToString(_startTime) 
				+ "\tEnd: " + timeToString(_endTime)
				+ "\tName: " + _name);
	}
	
	@Override
	public int compareTo(Response r) {
		return this.getStartTime().compareTo(r.getStartTime());
	}
}
