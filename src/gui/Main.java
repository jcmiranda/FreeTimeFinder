package gui;

import org.joda.time.DateTime;

import calendar.CalendarImpl;
import calendar.Response;
import calendar.When2MeetEvent;

public class Main {

	public static void main(String[] args) {

		When2MeetEvent e = new When2MeetEvent(new DateTime(2012,3,5,9,0), new DateTime(2012,3,11,22,0));
		e.addCalendar(new CalendarImpl(new DateTime(2012,3,5,9,0), new DateTime(2012,3,11,22,0), "Hamlet"));
		e.getCalendars().get(0).addResponse(new Response (new DateTime(2012,3,6,12,0), new DateTime(2012,3,6,16,0)));
		e.getCalendars().get(0).addResponse(new Response (new DateTime(2012,3,8,12,15), new DateTime(2012,3,8,16,45)));
		e.getCalendars().get(0).addResponse(new Response (new DateTime(2012,3,7,11,30), new DateTime(2012,3,7,14,30)));
		CalendarGui g = new CalendarGui(e);
		g.build();

	}

}
