package calendar;

import org.joda.time.DateTime;

public class CalendarSlotsImpl implements CalendarSlots {
	private DateTime _startTime;
	private DateTime _endTime;
	private int _numSlotsInDay;
	private int _numDays;
	private Owner _owner = new OwnerImpl("Unowned");
	private CalSlotsFB[][] _avail;
	
	public CalendarSlotsImpl(DateTime startTime, DateTime endTime, Owner owner, int minInSlot, CalSlotsFB initAvail) {
		_startTime = startTime;
		_endTime = endTime;
		_owner = owner;
		_numSlotsInDay = lenDayInMinutes() / minInSlot;
		_numDays = numDays();
		
		_avail = new CalSlotsFB[_numDays][_numSlotsInDay];
		for(int day = 0; day < _numDays; day++)
			for(int slot = 0; slot < _numSlotsInDay; slot++)
				_avail[day][slot] = initAvail;
		
	}
	
	private int lenDayInMinutes() {
		return _endTime.getMinuteOfDay() - _startTime.getMinuteOfDay();
	}
	
	private int numDays() {
		if(_endTime.getYear() == _startTime.getYear())
			return _endTime.getDayOfYear() - _startTime.getDayOfYear() + 1;
		else if(_endTime.getYear() == _startTime.getYear() + 1)
			return _endTime.getDayOfYear() + 365 - _startTime.getDayOfYear();
		System.err.println("err in numDays in CalendarSlotsImpl");
		System.exit(1);
		return -1;
	}
	
	@Override
	public DateTime getStartTime() { return _startTime; }

	@Override
	public DateTime getEndTime() { return _endTime;	}

	@Override
	public Owner getOwner() { return _owner; }

	@Override
	public int getSlotsInDay() { return _numSlotsInDay; }

	@Override
	public int getTotalSlots() { return _numDays * _numSlotsInDay; }

	@Override
	public CalSlotsFB getAvail(int day, int slot) {
		return _avail[day][slot];
	}

	@Override
	public CalSlotsFB getAvail(int slot) {
		int day = slot / _numSlotsInDay;
		int slotInDay = slot % _numSlotsInDay;
		return _avail[day][slotInDay];
	}

	@Override
	public void setAvail(int day, int slot, CalSlotsFB avail) {
		_avail[day][slot] = avail;
		return;
	}

	@Override
	public void setAvail(int slot, CalSlotsFB avail) {
		int day = slot / _numSlotsInDay;
		int slotInDay = slot % _numSlotsInDay;
		_avail[day][slotInDay] = avail;
	}

}
