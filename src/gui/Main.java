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

	public static Availability[][] setAvailability(){

		Availability[][] availability = {
				{Availability.free, Availability.free, Availability.free, Availability.free,
					Availability.free, Availability.free, Availability.free, Availability.free,
					Availability.free, Availability.free, Availability.free, Availability.free,
					Availability.free, Availability.free, Availability.free, Availability.free,
					Availability.free, Availability.free, Availability.free, Availability.free,
					Availability.free, Availability.free, Availability.free, Availability.free},
					{Availability.free, Availability.free, Availability.free, Availability.free,
						Availability.free, Availability.free, Availability.free, Availability.free,
						Availability.free, Availability.free, Availability.free, Availability.free,
						Availability.free, Availability.free, Availability.free, Availability.free,
						Availability.free, Availability.free, Availability.free, Availability.free,
						Availability.free, Availability.free, Availability.free, Availability.free},
						{Availability.free, Availability.free, Availability.free, Availability.free,
							Availability.free, Availability.free, Availability.free, Availability.free,
							Availability.free, Availability.free, Availability.free, Availability.free,
							Availability.free, Availability.free, Availability.free, Availability.free,
							Availability.free, Availability.free, Availability.free, Availability.free,
							Availability.free, Availability.free, Availability.free, Availability.free},
							{Availability.busy, Availability.free, Availability.busy, Availability.free,
								Availability.busy, Availability.free, Availability.free, Availability.free,
								Availability.busy, Availability.free, Availability.free, Availability.free,
								Availability.busy, Availability.free, Availability.busy, Availability.free,
								Availability.busy, Availability.free, Availability.free, Availability.free,
								Availability.busy, Availability.free, Availability.busy, Availability.free},
								{Availability.busy, Availability.free, Availability.busy, Availability.free,
									Availability.busy, Availability.free, Availability.busy, Availability.free,
									Availability.busy, Availability.free, Availability.busy, Availability.free,
									Availability.busy, Availability.free, Availability.busy, Availability.free,
									Availability.busy, Availability.free, Availability.busy, Availability.free,
									Availability.busy, Availability.free, Availability.busy, Availability.free},
									{Availability.busy, Availability.free, Availability.busy, Availability.free,
										Availability.busy, Availability.free, Availability.busy, Availability.free,
										Availability.busy, Availability.free, Availability.busy, Availability.free,
										Availability.busy, Availability.free, Availability.busy, Availability.free,
										Availability.busy, Availability.free, Availability.busy, Availability.free,
										Availability.busy, Availability.free, Availability.busy, Availability.free},
										{Availability.busy, Availability.free, Availability.busy, Availability.free,
											Availability.busy, Availability.free, Availability.busy, Availability.free,
											Availability.busy, Availability.free, Availability.busy, Availability.free,
											Availability.busy, Availability.free, Availability.busy, Availability.free,
											Availability.busy, Availability.free, Availability.busy, Availability.free,
											Availability.busy, Availability.free, Availability.busy, Availability.free}};
		return availability;
	}

	public static void main(String[] args) {

		GoogleCalendars g = new GoogleCalendars(new DateTime(2012,4,16,9,0), new DateTime(2012,4,22,15,0), new OwnerImpl("Tim"));
		CalendarResponses responses = new CalendarResponses(new DateTime(2012,4,16,9,0), new DateTime(2012,4,22,15,0), "test");
		responses.addResponse(new Response(new DateTime(2012,4,16,9,0), new DateTime(2012,4,16,15,0), "Fishing"));
		responses.addResponse(new Response(new DateTime(2012,4,17,10,0), new DateTime(2012,4,17,12,0), "Swimming"));
		responses.addResponse(new Response(new DateTime(2012,4,18,9,0), new DateTime(2012,4,18,15,0), "thing1"));
		responses.addResponse(new Response(new DateTime(2012,4,18,10,0), new DateTime(2012,4,18,12,0), "thing2"));
		responses.addResponse(new Response(new DateTime(2012,4,18,12,0), new DateTime(2012,4,18,15,0), "thing3"));
		responses.addResponse(new Response(new DateTime(2012,4,18,11,0), new DateTime(2012,4,18,14,0), "thing4"));
		responses.addResponse(new Response(new DateTime(2012,4,19,9,0), new DateTime(2012,4,19,15,0), "test"));
		responses.addResponse(new Response(new DateTime(2012,4,20,10,0), new DateTime(2012,4,20,12,0), "test"));
		responses.addResponse(new Response(new DateTime(2012,4,21,9,0), new DateTime(2012,4,21,14,0), "test"));		
		
		g.addCalendar(responses);

		ArrayList<CalendarSlots> slotsCals = new ArrayList<CalendarSlots>();
		Availability[][] availability = setAvailability();
		slotsCals.add(new CalendarSlots(new DateTime(2012,4,16,9,0), new DateTime(2012,4,22,15,0), new When2MeetOwner("Tim", 0), 15, availability));
		When2MeetEvent w = new When2MeetEvent(new DateTime(2012,4,16,9,0), new DateTime(2012,4,22,15,0), "test", 0, "test_url", slotsCals, null);

		CalendarGui gui = new CalendarGui(g, w);

		//		e.addCalendar(new CalendarResponses(new DateTime(2012,3,5,9,0), new DateTime(2012,3,11,22,0), "Hamlet"));
		//		e.getCalendars().get(0).addResponse(new Response (new DateTime(2012,3,6,12,0), new DateTime(2012,3,6,16,0)));
		//		e.getCalendars().get(0).addResponse(new Response (new DateTime(2012,3,8,12,15), new DateTime(2012,3,8,16,45)));
		//		e.getCalendars().get(0).addResponse(new Response (new DateTime(2012,3,7,11,30), new DateTime(2012,3,7,14,30)));
		//		CalendarGui g = new CalendarGui(e);
		//		g.build();
	}

}
