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
		assert oldCal != null;
		assert newCal != null;
		
		When2MeetOwner oldOwner = oldCal.getOwner();
		When2MeetOwner newOwner = newCal.getOwner();
		
		if(oldOwner.getID() != newOwner.getID())
			throw new MismatchedUserIDException();
		else if(!oldOwner.getName().equals(newOwner.getName()))
			throw new MismatchedUserNamesException();
		
		// TODO delete this if created correctly
		int slotsInDay = oldCal.getSlotsInDay();
		int numDays = oldCal.getTotalSlots() / slotsInDay;
		
		assert oldCal.getSlotsInDay() == newCal.getSlotsInDay();
		assert oldCal.getStartTime().equals(newCal.getStartTime());
		assert oldCal.getEndTime().equals(newCal.getEndTime());
		
		
		ArrayList<EventUpdate> updates = new ArrayList<EventUpdate>();
		for(int day = 0; day < numDays; day++) {
			for(int slot = 0; slot < numDays; slot++) {
				Availability oldAvail = oldCal.getAvail(day, slot);
				Availability newAvail = newCal.getAvail(day, slot);
				
				if(oldAvail != newAvail) {
					updates.add(new EventUpdate(newOwner.getName() + 
							" changed his/her availability on day " + (day+1)));
					break;
				}
					
			}
		}
		
		return updates;
	}
}
