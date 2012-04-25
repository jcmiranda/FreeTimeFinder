package calendar;

import java.util.ArrayList;
import java.util.Collection;

import org.joda.time.DateTime;

public class When2MeetEvent extends CalendarGroup<CalendarSlots> {
	
	private String _name, _url;
	private int _id;
	private CalendarSlots _userResponse = null;
	private boolean _userHasSubmitted = false;
	private ArrayList<Integer> _slotIndexToSlotID = new ArrayList<Integer>();
	private ArrayList<EventUpdate> _updates = null;
	private boolean _hasUpdates = false;
	
	
	public When2MeetEvent(DateTime st, DateTime et, String name, int id, String url,
			Collection<CalendarSlots> cals, ArrayList<Integer> slotIndToID){
		super(st, et, cals, CalGroupType.When2MeetEvent);
		_name = name;
		_id = id;
		_url = url;
		_slotIndexToSlotID = slotIndToID;
	}
	
	public void setID(int id) { _id = id; };
	public void setURL(String url) { _url = url; }
	public void setName(String name) {_name = name; }
	public void setUserResponse(CalendarSlots cal) { 
		_userResponse = cal; 
		this.removeCalendar(cal);
	}
	public void setUserSubmitted(boolean b) {_userHasSubmitted = b; }
	
	public String getURL(){ return _url; }
	public int getID(){ return _id; }
	public String getName(){ return _name; }
	public CalendarSlots getUserResponse() { return _userResponse; }
	
	/** Updates **/
	public boolean hasUpdates() { return _hasUpdates; }
	public ArrayList<EventUpdate> getUpdates() { return _updates; }
	public void addUpdates(ArrayList<EventUpdate> newUpdates) {
		if(newUpdates.size() == 0)
			return;
		_hasUpdates = true;
		_updates.addAll(0,  newUpdates);
	}
	
	public void updatesViewed() { _hasUpdates = false;}
	
	
	public boolean userHasSubmitted(){
		return _userHasSubmitted;
	}
	
	
	
	
	public ArrayList<String> getCalOwnerNames() {
		ArrayList<String> names = new ArrayList<String>();
		if(_userHasSubmitted)
			names.add(_userResponse.getOwner().getName());
		
		for(CalendarSlots cal : this.getCalendars())
			names.add(cal.getOwner().getName());
		
		return names;
	}
	
	public CalendarSlots getCalByName(String name) {
		CalendarSlots cal = null;
		for(int i = 0; i < this.getCalendars().size(); i++) { 
			if(this.getCalendars().get(i).getOwner().getName().equalsIgnoreCase(name)) {
				cal = this.getCalendars().get(i);
				break;
			}
		}
		return cal;
	}
	
	public void removeCalByName(String name){
		this.getCalendars().remove(getCalByName(name));
	}
	
	public int getSlotID(int slotIndex) {
		try {
			return _slotIndexToSlotID.get(slotIndex);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Invalid slot index for getting ID");
			return -1;
		}
	}


}
