package gui;

import static gui.GuiConstants.DAY_SPACING;
import static gui.GuiConstants.DEFAULT_END_HOUR;
import static gui.GuiConstants.DEFAULT_START_HOUR;
import static gui.GuiConstants.LINE_COLOR;
import static gui.GuiConstants.MAX_DAY_WIDTH;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.joda.time.DateTime;

import calendar.Availability;
import calendar.CalendarResponses;
import calendar.CalendarSlots;
import calendar.Event;
import calendar.UserCal;
import calendar.When2MeetEvent;

/**
 * Panel class that holds all the visual representation of current selected event
 * @author roie
 *
 */
public class ReplyPanel extends JPanel{

	// Current selected event
	private Event _event;
	// User's personal calendars
	private UserCal _userCal;
	// User's response input
	private CalendarSlots _clicks = null;
	// Visual organization panels
	private OuterDayPanel[] _bigDays;
	private JPanel _bigDayPanel;
	private HourOfDayPanel _innerLabelPanel;
	private JPanel _postPadding;
	private JPanel _outerBigDayPanel;
	
	// Panel holding hour of day labels
	private JPanel _hourOfDayLabels;
	private int _startHour = DEFAULT_START_HOUR;
	private int _endHour = DEFAULT_END_HOUR;
	private int _numHours =  DEFAULT_END_HOUR - DEFAULT_START_HOUR;
	private DateTime _startDay;
	private DateTime _endDay;
	
	// Boolean representing whether all 7 days of the week should be displayed for a given week within the span of an event
	// Will always be true if the event's span is >= 7, false otherwise
	private boolean _fullWeekMode = true;

	/**
	 * Initializes empty ReplyPanel
	 */
	public ReplyPanel() {
		super();
		_startDay = new DateTime();
		_endDay = _startDay.plusDays(6);
		_bigDayPanel = new JPanel();
		_bigDayPanel.setBackground(LINE_COLOR);
		makeDays();
		this.repaint();
	}

