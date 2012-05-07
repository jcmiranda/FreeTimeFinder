package ftf;

import java.util.ArrayList;
import java.util.PriorityQueue;

import org.joda.time.DateTime;

import calendar.CalendarResponses;
import calendar.CalendarSlots;
import calendar.Event;
import calendar.Response;

/**
 * Class representing algorithm that determines which times would be best for the given event to occur
 *
 */

public class TimeFinder {

	private int _numSlotsInDay;
	private int _numDays;
	private int _interval;
	private DateTime _start;
	
	
	/**
	 * @param slotIndex - row of array
	 * @param day - col of array
	 * @return - time representing the index (slotIndex, day) in a respondee's availability array
	 */
	private DateTime slotToTime(int slotIndex, int day) {
		DateTime ret = _start;
		ret = ret.plusDays(day);
		ret = ret.plusMinutes(_interval * (slotIndex % _numSlotsInDay));
		if(ret.getHourOfDay() == 0 && ret.getMinuteOfHour() == 0)
			ret = ret.minusMinutes(1);
		return ret;
	}
	
	/**
	 * 
	 * @param dt -- date
	 * @return - row of the matrix that corresponds to the given date
	 */
	public int toSlot(DateTime dt) {
		int minutesOff = dt.getMinuteOfDay() - _start.getMinuteOfDay();
		return minutesOff / _interval;
	}
	
	/**
	 * 
	 * @param e - Event for which to find times
	 * @param interval - number of minutes/slot of the availability array
	 * @param duration - length of the event we want to schedule
	 * @param numToReturn - max number of suggestions to give
	 * @param minAttendees - minimum number of people who must attend the event
	 * @return - CalendarResponses representation of suggestions for best times to meet, where each Response represents a different suggestion
	 */
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
		
		// Availability array = number of starting times in a day x number of respondees (including user)
		int[][] freeTimes = new int[_numSlotsInDay][calendars.size() + 1];
		PriorityQueue<TimeAvailability> times = new PriorityQueue<TimeAvailability>();
		
		for(int day=0; day<_numDays; day++){
			
			//each column represents a certain user
			int col = 0;
			
			for(CalendarSlots cal : calendars){
				
				for(int row=0; row<_numSlotsInDay; row++){
					if(_numSlotsInDay == cal.getSlotsInDay()){
						//only want to base times off of selected respondees (i.e. respondees currently visible to the user)
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
			// add user response to the array (in its own column)
			if(userResponse != null){
				for(int row=0; row<_numSlotsInDay; row++){
					if(_numSlotsInDay == userResponse.getSlotsInDay()){
						//user response weighted to give them preference
						freeTimes[row][col] = userResponse.getAvail(day, row).getUserAvailAsInt();
					}
				}
			}
			
			// find the best times for this day, adding them to the list for all days
			PriorityQueue<TimeAvailability> temp = calculateTimes(freeTimes, day, interval, duration, minAttendees);
			int size = temp.size();
			int i=0;
			while(i<size){
				times.add(temp.poll());
				i++;
			}
			
		}
		
		//create a response for each suggestion stored in the list, only pulling out the best n (n == min(numToReturn, numWeHave) )
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
	
	/**
	 * 
	 * @param times -- array of availabilities of all respondees
	 * @param day -- column of each respondee's availability we're currently looking at
	 * @param interval -- num minutes b/w possible start times
	 * @param duration -- length of the event we wish to schedule
	 * @param minAttendees -- min number of people who must be in attendance
	 * @return 
	 */
	public PriorityQueue<TimeAvailability> calculateTimes(int[][] times, int day, int interval, int duration, int minAttendees){
		
		int numRows = times.length;
		int numCol = times[0].length;
		int[] result = new int[numRows];
		int span = duration/interval - 1;
		
		//initialize all entries to 0
		for(int i=0; i<result.length; i++){
			result[i] = 0;
		}
		
		// sum across the row to get total availability in a certain interval-length period
		for(int row=0; row<numRows; row++){
			for(int col=0; col<numCol; col++){
				result[row] += times[row][col];
			}
		}
		
		// sum all the availabilities for the intervals within the duration given a certain start time (given by row i).
		// if there are not enough intervals to sum to duration, then i would be too late a start time in order to end 
		// by the end time of the event, so it should not be considered
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
		// only add things to the queue if (1) they meet the requirement that at least an average minAttendees people can attend for
		// each interval that falls in the duration, and (2) that it does not include the same number of people as the event added just before 
		// (as that would give redundant suggestions)
		for(int curr=0; curr<numRows; curr++){
			if((bestTimes.isEmpty() || result[curr] != result[lastIn]) && result[curr]>=minAttendees*duration/interval){
				bestTimes.add(new TimeAvailability(curr, day, result[curr]));
				
			}
			lastIn = curr;
		}
		
		return bestTimes;
	}
	
	
	
	/**
	 * 
	 * Tuple representing a startTime (row), day (col) and the number of people available at that time
	 *
	 */
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
		// Allows us to sort start times in order of decreasing numAttendees
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
	