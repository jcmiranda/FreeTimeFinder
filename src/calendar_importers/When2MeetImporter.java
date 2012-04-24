package calendar_importers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import calendar.Availability;
import calendar.CalendarGroup;
import calendar.CalendarSlots;
import calendar.OwnerImpl;
import calendar.When2MeetEvent;
import calendar.When2MeetOwner;


public class When2MeetImporter implements CalendarsImporter {

	private String _urlString = null, _eventName = null;
	private int _eventID;
	private HashMap<Integer, CalendarSlots> _IDsToCals = new HashMap<Integer, CalendarSlots>();
	private HashMap<String, Integer> _months = new HashMap<String, Integer>();
	private ArrayList<Integer> _slotIndexToSlotID = new ArrayList<Integer>();
	
	private Pattern _nameIDPattern = Pattern.compile("PeopleNames\\[(\\d+)\\] = '([\\w+\\s*]+)';PeopleIDs\\[(\\d+)\\] = (\\d+);");
	private int _nameGroupIndex = 2, _IDGroupIndex = 4;
	private Pattern _slotsPattern = Pattern.compile("TimeOfSlot\\[(\\d+)\\]=(\\d+);");
	private int _slotIDGroupIndex = 2;
	private Pattern _availPattern = Pattern.compile("AvailableAtSlot\\[(\\d+)\\]\\.push\\((\\d+)\\);"); 
	private Pattern _datesPattern = Pattern.compile("text\\-align:center;font\\-size:10px;width:44px;padding\\-right:1px;\">(\\w+) (\\d+)<br>");
	private Pattern _eventIDPattern = Pattern.compile("http://www.when2meet.com/\\?(\\d+)\\-[a-zA-Z]+");
	private Pattern _eventNameDivPattern = Pattern.compile("<div id=\\\"NewEventNameDiv\\\" style=\\\"padding:20px 0px 20px 20px;font-size:30px;\\\">");
	private Pattern _eventNamePattern = Pattern.compile("(.*)<br><span style=\\\"font-size: 12px;\\\">");
	private Pattern _timesPattern = Pattern.compile("width:44px;font\\-size:10px;margin:4px 4px 0px 0px;'>(\\d*)(\\s*)(\\w+)&nbsp");
	private int _timeIndex = 1;
	private int _AMPMIndex = 3;
	private DateTime _startTime, _endTime;
	private int _minInSlot = 15; // Minutes in a time slot
	private int _year = 2012;
	
	public When2MeetImporter() {
			initializeMonths();		
	}
	
	private void initializeMonths() {
		_months.put("Jan", 1);
		_months.put("Feb", 2);
		_months.put("Mar", 3);
		_months.put("Apr", 4);
		_months.put("May", 5);
		_months.put("Jun", 6);
		_months.put("July", 7);
		_months.put("Aug", 8);
		_months.put("Sept", 9);
		_months.put("Oct", 10);
		_months.put("Nov", 11);
		_months.put("Dec", 12);
	}
	
	private void parseEventID() {
		Matcher eventIDMatcher = _eventIDPattern.matcher(_urlString);
		if(eventIDMatcher.find()) {
			_eventID = Integer.parseInt(eventIDMatcher.group(1));
		} else {
			// TODO handle properly
			System.err.println("Error parsing event id");
			System.exit(1);
		}
	}
	
	private void initCalendars(ArrayList<String> nameIDPairs) {
		for(String s : nameIDPairs) {
			Matcher nameIDMatcher = _nameIDPattern.matcher(s);
			String name = "";
			int id = 0;	

			while(nameIDMatcher.find()) {
				name = nameIDMatcher.group(_nameGroupIndex);
				id = Integer.parseInt(nameIDMatcher.group(_IDGroupIndex));

				if(_IDsToCals.containsKey(id)) {
					System.err.println("cal already found for id");
					System.exit(1);
				} else {
					CalendarSlots cal = new CalendarSlots(_startTime, _endTime, _minInSlot, Availability.busy);
					cal.setOwner(new When2MeetOwner(name, id));
					_IDsToCals.put(id, cal);
				}
			}
		}
	}
	
	private void parseAvailability(ArrayList<String> availLines) {
		for(String s : availLines) {
			Matcher m = _availPattern.matcher(s);
			
			while(m.find()) {
				Integer slot = new Integer(Integer.parseInt(m.group(1)));
				Integer id = new Integer(Integer.parseInt(m.group(2)));
				// System.out.println("Slot: " + slot + " ID: " + id);
				assert(slot != null);
				assert(id != null);
				if(_IDsToCals.get(id) == null) {
					System.out.println(id);
					System.out.println(_IDsToCals.keySet());
				}
				_IDsToCals.get(id).setAvail(slot, Availability.free);		
			}
		}
	}
	
