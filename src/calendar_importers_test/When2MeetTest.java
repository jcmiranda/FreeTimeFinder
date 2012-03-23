package calendar_importers_test;
import java.io.IOException;

import javax.swing.JOptionPane;

import calendar.CalendarGroup;
import calendar.Response;
import calendar.When2MeetEvent;
import calendar_importers.When2MeetImporter;
import ftf.TimeFinder;

public class When2MeetTest {
	public static void main(String[] args) throws IOException {
		//String str = JOptionPane.showInputDialog(null, "Enter When2Meet URL: ", 
		//		"http://www.when2meet.com/?353066-BlwWl", 1);
		//String url = "http://www.when2meet.com/?353066-BlwWl";
		When2MeetImporter wtmi = new When2MeetImporter("when2meet.html");
		CalendarGroup w2me = wtmi.importFresh();
			
		TimeFinder timeFind = new TimeFinder();
		Response[] times = timeFind.findBestTimes(w2me, 15, 60, 20, 4);
		
		for(int i = 0; i < times.length; i++) {
			times[i].print();
		}
		
	}
}
