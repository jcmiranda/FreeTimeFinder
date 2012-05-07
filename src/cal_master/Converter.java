package cal_master;

import java.util.ArrayList;

import org.joda.time.DateTime;

import calendar.Availability;
import calendar.CalendarResponses;
import calendar.CalendarSlots;
import calendar.Event;
import calendar.Response;
import calendar.UserCal;

/**
 * The class that calculates conversions between CalendarResponses and CalendarSlots
 *
 */


public class Converter {
	
	private int _numSlotsInDay;
	private static final int INTERVAL = 15;
	private DateTime _eventStart, _eventEnd;
	
	/**
	 * Gives the length (number of minutes) in a day that starts at start and ends at end
	 */
	private int dayLen(DateTime start, DateTime end){
		return end.getMinuteOfDay() - start.getMinuteOfDay() + 1;
	}
	
	/**
	 * Converts from time (day of year) to column of the Availability array
	 */
	private int toCol(DateTime dt){
		return dt.getDayOfYear() - _eventStart.getDayOfYear();
	}
	
	/**
	 * Converts from time (hours and minutes) to row in the Availability array
	 * @param dt -- time to convert
	 * @param isEndTime -- whether the time represents the time a response starts or when it ends
	 * @return
	 */
	private int toRow(DateTime dt, boolean isEndTime){
		
		int endMinutes = _eventEnd.getMinuteOfDay();
		
		//0 represents midnight, but if endTime is midnight, it should be greater than all other times of the day
		if(endMinutes == 0)
			endMinutes = 24 * 60;
		
		
		int thisMinutes = dt.getMinuteOfDay();
		
		//midnight as end of day
		if(thisMinutes == 0 && isEndTime)
			thisMinutes = 24 * 60;
		
		int minutesOff;
		
		//shouldn't use a time that is later than the event's end time
		if(isEndTime)
			minutesOff = Math.min(thisMinutes, endMinutes) - _eventStart.getMinuteOfDay();
		//shouldn't use a time that is earlier that the event's start time
		else
			minutesOff = Math.max(thisMinutes - _eventStart.getMinuteOfDay(), 0);
		
		int offset = 0;
		//if end time, round up to the nearest 15 min interval; otherwise, round down to the nearest 15
		if(isEndTime && minutesOff % INTERVAL != 0){
			offset = 1;
		}
		
		return minutesOff / INTERVAL + offset;
		
	}
	
	/**
	 * Returns whether the given time is before the event's end time
	 */
	private boolean isBeforeEndTimeOfDay(DateTime dt) {
		
		int endMinutes = _eventEnd.getMinuteOfDay();
		
		if(endMinutes == 0)
			endMinutes = 24 * 60;
		
		return dt.getMinuteOfDay() <= endMinutes;
	
	}
	
	/**
	 * Returns whether the given time is after the event's start time
	 */
	private boolean isAfterStartTimeOfDay(DateTime dt) {
		return dt.getMinuteOfDay() >= _eventStart.getMinuteOfDay();
	}
	
	/**
	 * Returns a CalendarSlots representation of the user's calendar based on the given event
	 */
	public CalendarSlots calToSlots(UserCal userCal, Event event){
		
		ArrayList<CalendarResponses> calendars = userCal.getCalendars();
		if(calendars.size() <= 0){
			return null;
		}
		
		int dayLen = dayLen(event.getStartTime(), event.getEndTime());
		_numSlotsInDay = dayLen / INTERVAL;
		_eventStart = event.getStartTime();
		_eventEnd = event.getEndTime();
		
		int numDays = event.getEndTime().getDayOfYear() - event.getStartTime().getDayOfYear() + 1;
		Availability[][] availability = new Availability[numDays][_numSlotsInDay];
		
		// initialize the array to all free times (since responses represent busy times)
		for(int r=0; r<numDays; r++){
			for(int c=0; c<_numSlotsInDay; c++){
				availability[r][c] = Availability.free;
			}
		}
		
		for(CalendarResponses cal : calendars){
			ArrayList<Response> responses = cal.getResponses();
			
			for(Response r : responses){
				
				DateTime rStart = r.getStartTime();
				DateTime rEnd = r.getEndTime();
				
				/* ignore responses that begin after the event ends or end before the event starts */
				if(rStart.isBefore(_eventEnd) && rEnd.isAfter(_eventStart) &&
						isBeforeEndTimeOfDay(rStart) && isAfterStartTimeOfDay(rEnd)){
					
					int startMin = toRow(r.getStartTime(), false);
					int startDay = toCol(r.getStartTime());
					
					/* events that begin before the event starts should be altered to start at the beginning of the event*/
					if(startDay < 0){
						startDay = 0;
						startMin = 0;
					}
					
					int endMin = toRow(r.getEndTime(), true);
					int endDay = toCol(r.getEndTime());
					
					/* events that end after the event ends should be altered to end when the event ends*/
					if(endDay >= numDays){
						endDay = numDays-1;
						endMin = _numSlotsInDay;
					}
					
					int currDay = startDay;
					int currMin = startMin;
					while(currDay <= endDay){
						if(currDay == endDay && currMin >= endMin)
							break;
						availability[currDay][currMin] = Availability.busy;
						currMin = (currMin+1)%_numSlotsInDay;
						if(currMin == 0){
							currDay++;
						}
					}
				}
				
			}
		}
		
		return new CalendarSlots(_eventStart, _eventEnd, null, INTERVAL, availability);
		
	}
	
}
	
	
