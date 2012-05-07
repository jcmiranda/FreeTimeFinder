package calendar;

import java.util.ArrayList;
import java.util.Collection;

import org.joda.time.DateTime;

// Class for representing when2meets within our own program
// Contains information specific to when2meets, but different from just an event
// as an event could conceivably be a doodle poll as well

// This includes implementation specific details such as slotIndextoSlotIDs etc.

public class When2MeetEvent extends Event {
	
	private ArrayList<Integer> _slotIndexToSlotID = new ArrayList<Integer>();
	
	// Creates a when2meetevent given certain input parameters
	public When2MeetEvent(DateTime st, DateTime et, String name, int id, String url,
			Collection<CalendarSlots> cals, ArrayList<Integer> slotIndToID){
		super(st, et, cals, CalGroupType.When2MeetEvent);
		_name = name;
		_id = id;
		_url = url;
		_slotIndexToSlotID = slotIndToID;	
	}
	
	// Returns the number of minutes in a slot in a when2meet
	public int getMinInSlot(){
		return 15;
	}
	
	// Returns the slot ID of a given slot index
	public int getSlotID(int slotIndex) {
		try {
			return _slotIndexToSlotID.get(slotIndex);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Invalid slot index for getting ID");
			return -1;
		}
	}
	
	
}
