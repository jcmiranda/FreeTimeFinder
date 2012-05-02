package calendar;

public enum Availability {
	
	free, busy;
	
	private static final int USER_PRIORITY = 5;

	/**
	 * Get availability in terms of int to use in FreeTimeFinder
	 * @return
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
	 * @return
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
