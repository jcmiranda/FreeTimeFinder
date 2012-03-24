package calendar;

import org.joda.time.DateTime;

public class CalendarSlotsImpl implements CalendarSlots {
	private DateTime _startTime;
	private DateTime _endTime;
	private int _numSlotsInDay;
	private int _numDays;
	private Owner _owner = new OwnerImpl("Unowned");
	private CalSlotsFB[][] _avail;
	
	public CalendarSlotsImpl(DateTime startTime, DateTime endTime, int minInSlot, CalSlotsFB initAvail) {
		_startTime = startTime;
		_endTime = endTime;
		System.out.println("StartTime:" + _startTime + "\tEndTime: "+_endTime);
		System.out.println("Length day: " + lenDayInMinutes());
		// 36 slots in a day
		_numSlotsInDay = lenDayInMinutes() / minInSlot;
		_numDays = numDays();
		
		_avail = new CalSlotsFB[_numDays][_numSlotsInDay];
		for(int day = 0; day < _numDays; day++)
			for(int slot = 0; slot < _numSlotsInDay; slot++)
				_avail[day][slot] = initAvail;
	}
	
	// Need absolute value in case endtime is midnight
	private int lenDayInMinutes() {
		if(_endTime.getMinuteOfDay() == 0)
			return 24*60 - _startTime.getMinuteOfDay();
		else
			return _endTime.getMinuteOfDay() - _startTime.getMinuteOfDay();
	}
	
	private int numDays() {
		if(_endTime.getYear() == _startTime.getYear())
			return _endTime.getDayOfYear() - _startTime.getDayOfYear();
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
		System.out.println("Slot: " + slot);
		System.out.println("Num slots in day: " + _numSlotsInDay);
		int day = slot / _numSlotsInDay;
		int slotInDay = slot % _numSlotsInDay;
		System.out.println("Day: " + day + "\tSlotInDay:" + slotInDay);
		_avail[day][slotInDay] = avail;
	}

	@Override
	public void setOwner(Owner o) { _owner = o; }

	@Override
	public void invert() {
		for(int day = 0; day < _numDays; day++) {
			for(int slotInDay = 0; slotInDay < _numSlotsInDay; slotInDay++){
				if(_avail[day][slotInDay] == CalSlotsFB.busy)
					_avail[day][slotInDay] = CalSlotsFB.free;
				else
					_avail[day][slotInDay] = CalSlotsFB.busy;
			}
		}
	}

	@Override
	public void print() {
		for(int slotInDay = 0; slotInDay < _numSlotsInDay; slotInDay++){
			if(slotInDay % 4 == 0)
				System.out.println("=========");
			for(int day = 0; day < _numDays; day++) {
				if(_avail[day][slotInDay] == CalSlotsFB.busy)
					System.out.print("b");
				else
					System.out.print("f");
			}
			System.out.println();
		}
	}
	
	

}
