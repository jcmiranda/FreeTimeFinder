package ftf;
import java.util.ArrayList;
import java.util.PriorityQueue;

import org.joda.time.DateTime;

import calendar.CalendarGroup;
import calendar.CalendarResponses;
import calendar.CalendarGroup;
import calendar.Response;

public class TimeFinder {
	private int _numSlotsInDay;
	private int _interval;
	private DateTime _calStart;
	
	public int dayLen(DateTime start, DateTime end){
		return end.getMinuteOfDay() - start.getMinuteOfDay() + 1;
	}
	
	public int numIntervalsInDay(DateTime start, DateTime end, int interval){
		return this.dayLen(start, end)/interval;
	}
	
	private DateTime slotToTime(int slotIndex) {
		DateTime ret = _calStart;
		ret = ret.plusDays(slotIndex / _numSlotsInDay);
		ret = ret.plusMinutes(_interval * (slotIndex % _numSlotsInDay));
		if(ret.getHourOfDay() == 0 && ret.getMinuteOfHour() == 0)
			ret = ret.minusMinutes(1);
		return ret;
	}
	
	public int toSlot(DateTime dt) {
		int daysOff = dt.getDayOfYear() - _calStart.getDayOfYear();
		int minutesOff = dt.getMinuteOfDay() - _calStart.getMinuteOfDay();
		return daysOff * _numSlotsInDay + minutesOff / _interval;
	}

	public Response[] findBestTimes(CalendarGroup<CalendarResponses> e, int interval, int duration, int numToReturn, int minAttendees){
		
		ArrayList<CalendarResponses> calendars = e.getCalendars();
		if(calendars.size() <= 0){
			return null;
		}
		
		CalendarResponses firstCal = calendars.get(0);
		int dayLen = dayLen(firstCal.getStartTime(), firstCal.getEndTime());
		_numSlotsInDay = dayLen / interval;
		_interval = interval;
		System.out.println("dayLen: " + dayLen);
		System.out.println("interval: " + interval);
		int m = (firstCal.getEndTime().getDayOfYear() - firstCal.getStartTime().getDayOfYear() + 1)*_numSlotsInDay;
		int n = calendars.size();
		int[][] freeTimes = new int[m][n];
		for(int r=0; r<m; r++){
			for(int c=0; c<n; c++){
				freeTimes[r][c] = 1;
			}
		}
		
		int col=0, row=0;
		for(CalendarResponses cal : calendars){
			ArrayList<Response> responses = (ArrayList<Response>) cal.getResponses();
			_calStart = cal.getStartTime();
			for(Response r : responses){
				// System.out.println(r.getStartTime().hourOfDay().getAsText() + ":" + r.getStartTime().minuteOfHour().getAsText() + ", " + cal.getEndTime().hourOfDay().getAsText() + ":" + cal.getEndTime().minuteOfHour().getAsText());
				row = toSlot(r.getStartTime());
				int end = toSlot(r.getEndTime());
				/*row = (r.getStartTime().getDayOfYear() - cal.getStartTime().getDayOfYear())*dayLen/interval 
						+ (r.getStartTime().getMinuteOfDay() - cal.getStartTime().getMinuteOfDay())/interval;
				int end = (r.getEndTime().getDayOfYear() - cal.getStartTime().getDayOfYear())*dayLen/interval 
						+ (r.getEndTime().getMinuteOfDay() - cal.getStartTime().getMinuteOfDay())/interval;
				*/
				while(row < end){
					freeTimes[row][col] = 0;
					row++;
				}
			}
			col++;
		}
		
		/*
		
		// Print out free / busy array
		System.out.println(calendars.get(0).getName());
		for(int slotInDay = 0; slotInDay < _numSlotsInDay; slotInDay++) {
			if(slotInDay % 4 == 0)
				System.out.println("========");
			for(int day = 0; day < firstCal.getEndTime().getDayOfYear() - firstCal.getStartTime().getDayOfYear()+1; day++) {
				System.out.print(freeTimes[day*_numSlotsInDay+slotInDay][0] + " ");
			}
			System.out.println();
		}
		*/
		
		
		PriorityQueue<TimeAvailabilityPair> times = calculateTimes(freeTimes, interval, duration, minAttendees);
	
		int i=0, num;
		num = Math.min(times.size(), numToReturn);
		System.out.println("Num: " + num);
		Response[] toReturn = new Response[num];
		while(i<num){
			TimeAvailabilityPair t = times.poll();
			DateTime startTime = slotToTime(t.getTime());
			DateTime endTime = startTime.plusMinutes(duration);
			toReturn[i] = new Response(startTime, endTime);
			// toReturn[i] = new Response(calendars.get(0).getStartTime().plusDays(startDay).plusMinutes(startTime*interval), calendars.get(0).getStartTime().plusDays(startDay).plusMinutes(startTime*interval+duration));
			i++;
		}
		
		return toReturn;
	}
	
