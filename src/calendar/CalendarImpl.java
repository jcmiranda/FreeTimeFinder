package calendar;

import java.util.ArrayList;
import java.util.Collection;

import org.joda.time.DateTime;

public class CalendarImpl implements Calendar {

	private DateTime _startTime;
	private DateTime _endTime;
	private String _name;
	private ArrayList<Response> _responses;
	
	public CalendarImpl(DateTime st, DateTime et, String name) {
		_startTime = st;
		_endTime = et;
		_name = name;
	}
	
	public DateTime getStartTime() {
		return _startTime;
	}
	public DateTime getEndTime() {
		return _endTime;
	}

	public String getName() {
		return _name;
	}
	public Owner getOwner() {
		return null;
	}
	public Collection<Response> getResponses() {
		return null;
	}
	
	public void setResponses(ArrayList<Response> responses) {
		_responses = responses;
	}

}
