package calendar;

import org.joda.time.DateTime;

public class CalendarSlotsImpl implements CalendarSlots {
	private DateTime _startTime;
	private DateTime _endTime;
	private int _numSlotsInDay;
	private int _numDays;
	private Owner _owner = new OwnerImpl("Unowned");
	private CalSlotsFB[][] _avail;
	
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
