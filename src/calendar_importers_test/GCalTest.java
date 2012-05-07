package calendar_importers_test;

import java.io.IOException;
import java.net.URISyntaxException;

import calendar_importers.GCalImporter;

import com.google.gdata.util.ServiceException;

/*
 * Tested cases (involved creating dummy events/calendars in Google calendars and running below main):
 * 		Insert two GCals with the same name, handled because track cals with IDs
 * 		Add/delete calendars within your Google calendar, added check for this
 * 		Disconnect from the Internet: checks for Internet connection, only imports if connected
 * 		Importing all day events, all day events not imported because you usually aren't actually busy the 
 * 				entire day (i.e. 4th of July) and you usually don't want to respond to a When2Meet based on an
 * 				all day event
 * 		Importing events that start or end at midnight, handled
 * 		Overlapping events
 * 		Tested both with importing calendars that already existed and importing when nothing has been imported
 * 		Handled by Google: invalid input into username/password for authentication
 */

public class GCalTest {
	//TEST
	//note: un-comment line 183 in GCalImporter to print out calendars
    public static void main(String[] args) throws IOException, ServiceException, com.google.gdata.util.ServiceException, URISyntaxException {
    	GCalImporter myImporter = new GCalImporter();
    	org.joda.time.DateTime startTime = new org.joda.time.DateTime(2012, 4, 20, 8, 0);
		org.joda.time.DateTime endTime = new org.joda.time.DateTime(2012, 4, 29, 23, 0);
    	myImporter.importMyGCal(startTime, endTime);
    	myImporter.refresh(startTime, endTime);
    }
}
