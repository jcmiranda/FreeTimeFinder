package calendar_importers_test;
import java.io.IOException;

import calendar.CalendarGroup;
import calendar.CalendarSlotsImpl;
import calendar_importers.When2MeetImporterSlots;
import ftf.TimeFinderSlots;

public class When2MeetTest {
	public static void main(String[] args) throws IOException {
		//String str = JOptionPane.showInputDialog(null, "Enter When2Meet URL: ", 
		//		"http://www.when2meet.com/?353066-BlwWl", 1);
		//String url = "http://www.when2meet.com/?353066-BlwWl";
		When2MeetImporterSlots wtmi = new When2MeetImporterSlots("http://www.when2meet.com/?353066-BlwWl");
		CalendarGroup w2me = wtmi.importFresh();
		

		TimeFinderSlots timeFind = new TimeFinderSlots();
		CalendarSlotsImpl times = timeFind.findBestTimes(w2me, 15, 60, 20, 4);
		
		times.print();
		
		
	}
}
