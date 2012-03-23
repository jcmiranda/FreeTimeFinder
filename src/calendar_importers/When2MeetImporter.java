package calendar_importers;

import calendar.When2MeetEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import calendar.CalendarGroup;

public class When2MeetImporter implements CalendarsImporter {
	private URL _url;
	private HashMap<Integer, String> _IDsToNames = new HashMap<Integer, String>();
	private HashMap<Integer, ArrayList<Integer>> _slotToIDs = new HashMap<Integer, ArrayList<Integer>>();
	private HashMap<String, Integer> _months = new HashMap<String, Integer>();
	private Pattern _namePattern = Pattern.compile("PeopleNames\\[[\\d+]\\] = '(\\w+)'");
	private Pattern _idPattern = Pattern.compile("PeopleIDs\\[[\\d+]\\] = (\\d+)");
	private Pattern _availPattern = Pattern.compile("AvailableAtSlot\\[(\\d+)\\]\\.push\\((\\d+)\\);"); 
	private Pattern _datesPattern = Pattern.compile("text\\-align:center;font\\-size:10px;width:44px;padding\\-right:1px;\">(\\w+) (\\d+)<br>");
	private Pattern _timesPattern = Pattern.compile("width:(\\d+)px;font\\-size:(\\d+)px;margin:(\\d+)px (\\d+)px (\\d+)px (\\d+)px;'>(\\d*)(\\s*)(\\w+)&nbsp");
	private int _timeIndex = 7;
	private int _AMPMIndex = 9;
	private LocalTime _st;
	private LocalTime _et;
	private LocalDate _sd;
	private LocalDate _ed;
	private int _year = 2012;
	
	public When2MeetImporter(String url) throws IOException {
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
	
	private void addTime(Matcher m) {
		int time = 0; 
		if(m.group(_AMPMIndex).equals("Noon"))
			time = 12;
		else if(m.group(_AMPMIndex).equals("Midnight")) {
			if(_st == null)
				time = 0;
			else
				time = 24;
		} else
			time = Integer.parseInt(m.group(_timeIndex));
		
		if(m.group(_AMPMIndex).equals("PM"))
			time += 12;
		
		LocalTime thisTime;
		if(time == 24) 
			thisTime = new LocalTime(time-1, 59);
		else
			thisTime = new LocalTime(time, 0);
		
		if(_st == null)
			_st = thisTime;
		else
			_et = thisTime;
	}
	
	private void addDates(Matcher m) {
		do {
			String month = m.group(1);
			int day = Integer.parseInt(m.group(2));
			LocalDate thisDate = new LocalDate(_year, _months.get(month), day);
			if(_sd == null) {
				_sd = thisDate;
				_ed = thisDate;
			} else if (thisDate.compareTo(_sd) < 0) {
				_sd = thisDate;
			} else if (thisDate.compareTo(_ed) > 0) {
				_ed = thisDate;
			}
		} while(m.find());
	}
	
	private void parseNamesToIDs(String str) {
		String[] byPerson = str.split(";");
		boolean nameMatched = false;
		boolean idMatched = false;
		String name = "";
		int id = 0;	
		
		for(int i = 0; i < byPerson.length; i++) {
			Matcher nameMatcher = _namePattern.matcher(byPerson[i]);
			Matcher idMatcher = _idPattern.matcher(byPerson[i]);
			
			if(nameMatcher.matches()) {
				name = nameMatcher.group(1);
				nameMatched = true;
			} else if(idMatcher.matches()) {
				id = Integer.parseInt(idMatcher.group(1));
				idMatched = true;
			}
			
			if(nameMatched && idMatched) {
				_IDsToNames.put(new Integer(id), name);
				nameMatched = false;
				idMatched = false;
			}
		}
	
	}
	
	private void addAvail(String str, Matcher availMatcher) {
		System.out.println("adding availability");
		do {
			Integer slot = new Integer(Integer.parseInt(availMatcher.group(1)));
			Integer id = new Integer(Integer.parseInt(availMatcher.group(2)));
			if(_slotToIDs.containsKey(slot)) {
				_slotToIDs.get(slot).add(id);
			} else {
				ArrayList<Integer> idList = new ArrayList<Integer>();
				idList.add(id);
				_slotToIDs.put(slot, idList);
			}
		} while(availMatcher.find());
	}
	
	private void printAvail() {
		for(Integer key : _slotToIDs.keySet()) {
			System.out.print("Slot: " + key + " Names: ");
			for(Integer id : _slotToIDs.get(key)) {
				System.out.print(_IDsToNames.get(id) + " ");
			}
			System.out.println();
		}
	}

	private void parseHTML() throws IOException {
		BufferedReader page = new BufferedReader(new InputStreamReader(_url.openStream()));
		
		String inputLine;
		while((inputLine = page.readLine()) != null) {
			Matcher availMatcher = _availPattern.matcher(inputLine);
			Matcher dateMatcher = _datesPattern.matcher(inputLine);
			Matcher timeMatcher = _timesPattern.matcher(inputLine);
			
			while(timeMatcher.find()) {
				addTime(timeMatcher);
			}
			
			if(dateMatcher.find()) {
				addDates(dateMatcher);
			}
			if(availMatcher.find())
				addAvail(inputLine, availMatcher);
			if(inputLine.substring(0, Math.min(100, inputLine.length())).contains("PeopleIDs")) {
				parseNamesToIDs(inputLine);	}
		}
	}
	
	@Override
	public CalendarGroup importFresh() {
		try {
			parseHTML();
		} catch (IOException e) {
			e.printStackTrace();
		}
		CalendarGroup w2me = new When2MeetEvent(_st, _et, _sd, _ed);
		
		// TODO Auto-generated method stub
		return w2me;
	}

	@Override
	public void updateCalGrp(CalendarGroup cg) {
		// TODO Auto-generated method stub
		
	}

}
