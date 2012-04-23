package calendar;

import java.util.ArrayList;

import org.joda.time.DateTime;

public class CalendarSlots implements Calendar {
	private DateTime _startTime;
	private DateTime _endTime;
	private int _minInSlot;
	private int _numSlotsInDay;
	private int _numDays;
	private When2MeetOwner _owner;
	private Availability[][] _avail;
	
	
	
	public CalendarSlots(DateTime startTime, DateTime endTime, int minInSlot, Availability initAvail) {
		_startTime = startTime;
		_endTime = endTime;
		_numSlotsInDay = (lenDayInMinutes() + 1) / minInSlot;
		_numDays = numDays();
		_minInSlot = minInSlot;
		
		_avail = new Availability[_numDays][_numSlotsInDay];
		for(int day = 0; day < _numDays; day++)
			for(int slot = 0; slot < _numSlotsInDay; slot++)
				_avail[day][slot] = initAvail;
	}
	
	public CalendarSlots(DateTime startTime, DateTime endTime, When2MeetOwner owner, int minInSlot, Availability[][] availability){
		_startTime = startTime;
		_endTime = endTime;
		_owner = owner;
		_minInSlot = minInSlot;
		_numDays = availability.length;
		_numSlotsInDay = lenDayInMinutes() / minInSlot;
		_avail = availability;
	}
	
	// Need absolute value in case endtime is midnight
	public int lenDayInMinutes() {
		if(_endTime.getMinuteOfDay() == 0)
			return 24*60 - _startTime.getMinuteOfDay();
		else
			return _endTime.getMinuteOfDay() - _startTime.getMinuteOfDay();
	}
	
	public int numDays() {
		if(_endTime.getYear() == _startTime.getYear())
			return _endTime.getDayOfYear() - _startTime.getDayOfYear() + 1;
		else if(_endTime.getYear() == _startTime.getYear() + 1)
			return _endTime.getDayOfYear() + 366 - _startTime.getDayOfYear();
		System.err.println("err in numDays in CalendarSlotsImpl");
		System.exit(1);
		return -1;
	}
	
	@Override
	public DateTime getStartTime() { return _startTime; }

	@Override
	public DateTime getEndTime() { return _endTime;	}

	public When2MeetOwner getOwner() { return _owner; }
	
	public int getSlotsInDay() { return _numSlotsInDay; }
	
	public int getTotalSlots() { return _numDays * _numSlotsInDay; }

	
	public Availability getAvail(int day, int slot) {
		return _avail[day][slot];
	}
	
	public Availability getAvail(int slot) {
		int day = slot / _numSlotsInDay;
		int slotInDay = slot % _numSlotsInDay;
		return _avail[day][slotInDay];
	}
	
	public ArrayList<Integer> getSlotsForAvail(Availability avail){
		ArrayList<Integer> toReturn = new ArrayList<Integer>();

		for(int slot = 0; slot < getTotalSlots(); slot++) {
			if(getAvail(slot) == avail)
				toReturn.add(new Integer(slot));
		}
		return toReturn;
	}

	public void setAvail(int day, int slot, Availability avail) {
		_avail[day][slot] = avail;
		return;
	}

	
	public void setAvail(int slot, Availability avail) {
		int day = slot / _numSlotsInDay;
		int slotInDay = slot % _numSlotsInDay;
		// System.out.println("Day: " + day + " Slot in Day: " + slotInDay);
		_avail[day][slotInDay] = avail;
	}

	
	public void setOwner(When2MeetOwner o) { _owner = o; }

	
	public void print() {
		for(int slotInDay = 0; slotInDay < _numSlotsInDay; slotInDay++){
			if(slotInDay % 4 == 0)
				System.out.println("=========");
			for(int day = 0; day < _numDays; day++) {
				if(_avail[day][slotInDay] == Availability.busy)
					System.out.print("b");
				else
					System.out.print("f");
			}
			System.out.println();
		}
	}

	private int timeToSlot(DateTime time, boolean roundEarly) {
		assert time.compareTo(_startTime) >= 0 : "Time before start of calendar";
		assert time.compareTo(_endTime) <= 0 : "Time after end of calendar";
		
		int daysOff = time.getDayOfYear() - _startTime.getDayOfYear();
        int minutesOff = time.getMinuteOfDay() - _startTime.getMinuteOfDay();
        if(roundEarly)
        	return daysOff * _numSlotsInDay + minutesOff / _minInSlot;
        else
        	return daysOff * _numSlotsInDay + minutesOff / _minInSlot + 1;
	}
	
	//TODO this may fail if endTime is the same as the end of the calendar
	
	public void setAvail(DateTime startTime, DateTime endTime, Availability avail) {
		int startSlot = timeToSlot(startTime, true);
		int endSlot = timeToSlot(endTime, false);
		
		assert startSlot >= 0 : "Negative start slot";
		assert startSlot < _numDays * _numSlotsInDay : "Start slot greater than number of slots in cal";
		assert endSlot >= 0 : "Negative end slot";
		assert endSlot < _numDays * _numSlotsInDay : "End slot greater than number of slots in cal";
		
		for(int slot = startSlot; slot < endSlot; slot++)
			setAvail(slot, avail);
		
	}

	
	public int getMinInSlot() {
		return _minInSlot;
	}
	
	

}
