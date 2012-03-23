package calendar_importers;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import calendar.CalendarGroup;

public class When2MeetImporter implements CalendarsImporter {
	private URL _url;
	private HashMap<Integer, String> _IDsToNames = new HashMap<Integer, String>();
	private HashMap<Integer, ArrayList<Integer>> _slotToIDs = new HashMap<Integer, ArrayList<Integer>>();
	private Pattern _namePattern = Pattern.compile("PeopleNames\\[[\\d+]\\] = '(\\w+)'");
	private Pattern _idPattern = Pattern.compile("PeopleIDs\\[[\\d+]\\] = (\\d+)");
	private Pattern _availPattern = Pattern.compile("AvailableAtSlot\\[(\\d+)\\]\\.push\\((\\d+)\\);"); 
	
	public When2MeetImporter(String url) throws IOException {
			_url = new URL(url);
		
			BufferedReader page = new BufferedReader(new InputStreamReader(_url.openStream()));
			
			String inputLine;
			while((inputLine = page.readLine()) != null) {
				Matcher availMatcher = _availPattern.matcher(inputLine);
				if(availMatcher.find())
					addAvail(inputLine, availMatcher);
				if(inputLine.contains("PeopleIDs")) {
					parseNamesToIDs(inputLine);	}
			}
			printAvail();
	}
	
	private void parseNamesToIDs(String str) {
		String[] byPerson = str.split(";");
		boolean nameMatched = false;
		boolean idMatched = false;
		String name = "";
		int id = 0;	
		
		for(int i = 0; i < byPerson.length; i++) {
			System.out.println("ByPerson: " + byPerson[i]); 
			Matcher nameMatcher = _namePattern.matcher(byPerson[i]);
			Matcher idMatcher = _idPattern.matcher(byPerson[i]);
			
			if(nameMatcher.matches()) {
				System.out.println("Name matched: " + nameMatcher.group(1));
				name = nameMatcher.group(1);
				nameMatched = true;
			} else if(idMatcher.matches()) {
				System.out.println("ID matched: " + idMatcher.group(1));
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
	
	@Override
	public CalendarGroup importCalendarGroup() {
		// TODO Auto-generated method stub
		return null;
	}

}
