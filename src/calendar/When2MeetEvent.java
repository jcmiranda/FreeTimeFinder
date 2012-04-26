package calendar;

import gui.DayPanel;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Collection;

import org.joda.time.DateTime;

public class When2MeetEvent extends Event {
	
	private ArrayList<Integer> _slotIndexToSlotID = new ArrayList<Integer>();
	
	public When2MeetEvent(DateTime st, DateTime et, String name, int id, String url,
			Collection<CalendarSlots> cals, ArrayList<Integer> slotIndToID){
		super(st, et, cals, CalGroupType.When2MeetEvent);
		_name = name;
		_id = id;
		_url = url;
		_slotIndexToSlotID = slotIndToID;	
	}
	
	/** Updates **/

	
	public int getSlotID(int slotIndex) {
		try {
			return _slotIndexToSlotID.get(slotIndex);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Invalid slot index for getting ID");
			return -1;
		}
	}
	
	
}
