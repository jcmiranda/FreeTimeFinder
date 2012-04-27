package cal_master;

import java.io.IOException;

import java.util.ArrayList;

import org.joda.time.DateTime;

import com.google.gdata.util.ServiceException;

import calendar.Availability;
import calendar.CalendarGroup;
import calendar.CalendarResponses;
import calendar.CalendarSlots;
import calendar.Event;
import calendar.GoogleCalendars;
import calendar.Response;
import calendar.When2MeetEvent;
import calendar.When2MeetOwner;


public class Converter {
	
	private int _numSlotsInDay;
	private static final int INTERVAL = 15;
	private DateTime _eventStart, _eventEnd;
	
	
	private int dayLen(DateTime start, DateTime end){
		return end.getMinuteOfDay() - start.getMinuteOfDay() + 1;
	}
	
	/*private int toSlot(DateTime dt, boolean isEndTime) {
		int daysOff = dt.getDayOfYear() - _calStart.getDayOfYear();
		int minutesOff = dt.getMinuteOfDay() - _calStart.getMinuteOfDay();
		int offset = 0;
		if(isEndTime && minutesOff % INTERVAL != 0){
			offset = 1;
		}
		return daysOff * _numSlotsInDay + minutesOff / INTERVAL + offset;
	}*/
	
	private int toCol(DateTime dt){
		return dt.getDayOfYear() - _eventStart.getDayOfYear();
	}
	
	private int toRow(DateTime dt, boolean isEndTime){
		int endMinutes = _eventEnd.getMinuteOfDay();
		if(endMinutes == 0)
			endMinutes = 24 * 60;
		int thisMinutes = dt.getMinuteOfDay();
		if(thisMinutes == 0 && isEndTime)
			thisMinutes = 24 * 60;
		
		int minutesOff;
		if(isEndTime)
			minutesOff = Math.min(thisMinutes, endMinutes) - _eventStart.getMinuteOfDay();
		else
			minutesOff = Math.max(thisMinutes - _eventStart.getMinuteOfDay(), 0);
		
		int offset = 0;
		if(isEndTime && minutesOff % INTERVAL != 0){
			offset = 1;
		}
		return minutesOff / INTERVAL + offset;
	}
	
	private boolean isBeforeEndTimeOfDay(DateTime dt) {
		int endMinutes = _eventEnd.getMinuteOfDay();
		if(endMinutes == 0)
			endMinutes = 24 * 60;
		return dt.getMinuteOfDay() <= endMinutes;
		//return false;
	}
	
	private boolean isAfterStartTimeOfDay(DateTime dt) {
		return dt.getMinuteOfDay() >= _eventStart.getMinuteOfDay();
	}
	
	public CalendarSlots calToSlots(CalendarGroup<CalendarResponses> userCal, Event w2m){
		
		ArrayList<CalendarResponses> calendars = userCal.getCalendars();
		if(calendars.size() <= 0){
			return null;
		}
		
		int dayLen = dayLen(w2m.getStartTime(), w2m.getEndTime());
		_numSlotsInDay = dayLen / INTERVAL;
		_eventStart = w2m.getStartTime();
		_eventEnd = w2m.getEndTime();
		
		int numDays = w2m.getEndTime().getDayOfYear() - w2m.getStartTime().getDayOfYear() + 1;
		Availability[][] availability = new Availability[numDays][_numSlotsInDay];
		
		for(int r=0; r<numDays; r++){
			for(int c=0; c<_numSlotsInDay; c++){
				availability[r][c] = Availability.free;
			}
		}
		
		//int col=0, row=0;
		for(CalendarResponses cal : calendars){
			ArrayList<Response> responses = (ArrayList<Response>) cal.getResponses();
			//_calStart = cal.getStartTime();
			for(Response r : responses){
				
				DateTime rStart = r.getStartTime();
				DateTime rEnd = r.getEndTime();
				/* ignore responses that begin after the w2m ends or end before the w2m starts */
				if(rStart.isBefore(_eventEnd) && rEnd.isAfter(_eventStart) &&
						isBeforeEndTimeOfDay(rStart) && isAfterStartTimeOfDay(rEnd)){
					System.out.println("Response Name: " + r.getName());
					int startMin = toRow(r.getStartTime(), false);
					int startDay = toCol(r.getStartTime());
					
					/* events that begin before the w2m starts should be altered to start at the beginning of the w2m*/
					if(startDay < 0){
						startDay = 0;
						startMin = 0;
					}
					
					int endMin = toRow(r.getEndTime(), true);
					int endDay = toCol(r.getEndTime());
					
					/* events that end after the w2m ends should be altered to end when the w2m ends*/
					if(endDay >= numDays){
						endDay = numDays-1;
						endMin = _numSlotsInDay;
					}
					
					int currDay = startDay;
					int currMin = startMin;
					while(currDay <= endDay){
						if(currDay == endDay && currMin >= endMin){
							break;
						}
						System.out.println("Busy Day: " + currDay + "\tMin: " + currMin);
						availability[currDay][currMin] = Availability.busy;
						currMin = (currMin+1)%_numSlotsInDay;
						if(currMin == 0){
							currDay++;
						}
					}
				}
				
			}
		}
		//System.out.println("num rows = "+availability.length+" num cols = "+availability[0].length);
		
		return new CalendarSlots(_eventStart, _eventEnd, null, INTERVAL, availability);
		
	}
	
	public static void main(String[] args) throws IOException, ServiceException{

    	/*
		GCalImporter myImporter = new GCalImporter();
    	DateTime startTime = new DateTime(2011, 6, 28, 8, 0);
		DateTime endTime = new DateTime(2011, 7, 15, 23, 0);
    	GoogleCalendars myCal = myImporter.importMyGCal(startTime, endTime);
    	endTime = endTime.plusDays(5).minusHours(10);
    	When2MeetEvent w2me = new When2MeetEvent(startTime, endTime);
    	Converter myConverter = new Converter();
    	CalendarSlots slots = myConverter.gCalToSlots(myCal, w2me);
    	System.out.println("slots in day: " + slots.getSlotsInDay());
    	System.out.println("*********");
    	slots.print();
    	System.out.println("=========");*/

	}
}
	
	
