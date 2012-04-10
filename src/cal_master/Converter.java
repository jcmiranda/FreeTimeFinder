package cal_master;

import java.io.IOException;
import java.util.ArrayList;

import org.joda.time.DateTime;

import com.google.gdata.util.ServiceException;

import calendar.CalendarGroup;
import calendar.CalendarImpl;
import calendar.CalendarSlotsImpl;
import calendar.GoogleCalendars;
import calendar.Owner;
import calendar.Response;
import calendar.CalendarSlots.CalSlotsFB;
import calendar_importers.GCalImporter;

public class Converter {
	
	private int _numSlotsInDay;
	private static final int INTERVAL = 15;
	private DateTime _calStart;
	
	
	private int dayLen(DateTime start, DateTime end){
		return end.getMinuteOfDay() - start.getMinuteOfDay() + 1;
	}
	
	private int toSlot(DateTime dt, boolean isEndTime) {
		int daysOff = dt.getDayOfYear() - _calStart.getDayOfYear();
		int minutesOff = dt.getMinuteOfDay() - _calStart.getMinuteOfDay();
		int offset = 0;
		if(isEndTime && minutesOff % INTERVAL != 0){
			offset = 1;
		}
		return daysOff * _numSlotsInDay + minutesOff / INTERVAL + offset;
	}
	
	private int toCol(DateTime dt){
		return dt.getDayOfYear() - _calStart.getDayOfYear();
	}
	
	private int toRow(DateTime dt, boolean isEndTime){
		int minutesOff = dt.getMinuteOfDay() - _calStart.getMinuteOfDay();
		int offset = 0;
		if(isEndTime && minutesOff % INTERVAL != 0){
			offset = 1;
		}
		return minutesOff / INTERVAL + offset;
	}
	
	public CalendarSlotsImpl gCalToSlots(GoogleCalendars gCal){
		
		ArrayList<CalendarImpl> calendars = gCal.getCalendars();
		if(calendars.size() <= 0){
			return null;
		}
		
		CalendarImpl firstCal = calendars.get(0);
		int dayLen = dayLen(firstCal.getStartTime(), firstCal.getEndTime());
		_numSlotsInDay = dayLen / INTERVAL;
		
		int n = _numSlotsInDay;
		int m = firstCal.getEndTime().getDayOfYear() - firstCal.getStartTime().getDayOfYear() + 1;
		CalSlotsFB[][] availability = new CalSlotsFB[m][n];
		
		for(int r=0; r<m; r++){
			for(int c=0; c<n; c++){
				availability[r][c] = CalSlotsFB.free;
			}
		}
		
		//int col=0, row=0;
		for(CalendarImpl cal : calendars){
			ArrayList<Response> responses = (ArrayList<Response>) cal.getResponses();
			_calStart = cal.getStartTime();
			for(Response r : responses){
				int startMin = toRow(r.getStartTime(), false);
				int startDay = toCol(r.getStartTime());
				
				int endMin = toRow(r.getEndTime(), true);
				int endDay = toCol(r.getEndTime());
				
				int currDay = startDay;
				int currMin = startMin;
				while(currDay <= endDay){
					if(currDay == endDay && currMin >= endMin){
						break;
					}
					availability[currDay][currMin] = CalSlotsFB.busy;
					currMin = (currMin+1)%_numSlotsInDay;
					if(currMin == 0){
						currDay++;
					}
				}
				
				/*row = toSlot(r.getStartTime(), false);
				int end = toSlot(r.getEndTime(), true);
				while(row < end){
					availability[row][col] = CalSlotsFB.busy;
					row++;
				}*/
				
			}
			//col++;
		}
		System.out.println("num rows = "+availability.length+" num cols = "+availability[0].length);
		return new CalendarSlotsImpl(_calStart, firstCal.getEndTime(), gCal.getOwner(), INTERVAL, availability);
		
	}
	
	public static void main(String[] args) throws IOException, ServiceException{
    	GCalImporter myImporter = new GCalImporter();
    	org.joda.time.DateTime startTime = new org.joda.time.DateTime(2011, 6, 28, 8, 0);
		org.joda.time.DateTime endTime = new org.joda.time.DateTime(2011, 7, 15, 23, 0);
    	GoogleCalendars myCal = myImporter.importMyGCal(startTime, endTime);
    	Converter myConverter = new Converter();
    	CalendarSlotsImpl slots = myConverter.gCalToSlots(myCal);
    	System.out.println("slots in day: " + slots.getSlotsInDay());
    	System.out.println("*********");
    	slots.print();
    	System.out.println("=========");
	}
}
	
	
