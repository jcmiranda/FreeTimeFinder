package calendar_importers_test;
import java.io.IOException;

import calendar.CalendarGroup;
import calendar.CalendarSlots;
import calendar_importers.When2MeetImporter;

import ftf.TimeFinderSlots;

public class When2MeetTest {
	public static void main(String[] args) throws IOException {
		//String str = JOptionPane.showInputDialog(null, "Enter When2Meet URL: ", 
		//		"http://www.when2meet.com/?353066-BlwWl", 1);
		//String url = "http://www.when2meet.com/?353066-BlwWl";
		When2MeetImporter wtmi = new When2MeetImporter("http://www.when2meet.com/?353066-BlwWl");
		CalendarGroup w2me = wtmi.importCalendarGroup();
		

		TimeFinderSlots timeFind = new TimeFinderSlots();
		CalendarSlots times = timeFind.findBestTimes(w2me, 15, 60, 20, 4);
		
		times.print();
		
		
	}
}
