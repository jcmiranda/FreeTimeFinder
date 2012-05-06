package gui;

import static gui.GuiConstants.DAY_SPACING;
import static gui.GuiConstants.DEFAULT_END_HOUR;
import static gui.GuiConstants.DEFAULT_START_HOUR;
import static gui.GuiConstants.LINE_COLOR;

import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.JPanel;

import org.joda.time.DateTime;

import calendar.Availability;
import calendar.CalGroupType;
import calendar.CalendarGroup;
import calendar.CalendarResponses;
import calendar.CalendarSlots;
import calendar.Event;
import calendar.UserCal;
import calendar.When2MeetEvent;

public class ReplyPanel extends JPanel{

	private Event _event;
	private UserCal _userCal;
	private CalendarGroup<CalendarSlots> _clicks = null;
	private Day[] _bigDays;
	
	protected int _startHour = DEFAULT_START_HOUR;
	protected int _endHour = DEFAULT_END_HOUR;
	protected int _numHours =  DEFAULT_END_HOUR - DEFAULT_START_HOUR;
	protected DateTime _startDay;
	protected DateTime _endDay;
	protected DayPanel[] _days;


	public ReplyPanel() {
		super();
		this.setBackground(LINE_COLOR);
		_startDay = new DateTime();
		_endDay = _startDay.plusDays(6);
		makeDays();
		this.repaint();
		this.setLayout(new GridLayout(1,7,DAY_SPACING,0));
		this.setBackground(LINE_COLOR);
	}

	public ReplyPanel(UserCal userCal, Event event) {
		super();

		this.setBackground(LINE_COLOR);
		_startDay = new DateTime();
		_endDay = _startDay.plusDays(6);
		makeDays();

		_event = event;
		_userCal = userCal;

		setViewDate();

		if(_event != null){
			_startHour = _event.getStartTime().getHourOfDay();
			_endHour = _event.getEndTime().getHourOfDay();
			_numHours = _event.getCalendars().get(0).getNumHours();
		}

		configDays();
	}

	public CalendarSlots getClicks(){
		return _clicks.getCalendars().get(0);
	}

	public void setUserCal(UserCal respCals){
		_userCal = respCals;
		configDays();
	}

	public int getWeekDayPanelHeight(){
		return _bigDays[0].getLabelHeight();
	}


	public void setEvent(Event slotCals){
		_event = slotCals;
		System.out.println("PASSED INTO SET SLOTS: " + slotCals);
		System.out.println("SLOT CALS : " + _event);
		if(_event != null){
			_startHour = _event.getStartTime().getHourOfDay();
			_endHour = _event.getEndTime().getHourOfDay();
			_numHours = _event.getNumHours();
		}
		else{
			_startHour = 9;
			_endHour = 5;
			_numHours = 8;
		}

		setViewDate();
		configDays();
	}

	public void setBestTimes(CalendarResponses bestTimes){
		for (Day d: _bigDays){
			d.setBestTimes(bestTimes);
		}
	}
	
	public void nextWeek(){
		if (_endDay.isBefore(_event.getEndTime())){
			_startDay = _startDay.plusDays(7);
			_endDay = _endDay.plusDays(7);
			if (_endDay.isAfter(_event.getEndTime())){
				_endDay = _event.getEndTime();
			}
		}
		configDays();
	}

	public void prevWeek(){
		if (!(_startDay.getYear()==_event.getStartTime().getYear()
				&& _startDay.getDayOfYear()==_event.getStartTime().getDayOfYear())){
			_startDay = _startDay.minusDays(7);
			_endDay = _endDay.minusDays(7);
		}
		configDays();
	}

	public void setViewDate(){

		if (_event!=null){
			_startDay =  _event.getStartTime();

			if (CalendarSlots.getDaysBetween(_event.getStartTime(), _event.getEndTime()) < 7)
				_endDay = _event.getEndTime();
			else
				_endDay = _event.getStartTime().plusDays(6);	
		}
		else {
			_startDay = DateTime.now();
			_endDay = _startDay.plusDays(6);
		}
	}


	public void makeDays() {

		_bigDays=new Day[7];

		for (int i=0; i<7; i++){
			_bigDays[i]=new Day(new ClickableDayPanel(), new DayPanel(), new DateTime());
			this.add(_bigDays[i]);
		}	
		
	}

	public void configDays(){

		int numDays = CalendarSlots.getDaysBetween(_startDay, _endDay) +1;
		this.setLayout(new GridLayout(1,numDays,DAY_SPACING,0));
		int ctr = 0;

		for (int i=0; i<7; i++){
			_bigDays[i].setStartHour(_startHour);
			_bigDays[i].setNumHours(_numHours);
			_bigDays[i].setDay(_startDay.plusDays(i));
			if ((_event != null &&_startDay.plusDays(i).isAfter(_event.getEndTime())) || (_event == null && _startDay.plusDays(i).isAfter(_endDay))){
				_bigDays[i].setActive(false);
			} else {
				_bigDays[i].setActive(true);
				_bigDays[i].getClickableDay().setResponses(_userCal);
				_bigDays[i].getDay().setEvent((Event) _event, ctr);

				if(_event != null){
					_clicks = new CalendarGroup<CalendarSlots>(_event.getStartTime(), _event.getEndTime(), CalGroupType.When2MeetEvent);
					if(((When2MeetEvent) _event).getUserResponse() != null) {
						_clicks.addCalendar(((When2MeetEvent) _event).getUserResponse());
					}
					else{
						_clicks.addCalendar(new CalendarSlots(_event.getStartTime(),
								_event.getEndTime(),
								_event.getCalendars().get(0).getMinInSlot(),
								Availability.busy));
					}
				}
				else
					_clicks = null;

				

				_bigDays[i].getClickableDay().setSlots(_clicks);
				ctr++;
			}
		}		
	}


	public void paintComponent(Graphics g){
		super.paintComponent(g);
		for(Day day: _bigDays){
			day.repaint();
		}
	}

}
