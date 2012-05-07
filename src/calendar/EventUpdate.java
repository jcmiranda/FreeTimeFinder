package calendar;

/**
 * Represents a change between an existing w2m stored in the program and the most recent version of it pulled from the internet
 *
 */

public class EventUpdate {
	private String _updateString;
	
	public EventUpdate(String msg) {
		_updateString = msg;
	}
	
	public String getMessage(){
		return _updateString;
	}
}
