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

/* Class for exporting when2meets to the web:
 * 	- Can either create a new event
 * 	- Or update an existing event with changes in availability or new users
 */
public class When2MeetExporter {
	// The event that we are currently updating / exporting
	private When2MeetEvent _event = null;

	// Encode a string to use UTF-8 for posting to web (used on keys and values of key
	// value pairs)
	private String encode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (Exception e) {
			System.err.println("Error encoding string" + s);
			System.exit(1);
			return "AHHHHH";
		}
	}

	// Converts a Calendar to its binary availability - 1 represents free time
	// 0 represents busy time. one dimensional string with one number for each
	// 15 minute slot
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

	// List of the slotIDs that have changed their availability
	// Each slot in the day has a given slotID that is stored on parsing of the page
	private String slotIDs(ArrayList<Integer> slotIndices) {
		String ret = "";
		for(int i = 0; i < slotIndices.size(); i++) {
			ret += _event.getSlotID(slotIndices.get(i));
			if(i != slotIndices.size() - 1)
				ret += ",";
		}
		return ret;
	}

	// Class used for keeping track of Key Value argument pairs for posting to
	// when2meet - knows what type of key it is, what it's value is, and how to
	// encode the pair
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

	// From a list of keyValues builds a string that represents the addition
	// to a when2meet url for posting (could be used with any post requeset)
	// String format: key=value&key2=value2..
	private String buildKeyValueString(ArrayList<KeyValue> keyValues) {
		String ret = "";
		for(int i = 0; i < keyValues.size(); i++) {
			ret += keyValues.get(i).toEncodedString();
			if(i != keyValues.size() - 1)
				ret += "&";
		}
		return ret;
	}

	// Posts to a URL and waits for the response. Returns the entirety of the 
	// response back to the caller for processing.
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
		return resp;
	}

	// Posts the availability of a given calendar, for the give slotIndices,
	// for Availability array to saveTimes.php
	public void postAvailability(CalendarSlots cal, ArrayList<Integer> slotIndices, Availability avail) {
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
		keyValues.add(new KeyValue("person", person));
		keyValues.add(new KeyValue("event", eventID));
		keyValues.add(new KeyValue("slots", slots));
		keyValues.add(new KeyValue("availability", binaryAvailability(cal)));
		keyValues.add(new KeyValue("ChangeToAvailable", changeToAvailable));

		post(keyValues, "http://www.when2meet.com/SaveTimes.php");
	}

	public class EmptyEventException extends Exception {

	}

	public class NameAlreadyExistsException extends Exception {

	}

	// When this method is called, this calendar owner has a name unique from
	// all the other calendar owners
	public void createNewUser(When2MeetEvent event, CalendarSlots cal, String password) throws NameAlreadyExistsException {
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
	}
	
	// Creates a new user without a password for an existing when2meet event
	public void createNewUserNoPassword(When2MeetEvent event, CalendarSlots cal) throws NameAlreadyExistsException {
		createNewUser(event, cal, "");
	}

	// Turns startTime into a string with hour of day
	private String startTime(DateTime time) {
		return ""+time.getHourOfDay();
	}

	// Turns endTime into a string with hour of day
	private String endTime(DateTime time) {
		if(time.getMinuteOfHour() == 59)
			return ""+0;
		else
			return ""+time.getHourOfDay();
	}

	// Generates a list of possible dates between start time and end time
	private String possibleDates(DateTime st, DateTime et) {
		DateTime curDate = st;
		String ret = "";
		for(int i = st.getDayOfYear(); i <= et.getDayOfYear(); i++) {
			ret += curDate.getYear() + "-"+curDate.getMonthOfYear()+"-"+curDate.getDayOfMonth()+"|";
			curDate = curDate.plusDays(1);
		}
		ret = ret.substring(0, ret.length()-1);
		return ret;
	}

	// Posts all availability for this event
	public void postAllAvailability(When2MeetEvent event) throws NameAlreadyExistsException, EmptyEventException {
		_event = event;
		if (event.getUserResponse() == null) {
			throw new EmptyEventException();
		}
		
		CalendarSlots cal = event.getUserResponse();
		if(!event.userHasSubmitted()){
			String password = "";
			this.createNewUser(event, cal, password);
			event.setUserSubmitted(true);
		}
		
		ArrayList<Integer> busySlots = cal.getSlotsForAvail(Availability.busy);
		ArrayList<Integer> freeSlots = cal.getSlotsForAvail(Availability.free);
		this.postAvailability(cal, busySlots, Availability.busy);
		this.postAvailability(cal, freeSlots, Availability.free);
	}

	// Posts a new event
	public String postNewEvent(String name, DateTime st, DateTime et) {
		ArrayList<KeyValue> keyValues = new ArrayList<KeyValue>();
		keyValues.add(new KeyValue("NewEventName", name));
		keyValues.add(new KeyValue("DateTypes", "SpecificDates"));
		keyValues.add(new KeyValue("PossibleDates", possibleDates(st, et)));
		keyValues.add(new KeyValue("NoEarlierThan", startTime(st)));
		keyValues.add(new KeyValue("NoLaterThan", endTime(et)));
		String toParse = this.post(keyValues, "http://www.when2meet.com/SaveNewEvent.php");

		Pattern eventIDPattern = Pattern.compile("<html><body onload=\\\"window.location='/\\?(\\d+)-([a-zA-Z0-9]*)'\\\"></body></html>");
		Matcher matcher = eventIDPattern.matcher(toParse);
		if(matcher.matches()) {
			int id = Integer.parseInt(matcher.group(1));
			String second = matcher.group(2);
			
			String URL = "http://www.when2meet.com/?"+id+"-"+second;
			return URL;
		}

		return null;
	}

}
