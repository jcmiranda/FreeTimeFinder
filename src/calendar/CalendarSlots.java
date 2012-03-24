package calendar;

import java.util.Collection;

import org.joda.time.DateTime;

public interface CalendarSlots {
	
	public enum CalSlotsFB {free, busy};
	// Accessors for start time, end time and owner
	// All should be set at creation
	DateTime getStartTime();
	DateTime getEndTime();
	Owner getOwner();
	
	int getSlotsInDay();
	int getTotalSlots();
	// Accessors for data in calendar
	CalSlotsFB getAvail(int day, int slot);
	CalSlotsFB getAvail(int slot);
	
	void setOwner(Owner o);
	void setAvail(int day, int slot, CalSlotsFB avail);
	void setAvail(int slot, CalSlotsFB avail);
	
	void invert();
	void print();
}
