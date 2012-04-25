package calendar;

import java.util.ArrayList;

public class CalendarDifferenceCalculator {
	
	public class MismatchedUserIDException extends Exception {
		
	}
	public class MismatchedUserNamesException extends Exception {
		
	}
	
	public ArrayList<EventUpdate> diffEventCals(CalendarSlots oldCal, 
			CalendarSlots newCal) throws MismatchedUserIDException, 
			MismatchedUserNamesException {
		When2MeetOwner oldOwner = oldCal.getOwner();
		When2MeetOwner newOwner = newCal.getOwner();
		
		if(oldOwner.getID() != newOwner.getID())
			throw new MismatchedUserIDException();
		else if(!oldOwner.getName().equals(newOwner.getName()))
			throw new MismatchedUserNamesException();
		
		// TODO delete this if created correctly
		int slotsInDay = oldCal.getSlotsInDay();
		int numDays = oldCal.getTotalSlots() / slotsInDay;
		
		ArrayList<EventUpdate> updates = new ArrayList<EventUpdate>();
		for(int day = 0; day < numDays; day++) {
			for(int slot = 0; slot < numDays; slot++) {
				Availability oldAvail = oldCal.getAvail(day, slot);
				Availability newAvail = newCal.getAvail(day, slot);
				
				if(oldAvail != newAvail) {
					updates.add(new EventUpdate(newOwner.getName() + 
							"changed their availability on day " + day));
					break;
				}
					
			}
		}
		
		return updates;
	}
}
