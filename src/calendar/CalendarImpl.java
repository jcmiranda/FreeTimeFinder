package calendar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.joda.time.DateTime;

public class CalendarImpl implements Calendar {

	private DateTime _startTime;
	private DateTime _endTime;
	private String _name;
	private ArrayList<Response> _responses = new ArrayList<Response>();
	
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
	public ArrayList<Response> getResponses() {
		return _responses;
	}
	public void addResponse(Response r) {
		_responses.add(r);
	}
	
	public void flatten() {
		Collections.sort(_responses);
		ArrayList<Response> newResp = new ArrayList<Response>();
		DateTime st = _responses.get(0).getStartTime();
		DateTime et = _responses.get(0).getEndTime();
		for(int i = 1; i < _responses.size(); i++) {
			Response thisResponse = _responses.get(i);
			if(thisResponse.getStartTime().compareTo(et) == 0) {
				et = _responses.get(i).getEndTime();
			} else {
				newResp.add(new Response(st, et));
				st = thisResponse.getStartTime();
				et = thisResponse.getEndTime();
			}
		}
		newResp.add(new Response(st, et));
		_responses = newResp; 
	}
	
	public void print() {
		System.out.println("CALENDAR IMPL: ");
		System.out.println("Name: " + _name);
		for(Response r : _responses) {
			r.print();
		}
	}

}
