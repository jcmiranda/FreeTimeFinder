package calendar_importers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import calendar.CalendarGroup;
import calendar.CalendarSlotsImpl;
import calendar.OwnerImpl;
import calendar.When2MeetEvent;
import calendar.CalendarSlots.CalSlotsFB;

public class When2MeetImporterSlots implements CalendarsImporter {

	private URL _url;
	private HashMap<Integer, CalendarSlotsImpl> _IDsToCals = new HashMap<Integer, CalendarSlotsImpl>();
	private HashMap<String, Integer> _months = new HashMap<String, Integer>();
	
	private Pattern _nameIDPattern = Pattern.compile("PeopleNames\\[[\\d+]\\] = '(\\w+)';PeopleIDs\\[[\\d+]\\] = (\\d+);");
	private Pattern _availPattern = Pattern.compile("AvailableAtSlot\\[(\\d+)\\]\\.push\\((\\d+)\\);"); 
	private Pattern _datesPattern = Pattern.compile("text\\-align:center;font\\-size:10px;width:44px;padding\\-right:1px;\">(\\w+) (\\d+)<br>");
	//private Pattern _timesPattern = Pattern.compile("<div style='text-align:right;width:44px;font-size:10px;margin:4px 4px 0px 0px;'>(.*)&nbsp;");
	private Pattern _timesPattern = Pattern.compile("width:44px;font\\-size:10px;margin:4px 4px 0px 0px;'>(\\d*)(\\s*)(\\w+)&nbsp");
	private int _timeIndex = 1;
	private int _AMPMIndex = 3;
	private DateTime _startTime, _endTime;
	private int _minInSlot = 15; // Minutes in a time slot
	private int _year = 2012;
	
	public When2MeetImporterSlots(String url) throws IOException {
			_url = new URL(url);
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
	
	private void initCalendars(ArrayList<String> nameIDPairs) {
		for(String s : nameIDPairs) {
			Matcher nameIDMatcher = _nameIDPattern.matcher(s);
			String name = "";
			int id = 0;	

			while(nameIDMatcher.find()) {
				name = nameIDMatcher.group(1);
				id = Integer.parseInt(nameIDMatcher.group(2));

				if(_IDsToCals.containsKey(id)) {
					System.err.println("cal already found for id");
					System.exit(1);
				} else {
					CalendarSlotsImpl cal = new CalendarSlotsImpl(_startTime, _endTime, _minInSlot, CalSlotsFB.busy);
					cal.setOwner(new OwnerImpl(name));
					_IDsToCals.put(id, cal);
				}
			}
		}
	}
	
	private void addAvailability(ArrayList<String> availLines) {
		for(String s : availLines) {
			Matcher m = _availPattern.matcher(s);
			
			while(m.find()) {
				Integer slot = new Integer(Integer.parseInt(m.group(1)));
				Integer id = new Integer(Integer.parseInt(m.group(2)));		
				_IDsToCals.get(id).setAvail(slot, CalSlotsFB.free);		
			}
		}
	}
	
	private void parseHTML() throws IOException {
		// BufferedReader page = new BufferedReader(new InputStreamReader(new FileInputStream(_urlString)));
		BufferedReader page = new BufferedReader(new InputStreamReader(_url.openStream()));
		
		ArrayList<String> nameIDLines = new ArrayList<String>();
		ArrayList<String> availLines = new ArrayList<String>();
		ArrayList<String> dateLines = new ArrayList<String>();
		ArrayList<String> timeLines = new ArrayList<String>();

		String inputLine;
		while((inputLine = page.readLine()) != null) {
			Matcher availMatcher = _availPattern.matcher(inputLine);
			Matcher dateMatcher = _datesPattern.matcher(inputLine);
			Matcher timeMatcher = _timesPattern.matcher(inputLine);
			Matcher nameIDMatcher = _nameIDPattern.matcher(inputLine);

			if(timeMatcher.find())
				timeLines.add(inputLine);
			if(dateMatcher.find())
				dateLines.add(inputLine);
			if(availMatcher.find())
				availLines.add(inputLine);
			if(nameIDMatcher.find())
				nameIDLines.add(inputLine);
		}
		
		setStartEndTime(dateLines, timeLines);
		initCalendars(nameIDLines);
		addAvailability(availLines);
		
	}
	
	private void setStartEndTime(ArrayList<String> dateLines, ArrayList<String> timeLines) {
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
		
		if(endHour == 0)
			endHour = 24;
		
		System.out.println("Start Hour: " + startHour + "End Hour:" + endHour);
		
		_startTime = makeDateTime(startDate, startHour);
		_endTime = makeDateTime(endDate, endHour);
			
	}
	
	private DateTime makeDateTime(LocalDate date, int hours) {
		return new DateTime(_year, date.getMonthOfYear(), 
				date.getDayOfMonth(), 0, 0).plusHours(hours);
	}
	
	
	@Override
	public CalendarGroup importFresh() {
		try {
			parseHTML();
		} catch (IOException e) {
			e.printStackTrace();
		}
		CalendarGroup w2me = new When2MeetEvent(_startTime, _endTime);

		for(int id : _IDsToCals.keySet()) {
			System.out.println("======================================");
			System.out.println(_IDsToCals.get(id).getOwner().getName());
			_IDsToCals.get(id).print();
			// Invert
			w2me.addCalendar(_IDsToCals.get(id));
		}
		
		
		// TODO Auto-generated method stub
		return w2me;
	}

	@Override
	public void updateCalGrp(CalendarGroup cg) {
		// TODO Auto-generated method stub

	}

}
