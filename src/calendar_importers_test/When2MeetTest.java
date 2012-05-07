package calendar_importers_test;
import java.io.IOException;
import java.util.HashMap;

import calendar.CalendarSlots;
import calendar.Event.CalByThatNameNotFoundException;
import calendar.When2MeetEvent;
import calendar_exporters.When2MeetExporter.NameAlreadyExistsException;
import calendar_importers.When2MeetImporter;

public class When2MeetTest {
	
	private static void testImport(When2MeetImporter wtmi, String url, String name, HashMap<String, Integer> expectedRespondees, int eventID) throws IOException {
			
		When2MeetEvent event = wtmi.importNewEvent(url);
		System.out.println("TESTING " + event.getName());
		// Check the name of the event
		assert(event.getName().equals(name));
		// Check the ID of the event
		assert(event.getID() == eventID);

		// Build a list of all of the imported names
		HashMap<String, Integer> importedNames = new HashMap<String, Integer>();
		for(CalendarSlots cal : event.getCalendars()) {
			importedNames.put(cal.getOwner().getName(), cal.getOwner().getID());
		}

		// Check that the imported names all correspond to the expected names
		for(String respondent : expectedRespondees.keySet()) {
			// Make sure that we have a response for this person
			assert(importedNames.keySet().contains(respondent));
			
			// Make sure that the ID matches
			assert(importedNames.get(respondent).equals(expectedRespondees.get(respondent)));
		}
		System.out.println("PASSED");
	}
	
	public static void main(String[] args) throws IOException, NameAlreadyExistsException, CalByThatNameNotFoundException {
		When2MeetImporter wtmi = new When2MeetImporter(); 
		
		// Test 1 - 2 users, 3 days long
		HashMap<String, Integer> expRespondees = new HashMap<String, Integer>();
		expRespondees.put("User1", 1872671);
		expRespondees.put("User2", 1872676);
		testImport(wtmi, "http://www.when2meet.com/?438771-KGv1R", "Test1", expRespondees, 438771);
		
		
		// Test 2 - multi-word name, users with spaces in their names, to midnight
		expRespondees.clear();
		expRespondees.put("User 1", 1872681);
		expRespondees.put("User 2", 1872686);
		testImport(wtmi, "http://www.when2meet.com/?438776-5kHqQ", "NewTest Event Name", expRespondees, 438776);
		
		
	}
}
