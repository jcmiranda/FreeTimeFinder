package calendar;

/**
 * Represents a respondee's availability
 * Enum makes it easily extendible to include attendee priorities, preferred times, etc
 *
 */

public enum Availability {
	
	free, busy;
	
	//weighted value for program user so algo tends towards times when user is free/avoids times when they are busy
	private static final int USER_PRIORITY = 5;

	/**
	 * Get availability in terms of int to use in FreeTimeFinder
	 */
	public int getAvailAsInt(){
		switch(this){
			case free:
				return 1;
			case busy:
				return 0;
			default:
				return 0;
		}
	}
	
	
	/**
	 * Get availability of program user in terms of int for use in FreeTimeFinder
	 */
	public int getUserAvailAsInt(){
		int toReturn = getAvailAsInt();
		
		switch(this){
			case free:
				return toReturn + USER_PRIORITY;
			case busy:
				return toReturn - USER_PRIORITY;
			default:
				return toReturn;
		}
	}
	
}