	public PriorityQueue<TimeAvailabilityPair> calculateTimes(int[][] times, int interval, int duration, int minAttendees){
		
		int numRows = times.length;
		int numCol = times[0].length;
		System.out.println("Num Col:" + numCol);
		int[] result = new int[numRows];
		System.out.println("Times Length: " + times.length);
		int span = duration/interval - 1;
		//initialize all entries to 0
		for(int i=0; i<result.length; i++){
			result[i] = 0;
		}
		
		System.out.println("FIRST:");
		for(int row=0; row<numRows; row++){
			for(int col=0; col<numCol; col++){
				result[row] += times[row][col];
			}
			// System.out.println(row + ": " + result[row]);
		}
		System.out.println("SECOND:");
		for(int i=0; i<numRows; i++){
			if(i+span < numRows){
				for(int j=i+1; j<= i+span; j++){
					result[i] += result[j];
				}
			}
			else{
				result[i] = 0;
			}
			// System.out.println(i + ": " + result[i]);
		}
		PriorityQueue<TimeAvailabilityPair> bestTimes = new PriorityQueue<TimeAvailabilityPair>();
		int lastIn = 0;
		for(int curr=0; curr<numRows; curr++){
			if((bestTimes.isEmpty() || result[curr] != result[lastIn]) && result[curr]>=minAttendees*duration/interval){
				bestTimes.add(new TimeAvailabilityPair(curr, result[curr]));
				
			}
			lastIn = curr;
		}
		
		return bestTimes;
	}
	
	private class TimeAvailabilityPair implements Comparable<TimeAvailabilityPair>{
		
		private int _startTime, _numAvailable;
		
		public TimeAvailabilityPair(int startTime, int numAvailable){
			_startTime = startTime;
			_numAvailable = numAvailable;
		}
		
		public int getTime(){
			return _startTime;
		}
		
		public int getAttendance(){
			return _numAvailable;
		}

