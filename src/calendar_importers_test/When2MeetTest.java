package calendar_importers_test;
import java.io.IOException;
import java.util.HashMap;

import org.joda.time.DateTime;

import calendar.CalendarSlots;
import calendar.Event.CalByThatNameNotFoundException;
import calendar.When2MeetEvent;
import calendar_exporters.When2MeetExporter.NameAlreadyExistsException;
import calendar_importers.When2MeetImporter;

// Testing for importing of when2meets
// Checks for start time, end time (corners to give start date and start time of day
// together)
// Respondees have correct names  and user ids
// Event has correct ID and name

public class When2MeetTest {
	
	private static void testImport(When2MeetImporter wtmi, String url, DateTime expStartTime, DateTime expEndTime,
			String expName, HashMap<String, Integer> expectedRespondees, int expEventID) throws IOException {
			
		When2MeetEvent event = wtmi.importNewEvent(url);
		System.out.println("TESTING " + event.getName());
		// Check the name of the event
		assert(event.getName().equals(expName));
		// Check the ID of the event
		assert(event.getID() == expEventID);

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
		DateTime startTime = new DateTime(2012, 5, 19, 9, 0);
		DateTime endTime = new DateTime(2012, 5, 21, 5, 0);
		testImport(wtmi, "http://www.when2meet.com/?438771-KGv1R", startTime,
				endTime, "Test1", expRespondees, 438771);
		
		
		// NewTest Event Name - multi-word name, users with spaces in their names, to midnight
		expRespondees.clear();
		expRespondees.put("User 1", 1872681);
		expRespondees.put("User 2", 1872686);
		startTime = new DateTime(2012, 5, 6, 9, 0);
		endTime = new DateTime(2012, 5, 9, 11, 59);
		testImport(wtmi, "http://www.when2meet.com/?438776-5kHqQ", startTime, endTime,
				"NewTest Event Name", expRespondees, 438776);
		
		// 
		
	}
}