	private void parseHTML() throws IOException, MalformedURLException {
		// BufferedReader page = new BufferedReader(new InputStreamReader(new FileInputStream(_urlString)));
		//TODO error handling
		URL url = new URL(_urlString);
		BufferedReader page = new BufferedReader(new InputStreamReader(url.openStream()));
		
		ArrayList<String> nameIDLines = new ArrayList<String>();
		ArrayList<String> availLines = new ArrayList<String>();
		ArrayList<String> dateLines = new ArrayList<String>();
		ArrayList<String> timeLines = new ArrayList<String>();

		String inputLine;
		boolean nextIsEventName = false;
		
		while((inputLine = page.readLine()) != null) {
			Matcher availMatcher = _availPattern.matcher(inputLine);
			Matcher dateMatcher = _datesPattern.matcher(inputLine);
			Matcher timeMatcher = _timesPattern.matcher(inputLine);
			Matcher nameIDMatcher = _nameIDPattern.matcher(inputLine);
			Matcher eventNameDivMatcher = _eventNameDivPattern.matcher(inputLine);
			Matcher slotsMatcher = _slotsPattern.matcher(inputLine);
			
			if(nextIsEventName) {
				Matcher eventNameMatcher = _eventNamePattern.matcher(inputLine);
				if(eventNameMatcher.find()) {
					_eventName = eventNameMatcher.group(1);
				}
				nextIsEventName = false;
			}
			
			if(timeMatcher.find())
				timeLines.add(inputLine);
			if(dateMatcher.find())
				dateLines.add(inputLine);
			if(availMatcher.find())
				availLines.add(inputLine);
			if(nameIDMatcher.find()) {
				nameIDLines.add(inputLine);
				System.out.println(inputLine);
			}
			if(slotsMatcher.matches()) {
				int slotID = Integer.parseInt(slotsMatcher.group(_slotIDGroupIndex));
				_slotIndexToSlotID.add(slotID);
			}
				
			if(eventNameDivMatcher.matches()) 
				nextIsEventName = true;
			
		}
		
		parseEventID();
		parseStartEndTime(dateLines, timeLines);
		initCalendars(nameIDLines);
		parseAvailability(availLines);
		
	}
	
	private void parseStartEndTime(ArrayList<String> dateLines, ArrayList<String> timeLines) {
		LocalDate startDate = null;
		LocalDate endDate = null;
		for(String s : dateLines) {
			Matcher m =_datesPattern.matcher(s);
			while(m.find()) {
				String month = m.group(1);
				int day = Integer.parseInt(m.group(2));
				LocalDate thisDate = new LocalDate(_year, _months.get(month), day);
				if(startDate == null) {
					startDate = thisDate;
					endDate = thisDate;
				} else if (thisDate.compareTo(startDate) < 0) {
					startDate = thisDate;
				} else if (thisDate.compareTo(endDate) > 0) {
					endDate = thisDate;
				}
			}
		}
		
		int startHour = -1;
		int endHour = -1;
		for(String s : timeLines) {
			Matcher m = _timesPattern.matcher(s);
			while(m.find()) {
				int hour = 0; 
				if(m.group(_AMPMIndex).equals("Noon"))
					hour = 12;
				else if(m.group(_AMPMIndex).equals("Midnight")) {
					hour = 0;
				} else
					hour = Integer.parseInt(m.group(_timeIndex));

				if(m.group(_AMPMIndex).equals("PM"))
					hour += 12;

				if(startHour == -1)
					startHour = hour;
				endHour = hour;
			}
		}
		
		int minutes = 0;
		if(endHour == 0) {
			endHour = 23;
			minutes = 59;
		}
		
		_startTime = makeDateTime(startDate, startHour, 0);
		_endTime = makeDateTime(endDate, endHour, minutes);
			
	}
	
	private DateTime makeDateTime(LocalDate date, int hours, int minutes) {
		return new DateTime(_year, date.getMonthOfYear(), 
				date.getDayOfMonth(), hours, minutes);
	}
	
	
	@Override
	public When2MeetEvent importCalendarGroup(String url) throws MalformedURLException{
		_urlString = url;
		try {
			parseHTML();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Event ID: " + _eventID);
		System.out.println("Event Name: " + _eventName);
		When2MeetEvent w2me = new When2MeetEvent(_startTime, _endTime, _eventName, 
				_eventID, _urlString, _IDsToCals.values(), _slotIndexToSlotID);

		return w2me;
	}

}
