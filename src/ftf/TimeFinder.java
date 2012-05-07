package ftf;

import java.util.ArrayList;
import java.util.PriorityQueue;

import org.joda.time.DateTime;

import calendar.CalendarResponses;
import calendar.CalendarSlots;
import calendar.Event;
import calendar.Response;


public class TimeFinder {

	private int _numSlotsInDay;
	private int _numDays;
	private int _interval;
	private DateTime _start;
	
	
	private DateTime slotToTime(int slotIndex, int day) {
		DateTime ret = _start;
		ret = ret.plusDays(day);
		ret = ret.plusMinutes(_interval * (slotIndex % _numSlotsInDay));
		if(ret.getHourOfDay() == 0 && ret.getMinuteOfHour() == 0)
			ret = ret.minusMinutes(1);
		return ret;
	}
	
	public int toSlot(DateTime dt) {
		int minutesOff = dt.getMinuteOfDay() - _start.getMinuteOfDay();
		return minutesOff / _interval;
	}
	
	
	public CalendarResponses findBestTimes(Event e, int interval, int duration, int numToReturn, int minAttendees){
		
		ArrayList<CalendarSlots> calendars = e.getCalendars();
		if(calendars.size() <= 0){
			return null;
		}
		
		CalendarSlots firstCal = calendars.get(0);
		CalendarSlots userResponse = e.getUserResponse();
		_start = firstCal.getStartTime();
		_numSlotsInDay = firstCal.getSlotsInDay();
		_interval = interval;
		_numDays = firstCal.numDays();
		int[][] freeTimes = new int[_numSlotsInDay][calendars.size() + 1];
		PriorityQueue<TimeAvailability> times = new PriorityQueue<TimeAvailability>();
		
		for(int day=0; day<_numDays; day++){
			int col = 0;
			
			for(CalendarSlots cal : calendars){
				
				for(int row=0; row<_numSlotsInDay; row++){
					if(_numSlotsInDay == cal.getSlotsInDay()){
						if(cal.isVisible()){
							freeTimes[row][col] = cal.getAvail(day, row).getAvailAsInt();
						}
						else{
							freeTimes[row][col] = 0;
						}
					}
				}
				col++;
			}
			if(userResponse != null){
				for(int row=0; row<_numSlotsInDay; row++){
					if(_numSlotsInDay == userResponse.getSlotsInDay()){
						freeTimes[row][col] = userResponse.getAvail(day, row).getUserAvailAsInt();
					}
				}
			}
			
			PriorityQueue<TimeAvailability> temp = calculateTimes(freeTimes, day, interval, duration, minAttendees);
			int size = temp.size();
			int i=0;
			while(i<size){
				times.add(temp.poll());
				i++;
			}
			
		}
		
		CalendarResponses ret = new CalendarResponses(_start, firstCal.getEndTime(), "", "");
		int i=0;
		int num = Math.min(numToReturn, times.size());
		while(i<num){
			TimeAvailability t = times.poll();
			int j = 0;
			DateTime start = slotToTime(t.getTime(), t.getDay());
			while(j<duration/interval){
				j++;
			}
			
			DateTime end = slotToTime(t.getTime()+j, t.getDay());
			ret.addResponse(new Response(start, end));
			i++;
		}
		
		
		return ret;
	}
	
	public PriorityQueue<TimeAvailability> calculateTimes(int[][] times, int day, int interval, int duration, int minAttendees){
		
		int numRows = times.length;
		int numCol = times[0].length;
		int[] result = new int[numRows];
		int span = duration/interval - 1;
		
		//initialize all entries to 0
		for(int i=0; i<result.length; i++){
			result[i] = 0;
		}
		
		for(int row=0; row<numRows; row++){
			for(int col=0; col<numCol; col++){
				result[row] += times[row][col];
			}
		}
		
		for(int i=0; i<numRows; i++){
			if(i+span < numRows){
				for(int j=i+1; j<= i+span; j++){
					result[i] += result[j];
				}
			}
			else{
				result[i] = 0;
			}
		}
		PriorityQueue<TimeAvailability> bestTimes = new PriorityQueue<TimeAvailability>();
		int lastIn = 0;
		for(int curr=0; curr<numRows; curr++){
			if((bestTimes.isEmpty() || result[curr] != result[lastIn]) && result[curr]>=minAttendees*duration/interval){
				bestTimes.add(new TimeAvailability(curr, day, result[curr]));
				
			}
			lastIn = curr;
		}
		
		return bestTimes;
	}
	
	
	
	private class TimeAvailability implements Comparable<TimeAvailability>{
		
		private int _startTime, _day, _numAvailable;
		
		public TimeAvailability(int startTime, int day, int numAvailable){
			_startTime = startTime;
			_day = day;
			_numAvailable = numAvailable;
		}
		
		public int getTime(){
			return _startTime;
		}
		
		public int getDay(){
			return _day;
		}
		
		public int getAttendance(){
			return _numAvailable;
		}
		
		public void print(){
			System.out.println("Day: " + _day + "\tStart Slot: " + _startTime + "\tNum Available: " + _numAvailable);
		}

		@Override
		public int compareTo(TimeAvailability o) {
			if(_numAvailable < o.getAttendance()){
				return 1;
			}
			else if(_numAvailable > o.getAttendance()){
				return -1;
			}
			else{
				return 0;
			}
		}
	}
	
}
	