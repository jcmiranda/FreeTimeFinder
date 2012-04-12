package calendar;

import java.util.ArrayList;
import java.util.Collection;

import org.joda.time.DateTime;

public class When2MeetEvent extends CalendarGroup<CalendarSlots> {
	
	private String _name, _url;
	private int _id;
	
	
	public When2MeetEvent(DateTime st, DateTime et, String name, int id, String url, Collection<CalendarSlots> cals){
		super(st, et, cals);
		_name = name;
		_id = id;
		_url = url;
	}
	
	public String getUrl(){
		return _url;
	}
	
	public int getID(){
		return _id;
	}
	
	public String getName(){
		return _name;
	}

}