	/**
	 * Initialize ReplyPanel with a user's calendar and an event
	 * @param userCal
	 * @param event
	 */
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
			_numHours = _event.getNumHours();
			if(CalendarSlots.getDaysBetween(_event.getStartTime(), _event.getEndTime()) + 1 < 7){
				_fullWeekMode = false;
				System.out.println("FULL WEEK MODE SET TO FALSE");
			}
			else{
				System.out.println("CONSTRUCTOR SET FULL WEEK MODE TRUE");
				_fullWeekMode = true;
			}
		}
		else{
			_fullWeekMode = true;
			System.out.println("CONSTRUCTOR SET FULL WEEK MODE TRUE");
		}

		configDays();
	}

	public CalendarSlots getClicks(){
		return _clicks;
	}

	public void setUserCal(UserCal respCals){
		_userCal = respCals;
		configDays();
	}

	public int getWeekDayPanelHeight(){
		return _bigDays[0].getLabelHeight();
	}


	/**
	 * Set current event being viewed
	 * @param slotCals
	 */
	public void setEvent(Event slotCals){
		_event = slotCals;

		if(_event != null){
			_startHour = _event.getStartTime().getHourOfDay();
			_endHour = _event.getEndTime().getHourOfDay();
			_numHours = _event.getNumHours();
			if(CalendarSlots.getDaysBetween(_event.getStartTime(), _event.getEndTime()) + 1 < 7){
				_fullWeekMode = false;
			}
			else{
				_fullWeekMode = true;
			}
		}
		else{
			_startHour = 9;
			_endHour = 5;
			_numHours = 8;
			_fullWeekMode = true;
		}

		setBestTimes(null);
		setViewDate();
		configDays();
	}

	/**
	 * Set best times, iterate through days and display best times on the day panels
	 * @param bestTimes
	 */
	public void setBestTimes(CalendarResponses bestTimes){
		for (OuterDayPanel d: _bigDays){
			d.setBestTimes(bestTimes);
		}
	}

	/**
	 * If next week overlaps with range of the current event
	 * Increment all days of the week by 7
	 */
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

	/**
	 * If previous week overlaps with range of the current event
	 * Decrement all days of the week by 7
	 */
	public void prevWeek(){
		if (!(_startDay.getYear()==_event.getStartTime().getYear()
				&& _startDay.getDayOfYear()==_event.getStartTime().getDayOfYear())){
			_startDay = _startDay.minusDays(7);
			_endDay = _endDay.minusDays(7);
		}
		configDays();
	}

	/**
	 * Update hour labels based startTime and numHours in the day
	 */
	public void updateHourLabels(){
		_innerLabelPanel.updateHours(_startHour, _numHours);
		_hourOfDayLabels.revalidate();
		_hourOfDayLabels.repaint();
		this.repaint();
	}

	/**
	 * Build labels for hours of the day
	 */
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

	/**
	 * Build and layout the day Panels and organize them
	 */
	public void makeDays() {

		this.setLayout(new BorderLayout());
		_bigDayPanel.setLayout(new GridLayout(1,0,DAY_SPACING,0));
		_outerBigDayPanel = new JPanel();

		makeHourLabels();
		this.add(_hourOfDayLabels, BorderLayout.WEST);

		_bigDays=new OuterDayPanel[7];
		_postPadding = new JPanel();

		for (int i=0; i<7; i++){
			_bigDays[i]=new OuterDayPanel(new ClickableDayPanel(), new DayPanel(), new DateTime());
			_bigDayPanel.add(_bigDays[i]);
		}	

		_outerBigDayPanel.setLayout(new FlowLayout());
		((FlowLayout)(_outerBigDayPanel.getLayout())).setHgap(0);
		((FlowLayout)(_outerBigDayPanel.getLayout())).setVgap(0);
		((FlowLayout)(_outerBigDayPanel.getLayout())).setAlignOnBaseline(true);
		
		_outerBigDayPanel.add(_bigDayPanel);
		_outerBigDayPanel.add(_postPadding);

		this.add(_outerBigDayPanel, BorderLayout.CENTER);
	}

	/**
	 * Update the days of the week based on the fields of ReplyPanel
	 */
	public void configDays(){

		updateHourLabels();
		_outerBigDayPanel.removeAll();
		_bigDayPanel.removeAll();
		
		Dimension dayDim, padDim;
		// if there are at least 7 days in the events span, draw all days of the week and gray out inactive ones
		// else do not display inactive days and instead resize the others according to graphical constants 
		if (!_fullWeekMode){
			int numDays = CalendarSlots.getDaysBetween(_startDay, _endDay) + 1;
			int maxCalWidth = Math.min(MAX_DAY_WIDTH*numDays, _outerBigDayPanel.getWidth());
			
			dayDim = new Dimension(maxCalWidth, _outerBigDayPanel.getHeight());
			padDim = new Dimension((_outerBigDayPanel.getWidth() - maxCalWidth), _outerBigDayPanel.getHeight());
		}
		else{
			dayDim = new Dimension(_outerBigDayPanel.getWidth(), _outerBigDayPanel.getHeight());
			padDim = new Dimension(0, _outerBigDayPanel.getHeight());
		}
		
		_bigDayPanel.setMaximumSize(dayDim);
		_bigDayPanel.setSize(dayDim);
		_bigDayPanel.setPreferredSize(dayDim);
		_postPadding.setSize(padDim);
		_postPadding.setMaximumSize(padDim);
		_postPadding.setPreferredSize(padDim);

		int ctr = 0;

		for (int i=0; i<7; i++){
			_bigDays[i].setStartHour(_startHour);
			_bigDays[i].setNumHours(_numHours);
			_bigDays[i].setDay(_startDay.plusDays(i));
			
			if ((_event != null &&_startDay.plusDays(i).isAfter(_event.getEndTime())) || (_event == null && _startDay.plusDays(i).isAfter(_endDay))){
				_bigDays[i].setActive(false);
				if (_fullWeekMode){
					_bigDayPanel.add(_bigDays[i]);
				}

			} else {
				_bigDays[i].setActive(true);
				_bigDays[i].getClickableDay().setUserCal(_userCal);
				_bigDays[i].getDay().setEvent((Event) _event, ctr);

				if(_event != null){
					if(((When2MeetEvent) _event).getUserResponse() != null) {
						_clicks = ((When2MeetEvent) _event).getUserResponse();
					}
					else{
						_clicks = new CalendarSlots(_event.getStartTime(),
								_event.getEndTime(),
								_event.getMinInSlot(),
								Availability.busy);
					}
				}
				else
					_clicks = null;

				_bigDays[i].getClickableDay().setClicks(_clicks);
				_bigDayPanel.add(_bigDays[i]);
				ctr++;
			}
		}
		
		_outerBigDayPanel.add(_bigDayPanel);
		_outerBigDayPanel.add(_postPadding);
		this.revalidate();
	}


	public void paintComponent(Graphics g){
		super.paintComponent(g);
		for(OuterDayPanel day: _bigDays){
			day.repaint();
		}
	}

}
