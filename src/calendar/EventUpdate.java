package calendar;

public class EventUpdate {
	private String _updateString;
	
	public EventUpdate(String msg) {
		_updateString = msg;
	}
	
	public String getMessage(){
		return _updateString;
	}
}
