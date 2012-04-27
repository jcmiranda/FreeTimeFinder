package calendar;

import static gui.GuiConstants.RESPONSE_CONFLICT_SPACING;
import gui.DayPanel;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;

import org.joda.time.DateTime;

public class CalendarResponses implements Calendar {

	private DateTime _startTime;
	private DateTime _endTime;
	private String _name;

	private ArrayList<Response> _responses = new ArrayList<Response>();

	public CalendarResponses(DateTime st, DateTime et, String name) {
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

	public ArrayList<Response> getResponses() {
		return _responses;
	}
	public void addResponse(Response r) {
		_responses.add(r);
	}

	private boolean sameTimeOfDay(DateTime dt1, DateTime dt2) {
		return dt1.getHourOfDay() == dt2.getHourOfDay() &&
				dt1.getMinuteOfHour() == dt2.getMinuteOfHour();
	}

	private DateTime toEndPrevDay(DateTime dt) {
		DateTime ret = dt.minusDays(1);
		ret = ret.plusHours(_endTime.getHourOfDay() - _startTime.getHourOfDay());
		ret = ret.plusMinutes(_endTime.getMinuteOfHour() - _startTime.getMinuteOfHour());
		return ret;
	}
	private DateTime toStartNextDay(DateTime dt) {
		DateTime ret = dt.plusDays(1);
		ret = ret.minusHours(_endTime.getHourOfDay() - _startTime.getHourOfDay());
		ret = ret.minusMinutes(_endTime.getMinuteOfHour() - _startTime.getMinuteOfHour());
		return ret;	
	}

	public CalendarResponses invert(String newName) {
		CalendarResponses ret = new CalendarResponses(_startTime, _endTime, newName);

		DateTime st = _startTime;
		DateTime et = _responses.get(0).getStartTime();
		for(int i = 0; i < _responses.size(); i++) {
			Response thisResponse = _responses.get(i);
			et = thisResponse.getStartTime();
			if(st.compareTo(et) != 0) {
				if(sameTimeOfDay(et, _startTime)) {
					et = toEndPrevDay(et);
				}
				if(et.getDayOfYear() - st.getDayOfYear() != 0) {
					DateTime splitEndFirstDay = st;
					splitEndFirstDay = splitEndFirstDay.plusHours( _endTime.getHourOfDay() - splitEndFirstDay.getHourOfDay());
					splitEndFirstDay = splitEndFirstDay.plusMinutes( _endTime.getMinuteOfHour() - splitEndFirstDay.getMinuteOfHour());

					DateTime splitStartSecondDay = et;
					splitStartSecondDay = splitStartSecondDay.minusHours(splitStartSecondDay.getHourOfDay() - _startTime.getHourOfDay());
					splitStartSecondDay = splitStartSecondDay.minusMinutes(splitStartSecondDay.getMinuteOfHour() - _startTime.getMinuteOfHour());
					ret.addResponse(new Response(st,splitEndFirstDay));
					ret.addResponse(new Response(splitStartSecondDay, et));
				} else {
					ret.addResponse(new Response(st, et));
				}
			}

			st = thisResponse.getEndTime();
			if(sameTimeOfDay(st, _endTime)) {
				st = toStartNextDay(st);
			}

		}

		return ret;
	}

	public void sort() {
		Collections.sort(_responses);
		return;
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
		//System.out.println("CALENDAR IMPL: ");
		System.out.println("Name: " + _name);
		for(Response r : _responses) {
			if(r.getStartTime().getMonthOfYear() == 4 && r.getStartTime().getDayOfMonth() == 27)
				r.print();
		}
//		for(Response r : _responses) {
//			r.print();
//		}
	}

	public void setResponses(ArrayList<Response> responses) {
		_responses = responses;
	}


	public void paint(Graphics2D brush, DayPanel d, int numCals){

		ArrayList<Response> conflictCheck = new ArrayList<Response>();
		for (Response r: getResponses()){
			if (r.getStartTime().year().equals(d.getDay().year())
					&& r.getStartTime().dayOfYear().equals(d.getDay().dayOfYear())){
				r.setIndentation(0);
				conflictCheck.add(r);
			}
		}

		int maxIndent=1;
		
		for (int i=0; i<conflictCheck.size(); i++){
			for (int j=i+1; j<conflictCheck.size(); j++){
				if ((conflictCheck.get(i).getStartTime().isAfter(conflictCheck.get(j).getStartTime()) && conflictCheck.get(i).getStartTime().isBefore(conflictCheck.get(j).getEndTime()))
						|| (conflictCheck.get(j).getStartTime().isAfter(conflictCheck.get(i).getStartTime()) && conflictCheck.get(j).getStartTime().isBefore(conflictCheck.get(i).getEndTime()))
						|| (conflictCheck.get(i).getStartTime().equals(conflictCheck.get(j).getStartTime()))
						|| (conflictCheck.get(i).getEndTime().equals(conflictCheck.get(j).getEndTime()))){
					conflictCheck.get(j).setIndentation(conflictCheck.get(i).getIndentation()+1);
					maxIndent = Math.max(maxIndent, conflictCheck.get(j).getIndentation());
				}
			}
		}

		for (Response r: getResponses()){
			if (r.getStartTime().year().equals(d.getDay().year())
					&& r.getStartTime().dayOfYear().equals(d.getDay().dayOfYear())){
				r.paint(brush,
						d,
						(int) ((double) r.getIndentation()/(maxIndent+1)*d.getWidth()/2),
						(int) ((double) d.getWidth() - d.getWidth()*(maxIndent - r.getIndentation())/(maxIndent+1)/2));
			}
		}
	}


}
