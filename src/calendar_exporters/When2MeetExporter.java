package calendar_exporters;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

import calendar.Availability;
import calendar.CalendarSlots;
import calendar.When2MeetEvent;

public class When2MeetExporter {
	private When2MeetEvent _event = null;
	
	/*
	public When2MeetExporter() { //When2MeetEvent event) {
		//_event = event;
	}*/
	
	private String encode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (Exception e) {
			// TODO fix
			System.err.println("Error encoding string" + s);
			System.exit(1);
			return "AHHHHH";
		}
	}
	
	private String binaryAvailability(CalendarSlots cal) {
		String ret = "";
		for(int i = 0; i < cal.getTotalSlots(); i++) {
			Availability avail = cal.getAvail(i);
			if(avail == Availability.free)
				ret += "1";
			else
				ret += "0";
		}
		return ret;
	}
	
	private String slotIDs(ArrayList<Integer> slotIndices) {
		String ret = "";
		for(int i = 0; i < slotIndices.size(); i++) {
			ret += _event.getSlotID(slotIndices.get(i));
			if(i != slotIndices.size() - 1)
				ret += ",";
		}
		return ret;
	}
	
	private class KeyValue {
		private String _key, _value;
		public KeyValue(String key, String value) {
			_key = key;
			_value = value;
		}
		public String toEncodedString() {
			return encode(_key) + "=" + encode(_value);
		}
	}
	
	private String buildKeyValueString(ArrayList<KeyValue> keyValues) {
		String ret = "";
		for(int i = 0; i < keyValues.size(); i++) {
			ret += keyValues.get(i).toEncodedString();
			if(i != keyValues.size() - 1)
				ret += "&";
		}
		return ret;
	}
	
	private String post(ArrayList<KeyValue> keyValues, String urlString) {
		URL url;
		String resp = "";
		try {
			url = new URL(urlString);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			String data = buildKeyValueString(keyValues);
			
			wr.write(data);
		    wr.flush();
		    
		    // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    
		    String line;
		    while ((line = rd.readLine()) != null) 
		        resp += line;
		    
		    wr.close();		
		} catch (Exception e) {

		}
		// TODO implement properly
		return resp;
	}
	
	public void postAvailability(CalendarSlots cal, ArrayList<Integer> slotIndices, Availability avail) {
		URL url;
		String changeToAvailable = "false";
		if(avail == Availability.free) {
			changeToAvailable = "true";
		}
		
		for(int i = 0; i < slotIndices.size(); i++) {
			cal.setAvail(slotIndices.get(i), avail);
		}
		
		String person = ""+cal.getOwner().getID();
		String eventID = _event.getID()+"";
		String slots = slotIDs(slotIndices);
		ArrayList<KeyValue> keyValues = new ArrayList<KeyValue>();
		System.out.println("Person: " + person + "\tEvent ID: " + 
				eventID + "\tSlots: " + slots + "\tToAvail: " + changeToAvailable);
		keyValues.add(new KeyValue("person", person));
		keyValues.add(new KeyValue("event", eventID));
		keyValues.add(new KeyValue("slots", slots));
		keyValues.add(new KeyValue("availability", binaryAvailability(cal)));
		keyValues.add(new KeyValue("ChangeToAvailable", changeToAvailable));
		
		String resp = post(keyValues, "http://www.when2meet.com/SaveTimes.php");
		System.out.println(resp);
	}
	
	public class NameAlreadyExistsException extends Exception {
		
	}
	
	// When this method is called, this calendar owner has a name unique from
	// all the other calendar owners
	public void createNewUser(When2MeetEvent event, CalendarSlots cal, String password) throws NameAlreadyExistsException {
		System.out.println("Creating new user with name " + cal.getOwner().getName());
		ArrayList<KeyValue> keyValues = new ArrayList<KeyValue>();
		keyValues.add(new KeyValue("id", ""+event.getID()));
		keyValues.add(new KeyValue("name", "" + cal.getOwner().getName()));
		keyValues.add(new KeyValue("password", password));
		String resp = post(keyValues, "http://www.when2meet.com/ProcessLogin.php");
		if(resp.equalsIgnoreCase("Wrong Password.")) {
			throw new NameAlreadyExistsException();
		} else {
			int id = Integer.parseInt(resp);
			for(CalendarSlots c : event.getCalendars()){
				if(id == c.getOwner().getID()){
					throw new NameAlreadyExistsException();
				}
			}
			cal.getOwner().setID(id);
		}
		//this.postAllAvailability(event, cal);
	}
	public void createNewUserNoPassword(When2MeetEvent event, CalendarSlots cal) throws NameAlreadyExistsException {
		createNewUser(event, cal, "");
	}
	
	private String startTime() {
		return ""+_event.getStartTime().getHourOfDay();
	}
	
	private String endTime() {
		if(_event.getEndTime().getMinuteOfHour() == 59)
			return ""+0;
		else
			return ""+_event.getEndTime().getHourOfDay();
	}
	
	// TODO fix to deal with non consecutive dates. Fix to deal with dates not in the same year
	private String possibleDates() {
		DateTime curDate = _event.getStartTime();
		String ret = "";
		for(int i = _event.getStartTime().getDayOfYear(); i <= _event.getEndTime().getDayOfYear(); i++) {
			ret += curDate.getYear() + "-"+curDate.getMonthOfYear()+"-"+curDate.getDayOfMonth()+"|";
			curDate = curDate.plusDays(1);
		}
		ret = ret.substring(0, ret.length()-1);
		return ret;
	}
	
	public void postAllAvailability(When2MeetEvent event) throws NameAlreadyExistsException {
		_event = event;
		System.out.println("Posting all availability for user " 
				+ _event.getUserResponse().getOwner().getName());
		CalendarSlots cal = event.getUserResponse();
		if(!event.userHasSubmitted()){
			String password = "";
			//TODO: ask user if they want to use password
			this.createNewUser(event, cal, password);
			event.setUserSubmitted(true);
		}
		ArrayList<Integer> busySlots = cal.getSlotsForAvail(Availability.busy);
		ArrayList<Integer> freeSlots = cal.getSlotsForAvail(Availability.free);
		this.postAvailability(cal, busySlots, Availability.busy);
		this.postAvailability(cal, freeSlots, Availability.free);
	}
	
	public void postNewEvent(When2MeetEvent event) {
		_event = event;
		ArrayList<KeyValue> keyValues = new ArrayList<KeyValue>();
		keyValues.add(new KeyValue("NewEventName", _event.getName()));
		keyValues.add(new KeyValue("DateTypes", "SpecificDates"));
		keyValues.add(new KeyValue("PossibleDates", possibleDates()));
		keyValues.add(new KeyValue("NoEarlierThan", startTime()));
		keyValues.add(new KeyValue("NoLaterThan", endTime()));
		String toParse = this.post(keyValues, "http://www.when2meet.com/SaveNewEvent.php");
		
		Pattern eventIDPattern = Pattern.compile("<html><body onload=\\\"window.location='/\\?(\\d+)-([a-zA-Z0-9]*)'\\\"></body></html>");
		Matcher matcher = eventIDPattern.matcher(toParse);
		if(matcher.matches()) {
			int id = Integer.parseInt(matcher.group(1));
			_event.setID(id);
			String second = matcher.group(2);
			_event.setURL("http://www.when2meet.com/?"+id+"-"+second);
			System.out.println("URL set to: " + _event.getURL());
			
		}
		
	}
	
}
