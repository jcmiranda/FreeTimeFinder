package gui;

import java.util.ArrayList;

import org.joda.time.DateTime;

import calendar.CalendarResponses;
import calendar.GoogleCalendars;
import calendar.CalendarSlots;
import calendar.OwnerImpl;
import calendar.Response;
import calendar.When2MeetEvent;
import calendar.Availability;
import calendar.When2MeetOwner;


public class Main {

	
	public static void main(String[] args) {

		
		System.out.println("Starting main");

		CalendarGui gui = new CalendarGui(); //g, w);

		//		e.addCalendar(new CalendarResponses(new DateTime(2012,3,5,9,0), new DateTime(2012,3,11,22,0), "Hamlet"));
		//		e.getCalendars().get(0).addResponse(new Response (new DateTime(2012,3,6,12,0), new DateTime(2012,3,6,16,0)));
		//		e.getCalendars().get(0).addResponse(new Response (new DateTime(2012,3,8,12,15), new DateTime(2012,3,8,16,45)));
		//		e.getCalendars().get(0).addResponse(new Response (new DateTime(2012,3,7,11,30), new DateTime(2012,3,7,14,30)));
		//		CalendarGui g = new CalendarGui(e);
		//		g.build();
	}

}
