package gui;

import static gui.GuiConstants.LINE_SPACING;


import java.awt.GridLayout;
import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.Days;

import calendar.CalendarGroup;
import calendar.CalendarResponses;
import calendar.CalendarSlots;
import calendar.Response;
import calendar.Availability;

public class ReplyPanel extends CalPanel{

	private CalendarGroup<CalendarSlots> _slotCals;
	private CalendarGroup<CalendarResponses> _respCals;

	public ReplyPanel(DateTime thisMonday) {
		super(thisMonday);
		this.setLayout(new GridLayout(1,14,LINE_SPACING,0));
	}
	public ReplyPanel(DateTime thisMonday,
			CalendarGroup<CalendarResponses> respCals,
			CalendarGroup<CalendarSlots> slotCals) {
		this(thisMonday);

		_slotCals = slotCals;
		_respCals = respCals;

		_startHour = _slotCals.getStartTime().getHourOfDay();
		_endHour = _slotCals.getEndTime().getHourOfDay();
		_numHours = _endHour - _startHour;
		configDays();
	}


	public void setResps(CalendarGroup<CalendarResponses> respCals){

		_respCals = respCals;

		for (int i=0; i<14; i=i+2){
			_days[i].setResponses(getDayResps(i/2, _respCals));
		}
		this.repaint();
	}


	public void setSlots(CalendarGroup<CalendarSlots> slotCals){
		_slotCals = slotCals;
		configDays();
		for (int i=0; i<14; i=i+2){
			if (_days[i].isActive()){
				_days[i].setSlots(getDaySlots(i/2,_slotCals));			
			}
		}
		this.repaint();
	}

	public ArrayList<ArrayList<Response>> getDayResps(int dayOfWeek, CalendarGroup<CalendarResponses> respCals){

		// How would I do this without typecasting?
		ArrayList<ArrayList<Response>> responses = new ArrayList<ArrayList<Response>>();
		for (Object resp: respCals.getCalendars()){
			ArrayList<Response> resps = new ArrayList<Response>();
			CalendarResponses c = (CalendarResponses) resp;
			for (Response r: c.getResponses()){
				if (Days.daysBetween(_thisMonday.plusDays(dayOfWeek), r.getStartTime()).getDays()==0){
					resps.add(r);
				}
			}
			responses.add(resps);
		}
		return responses;

	}

	public ArrayList<CalendarSlots> getDaySlots(int dayOfWeek, CalendarGroup<CalendarSlots> slotCals){

		// How would I do this without typecasting?
		ArrayList<CalendarSlots> slots = new ArrayList<CalendarSlots>();
		for (Object s: slotCals.getCalendars()){
			CalendarSlots c = (CalendarSlots) s;
			Availability[][] oneDayAvail = {c.getAvail()[Days.daysBetween(c.getStartTime(), _thisMonday.plusDays(dayOfWeek)).getDays()]};
			CalendarSlots oneDayCal = new CalendarSlots(c.getStartTime(), c.getEndTime(), c.getOwner(), c.getMinInSlot(), oneDayAvail);
			slots.add(oneDayCal);	
		}
		return slots;
	}


	@Override
	public void makeDays() {
		_days = new DayPanel[14];
		for (int i=0; i<14; i+=2){
			_days[i]=new ClickableDayPanel();
			_days[i+1]=new DayPanel();
			this.add(_days[i]);
			this.add(_days[i+1]);
		}		
	}

	public void configDays(){
		for (int i=0; i<14; i+=2){
			_days[i].setStartHour(_startHour);
			_days[i+1].setStartHour(_startHour);
			_days[i].setNumHours(_numHours);
			_days[i+1].setNumHours(_numHours);
			_days[i].setDay(_thisMonday.plusDays(i/2));
			_days[i+1].setDay(_thisMonday.plusDays(i/2));
			if (_thisMonday.plusDays(i/2).isAfter(_slotCals.getEndTime())
					|| _thisMonday.plusDays(i/2).isBefore(_slotCals.getStartTime())){
				_days[i].setActive(false);
				_days[i+1].setActive(false);	
			} else {
				_days[i].setActive(true);
				_days[i].setResponses(getDayResps(i/2, _respCals));
				_days[i+1].setActive(true);
				_days[i].setSlots(getDaySlots(i/2, _slotCals));
				_days[i+1].setSlots(getDaySlots(i/2, _slotCals));
			}
		}		
		repaint();
	}

}
