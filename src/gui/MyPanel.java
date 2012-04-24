package gui;

import static gui.GuiConstants.LINE_SPACING;

import java.awt.GridLayout;
import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.Days;

import calendar.CalendarGroup;
import calendar.CalendarResponses;
import calendar.Response;

public class MyPanel extends CalPanel{

	private CalendarGroup<CalendarResponses> _respCals;

	public MyPanel(DateTime thisMonday) {
		super(thisMonday);
		this.setLayout(new GridLayout(1,7,LINE_SPACING,0));
	}
	public MyPanel(DateTime thisMonday,
			CalendarGroup<CalendarResponses> respCals) {
		this(thisMonday);
		_respCals = respCals;

		_startHour = _respCals.getStartTime().getHourOfDay();
		_endHour = _respCals.getEndTime().getHourOfDay();
		_numHours = _endHour - _startHour;

		configDays();
	}

	public void setResps(CalendarGroup<CalendarResponses> respCals){

		_respCals=respCals;
		configDays();
	}


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

	public void configDays(){
		for (int i=0; i<7; i++){
			_days[i].setStartHour(_startHour);
			_days[i].setNumHours(_numHours);
			_days[i].setDay(_thisMonday.plusDays(i));

			if (_thisMonday.plusDays(i).isAfter(_respCals.getEndTime())
					|| _thisMonday.plus(i).isBefore(_respCals.getStartTime())){
				_days[i].setActive(false);
			} else {
				_days[i].setActive(true);
				_days[i].setResponses(_respCals);
			}
		}
		repaint();
	}

	@Override
	public void makeDays() {
		_days = new DayPanel[7];
		for (int i=0; i<7; i++){
			_days[i]=new DayPanel();
			this.add(_days[i]);
		}
	}	



}
