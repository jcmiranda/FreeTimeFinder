package gui;

import static gui.GuiConstants.DAY_SPACING;
import static gui.GuiConstants.DEFAULT_END_HOUR;
import static gui.GuiConstants.DEFAULT_START_HOUR;
import static gui.GuiConstants.LINE_COLOR;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

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
	private OuterDayPanel[] _bigDays;
	private JPanel _hourOfDayLabels;
	private HourOfDayPanel _innerLabelPanel;
	private JPanel _bigDayPanel;
	protected int _startHour = DEFAULT_START_HOUR;
	protected int _endHour = DEFAULT_END_HOUR;
	protected int _numHours =  DEFAULT_END_HOUR - DEFAULT_START_HOUR;
	protected DateTime _startDay;
	protected DateTime _endDay;
	protected DayPanel[] _days;

	public ReplyPanel() {
		super();
		_startDay = new DateTime();
		_endDay = _startDay.plusDays(6);
		_bigDayPanel = new JPanel();
		_bigDayPanel.setBackground(LINE_COLOR);
		makeDays();
		this.repaint();
	}

	public ReplyPanel(UserCal userCal, Event event) {
		super();
		_startDay = new DateTime();
		_endDay = _startDay.plusDays(6);
		_bigDayPanel = new JPanel();
		_bigDayPanel.setBackground(LINE_COLOR);
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

		setBestTimes(null);
		setViewDate();
		configDays();
	}

	public void setBestTimes(CalendarResponses bestTimes){
		for (OuterDayPanel d: _bigDays){
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

	public void updateHourLabels(){
		_innerLabelPanel.updateHours(_startHour, _numHours);
		_hourOfDayLabels.revalidate();
		_hourOfDayLabels.repaint();
		this.repaint();
	}

	public void makeHourLabels(){

		_hourOfDayLabels = new JPanel();
		
		GridBagConstraints c1 = new GridBagConstraints();
		_innerLabelPanel = new HourOfDayPanel(_startHour, _numHours);
		_hourOfDayLabels.setLayout(new GridBagLayout());
		_hourOfDayLabels.setBackground(GuiConstants.LINE_COLOR);
		
		JPanel space = new JPanel();
		space.add(new JLabel("        "));
		c1.fill = GridBagConstraints.HORIZONTAL;
		c1.weightx = 1.0;
		c1.weighty = 0.0;
		c1.insets = new Insets(0,0,1,0);
		c1.gridx = 0;
		c1.gridy = 0;
		_hourOfDayLabels.add(space, c1);
		
		c1.fill = GridBagConstraints.BOTH;
		c1.weightx = 1.0;
		c1.weighty = 1.0;
		c1.gridx = 0;
		c1.gridy = 1;
		c1.insets = new Insets(0,0,0,0);
		_hourOfDayLabels.add(_innerLabelPanel, c1);
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

		this.setLayout(new BorderLayout());
		_bigDayPanel.setLayout(new GridLayout(1,7,DAY_SPACING,0));
		
		
		makeHourLabels();
		this.add(_hourOfDayLabels, BorderLayout.WEST);

		_bigDays=new OuterDayPanel[7];

		for (int i=0; i<7; i++){
			_bigDays[i]=new OuterDayPanel(new ClickableDayPanel(), new DayPanel(), new DateTime());
			_bigDayPanel.add(_bigDays[i]);
		}	
		
		this.add(_bigDayPanel, BorderLayout.CENTER);
	}

	public void configDays(){

		updateHourLabels();
		
		int numDays = CalendarSlots.getDaysBetween(_startDay, _endDay) +1;
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
						System.out.println(_event.getMinInSlot());
//						System.exit(0);
						_clicks.addCalendar(new CalendarSlots(_event.getStartTime(),
								_event.getEndTime(),
								_event.getMinInSlot(),
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
		for(OuterDayPanel day: _bigDays){
			day.repaint();
		}
	}

}