		@Override
		public int compareTo(TimeAvailabilityPair o) {
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
	
	public static void main(String[] args) {
		TimeFinder tF = new TimeFinder();
		int[][] times = {
				{0,0,1,1},
				{0,1,1,1},
				{1,0,0,1},
				{1,1,1,1},
				{0,1,1,1}
		};
		PriorityQueue<TimeAvailabilityPair> q = tF.calculateTimes(times, 15, 30, 1);
		System.out.println("QUEUE 1:");
		int size = q.size();
		for(int i=0; i<size; i++){
			TimeAvailabilityPair t = q.poll();
			System.out.println(t.getTime() + ", " + t.getAttendance());
		}
		
		int[][] times2 = {
				{0,1,1,1,0,1,1,0,0,0,0},
				{1,0,0,1,0,1,0,1,1,0,0},
				{1,1,1,1,1,1,0,0,0,0,0},
				{1,0,0,0,0,0,0,0,0,0,0},
				{1,1,1,1,1,1,1,1,1,1,1},
				{0,0,0,1,1,0,0,1,0,1,1},
				{1,0,1,1,1,1,1,1,1,1,1},
				{0,0,1,0,0,1,1,0,0,0,1},
				{1,0,1,0,1,0,0,0,0,1,1},
				{1,0,0,1,1,0,1,1,0,0,0},
				{1,1,1,0,0,0,0,0,1,1,1},
				{1,1,1,1,1,1,0,0,0,0,0},
				{0,0,1,0,1,1,0,0,1,1,1},
				{1,1,1,1,1,0,0,0,1,0,1},
				{0,0,1,1,0,1,0,0,1,1,1}	
		};
		q = tF.calculateTimes(times2, 5, 45, 3);
		System.out.println("QUEUE:");
		size = q.size();
		for(int i=0; i<size; i++){
			TimeAvailabilityPair t = q.poll();
			System.out.println(t.getTime() + ", " + t.getAttendance());
		}
		
		DateTime start = new DateTime();
		DateTime end = start.plusMinutes(240);
		System.out.println("dayLen: "+ tF.dayLen(start, end));
		System.out.println("numInterval 60min: " + tF.numIntervalsInDay(start, end, 60));
		System.out.println("numInterval 30min: " + tF.numIntervalsInDay(start, end, 30));
		System.out.println("numInterval 15min: " + tF.numIntervalsInDay(start, end, 15));
		end = start.plusDays(4).plusMinutes(240);
		System.out.println("num days: " + start.dayOfWeek().getAsText() + "-" + end.dayOfWeek().getAsText() + ", "+ (end.getDayOfYear() - start.getDayOfYear() + 1));
		System.out.println("interval=60; " + start.dayOfWeek().getAsText() + ", " + start.hourOfDay().getAsText() + ":"+ start.minuteOfHour().getAsText() +"-" + end.dayOfWeek().getAsText()+ ", " + end.hourOfDay().getAsText() + ":"+ end.minuteOfHour().getAsText() + "; m=" +(end.getDayOfYear() - start.getDayOfYear() + 1)*tF.numIntervalsInDay(start, end, 60));
		System.out.println("interval=30; " + start.dayOfWeek().getAsText() + ", " + start.hourOfDay().getAsText() + ":"+ start.minuteOfHour().getAsText() +"-" + end.dayOfWeek().getAsText()+ ", " + end.hourOfDay().getAsText() + ":"+ end.minuteOfHour().getAsText() + "; m=" +(end.getDayOfYear() - start.getDayOfYear() + 1)*tF.numIntervalsInDay(start, end, 30));
		System.out.println("interval=15; " + start.dayOfWeek().getAsText() + ", " + start.hourOfDay().getAsText() + ":"+ start.minuteOfHour().getAsText() +"-" + end.dayOfWeek().getAsText()+ ", " + end.hourOfDay().getAsText() + ":"+ end.minuteOfHour().getAsText() + "; m=" +(end.getDayOfYear() - start.getDayOfYear() + 1)*tF.numIntervalsInDay(start, end, 15));
		
		DateTime r = end.minusDays(4).minusMinutes(120);
		System.out.println("row for interval 60 on day 1: " + ((r.getDayOfYear() - start.getDayOfYear())*tF.numIntervalsInDay(start, end, 60) + (r.getMinuteOfDay() - start.getMinuteOfDay())/60));
		DateTime toCompare = start.plusDays(2/(tF.numIntervalsInDay(start, end, 60))).plusMinutes((2 % (tF.numIntervalsInDay(start, end, 60)))*60);
		System.out.println(r.dayOfWeek().getAsText() + ", "+ r.hourOfDay().getAsText() + ":" + r.minuteOfHour().getAsText());
		System.out.println(toCompare.dayOfWeek().getAsText() + ", "+ toCompare.hourOfDay().getAsText() + ":" + toCompare.minuteOfHour().getAsText());
		r = r.plusDays(1);
		System.out.println("row for interval 60 on day 2: " + ((r.getDayOfYear() - start.getDayOfYear())*tF.numIntervalsInDay(start, end, 60) + (r.getMinuteOfDay() - start.getMinuteOfDay())/60));
		r = start;
		System.out.println("row for interval 60 on day 0: " + ((r.getDayOfYear() - start.getDayOfYear())*tF.numIntervalsInDay(start, end, 60) + (r.getMinuteOfDay() - start.getMinuteOfDay())/60));
		
	}
}

