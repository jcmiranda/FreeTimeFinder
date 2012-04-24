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
//TODO fix these
//
//	public void setResps(CalendarGroup<CalendarResponses> respCals){
//
//		_respCals = respCals;
//
//		for (int i=0; i<7; i++){
//			_days[2*i].setResponses(_respCals);
//		}
//		this.repaint();
//	}
//
//
//	public void setSlots(CalendarGroup<CalendarSlots> slotCals){
//		_slotCals = slotCals;
//		configDays();
//		for (int i=0; i<14; i=i+2){
//			if (_days[2*i+1].isActive()){
//				_days[i].setSlots(getDaySlots(i/2,_slotCals));			
//			}
//		}
//		this.repaint();
//	}

	public ArrayList<ArrayList<Response>> getDayResps(int dayOfWeek, CalendarGroup<CalendarResponses> respCals){

		ArrayList<ArrayList<Response>> responses = new ArrayList<ArrayList<Response>>();
		for (CalendarResponses resp: respCals.getCalendars()){
			ArrayList<Response> resps = new ArrayList<Response>();
			for (Response r: resp.getResponses()){
				if (Days.daysBetween(_thisMonday.plusDays(dayOfWeek), r.getStartTime()).getDays()==0){
					resps.add(r);
				}
			}
			responses.add(resps);
		}
		return responses;

	}

	public ArrayList<CalendarSlots> getDaySlots(int dayOfWeek, CalendarGroup<CalendarSlots> slotCals){

		ArrayList<CalendarSlots> slots = new ArrayList<CalendarSlots>();
		for (CalendarSlots s: slotCals.getCalendars()){
			Availability[][] oneDayAvail = {s.getAvail()[Days.daysBetween(s.getStartTime(), _thisMonday.plusDays(dayOfWeek)).getDays()]};
			CalendarSlots oneDayCal = new CalendarSlots(s.getStartTime(), s.getEndTime(), s.getOwner(), s.getMinInSlot(), oneDayAvail);
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
				_days[i].setResponses(_respCals);
				_days[i+1].setActive(true);
//				_days[i].setSlots(_slotCals);
				_days[i+1].setSlots(_slotCals);
			}
		}		
		repaint();
	}

}
