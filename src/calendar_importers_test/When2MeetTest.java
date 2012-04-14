package calendar_importers_test;
import java.io.IOException;
import java.util.ArrayList;

import calendar.Availability;
import calendar.CalendarGroup;
import calendar.CalendarSlots;
import calendar.When2MeetEvent;
import calendar_exporters.When2MeetExporter;
import calendar_importers.When2MeetImporter;

import ftf.TimeFinderSlots;

public class When2MeetTest {
	public static void main(String[] args) throws IOException {
		//String str = JOptionPane.showInputDialog(null, "Enter When2Meet URL: ", 
		//		"http://www.when2meet.com/?353066-BlwWl", 1);
		//String url = "http://www.when2meet.com/?353066-BlwWl";
		When2MeetImporter wtmi = new When2MeetImporter("http://www.when2meet.com/?353066-BlwWl");
		
		When2MeetEvent w2me = wtmi.importCalendarGroup();
		
		//wtmi = new When2MeetImporter("http://www.when2meet.com/?408906-BySEr");
		//w2me = wtmi.importCalendarGroup();
		
		CalendarSlots cal1 = (CalendarSlots) w2me.getCalendars().get(0);
		for(int i = 0; i < w2me.getCalendars().size(); i++) { 
			if(w2me.getCalendars().get(i).getOwner().getName().equals("Jeanette")) {
				cal1 = w2me.getCalendars().get(i);
				break;
			}
		}
		System.out.println("Name: " + cal1.getOwner().getName());
		
		When2MeetExporter exporter = new When2MeetExporter(w2me, cal1);
		ArrayList<Integer> slotIDs = new ArrayList<Integer>();
		slotIDs.add(4);
		slotIDs.add(5);
		slotIDs.add(6);
		slotIDs.add(7);
		
		exporter.updateAvailability(slotIDs,  Availability.busy);
		System.out.println("After updating availability");
		
/*
		TimeFinderSlots timeFind = new TimeFinderSlots();
		CalendarSlots times = timeFind.findBestTimes(w2me, 15, 60, 20, 4);
		
		times.print();
		*/
		
	}
}
