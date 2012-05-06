package calendar_importers;

import java.io.IOException;

import calendar.Event;
import calendar.When2MeetEvent;

public class EventImporter {
	private When2MeetImporter _w2mImporter;
	
	public EventImporter() {
		_w2mImporter = new When2MeetImporter();
	}
	
	public When2MeetEvent refreshEvent(When2MeetEvent w2m) {
		return _w2mImporter.refreshEvent(w2m);
	}
	
	public class InvalidURLException extends Exception { };
	
	public Event importNewEvent(String url) throws IOException, InvalidURLException {
		String URL = url.trim();
		if(_w2mImporter.isWhen2MeetURL(URL))
			return _w2mImporter.importNewEvent(URL);
		else
			throw new InvalidURLException();
	}
}
