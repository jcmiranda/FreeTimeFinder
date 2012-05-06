package gui;

import static gui.GuiConstants.DAY_SPACING;
import static gui.GuiConstants.LINE_COLOR;

import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.joda.time.DateTime;

import calendar.Availability;
import calendar.CalGroupType;
import calendar.CalendarGroup;
import calendar.CalendarResponses;
import calendar.CalendarSlots;
import calendar.Event;
import calendar.When2MeetEvent;

public class ReplyPanel extends CalPanel{

	private CalendarGroup<CalendarSlots> _slotCals;
	private CalendarGroup<CalendarResponses> _respCals;
	private CalendarGroup<CalendarSlots> _clicks = null;
	private Day[] _bigDays;
	private JPanel _hourOfDayLabels;

	public ReplyPanel() {
		super();
		this.setLayout(new GridLayout(1,7,DAY_SPACING,0));
		this.setBackground(LINE_COLOR);
	}

	public ReplyPanel(CalendarGroup<CalendarResponses> respCals,
			CalendarGroup<CalendarSlots> slotCals) {
		super();

		this.setBackground(LINE_COLOR);

		_slotCals = slotCals;
		_respCals = respCals;

		setViewDate();

		if(_slotCals != null){
			_startHour = _slotCals.getStartTime().getHourOfDay();
			_endHour = _slotCals.getEndTime().getHourOfDay();
			_numHours = _slotCals.getCalendars().get(0).getNumHours();
		}
		else{

		}

		configDays();
	}

	public CalendarSlots getClicks(){
		return _clicks.getCalendars().get(0);
	}

	public void setResps(CalendarGroup<CalendarResponses> respCals){
		_respCals = respCals;
		configDays();
	}

	public int getWeekDayPanelHeight(){
		return _bigDays[0].getLabelHeight();
	}


	public void setSlots(CalendarGroup<CalendarSlots> slotCals){
		_slotCals = slotCals;
		System.out.println("PASSED INTO SET SLOTS: " + slotCals);
		System.out.println("SLOT CALS : " + _slotCals);
		if(_slotCals != null){
			_startHour = _slotCals.getStartTime().getHourOfDay();
			_endHour = _slotCals.getEndTime().getHourOfDay();
			_numHours = _slotCals.getNumHours();
			//			if (!_slotCals.getCalendars().isEmpty()){
			//				_numHours = _slotCals.getCalendars().get(0).getNumHours();
			//			}
			//			else{
			//			}
		}
		//		_thisMonday = _slotCals.getStartTime().minusDays(_slotCals.getStartTime().getDayOfWeek()-1);
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
	//CalendarSlots.getDaysBetween(_endDay, _slotCals.getEndTime()) != 0 && 
	public void nextWeek(){
		if (_endDay.isBefore(_slotCals.getEndTime())){
			_startDay = _startDay.plusDays(7);
			_endDay = _endDay.plusDays(7);
			if (_endDay.isAfter(_slotCals.getEndTime())){
				_endDay = _slotCals.getEndTime();
			}
		}
		configDays();
	}

	public void prevWeek(){
		if (!(_startDay.getYear()==_slotCals.getStartTime().getYear()
				&& _startDay.getDayOfYear()==_slotCals.getStartTime().getDayOfYear())){
			_startDay = _startDay.minusDays(7);
			_endDay = _endDay.minusDays(7);
		}
		configDays();
	}


	public void updateHourLabels(){
		_hourOfDayLabels.removeAll();
		_hourOfDayLabels.revalidate();
		_hourOfDayLabels.setLayout(new GridBagLayout());
		_hourOfDayLabels.setBackground(GuiConstants.LINE_COLOR);

		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0,0,0,0);
		c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = 0;
		JPanel space = new JPanel();
		space.add(new JLabel(" "));
		_hourOfDayLabels.add(space, c);


		for (int i=_startHour; i<_startHour + _numHours; i++){
			JPanel hourLabel = new JPanel();
			hourLabel.add(new JLabel(i+ ":00", SwingConstants.CENTER), SwingConstants.CENTER);
			hourLabel.setBackground(GuiConstants.LABEL_COLOR);
			_hourOfDayLabels.add(hourLabel);
			c.weightx = 1.0;

			c.insets = new Insets(1,0,0,0);
			c.weighty = 1.0;

			c.gridx = 0;
			c.gridy = i - _startHour + 1;
			System.out.println(c.gridy);
			_hourOfDayLabels.add(hourLabel, c);
		}
		_hourOfDayLabels.revalidate();
		_hourOfDayLabels.repaint();
		this.repaint();
	}

	public void makeHourLabels(){
		_hourOfDayLabels = new JPanel();
		_hourOfDayLabels.setBackground(GuiConstants.LINE_COLOR);
		//		//		_hourOfDayLabels.setLayout(new GridLayout(_numHours, 1, 0, 1));
		//		//		_hourOfDayLabels.setBorder(new EmptyBorder(0,0,0,0));
		//		//		
		//		//		for (int i=_startHour; i<_startHour + _numHours; i++){
		//		//			JPanel hourLabel = new JPanel();
		//		//			hourLabel.add(new JLabel(i+ ":00", SwingConstants.CENTER), SwingConstants.CENTER);
		//		//			hourLabel.setBorder(new EmptyBorder(0,0,0,0));
		//		//			hourLabel.setBackground(GuiConstants.LABEL_COLOR);
		//		//			_hourOfDayLabels.add(hourLabel);
		//		//		}
		//		_hourOfDayLabels = new JPanel();
		//		_hourOfDayLabels.setBackground(GuiConstants.LINE_COLOR);
		//		_hourOfDayLabels.setLayout(new GridBagLayout());
		//		//		_hourOfDayLabels.setBorder(new EmptyBorder (0,0,0,0));
		//		GridBagConstraints c = new GridBagConstraints();
		//
		//		c.fill = GridBagConstraints.BOTH;
		//		c.insets = new Insets(1,0,0,0);
		//		c.weighty = 1.0;
		//		c.gridx = 0;
		//		c.gridy = 0;
		//		JPanel space = new JPanel();
		//		space.add(new JLabel("a"));
		//		_hourOfDayLabels.add(space, c);
		//
		//		for (int i=_startHour; i<_startHour + _numHours; i++){
		//			JPanel hourLabel = new JPanel();
		//			hourLabel.setBorder(null);
		//			//			hourLabel.setBorder(new EmptyBorder (0,0,0,0));
		//			hourLabel.add(new JLabel(i+ ":00", SwingConstants.CENTER));
		//			hourLabel.setBackground(GuiConstants.LABEL_COLOR);
		//			c.weightx = 1.0;
		//
		//
		//			if (i==0){
		//				c.fill = GridBagConstraints.BOTH;
		//				c.insets = new Insets(1,0,0,0);
		//				c.weighty = 1.0;
		//			}
		////			else if (i==_startHour + _numHours -1) {
		////				c.fill = GridBagConstraints.BOTH;
		////				c.insets = new Insets(0,0,0,0);
		////				c.weighty = 1.0;
		////			} else if (i==_startHour + _numHours -2) {
		////				c.fill = GridBagConstraints.BOTH;
		////				c.insets = new Insets(1,0,1,0);
		////				c.weighty = 1.0;
		////			}
		//			else{
		//				c.fill = GridBagConstraints.BOTH;
		//				c.insets = new Insets(1,0,0,0);
		//				c.weighty = 1.0;
		//			}
		//			c.gridx = 0;
		//			c.gridy = i - _startHour + 1;
		//			System.out.println(c.gridy);
		//			_hourOfDayLabels.add(hourLabel, c);
		//		}


	}



	//	public ArrayList<ArrayList<Response>> getDayResps(int dayOfWeek, CalendarGroup<CalendarResponses> respCals){
	//
	//		ArrayList<ArrayList<Response>> responses = new ArrayList<ArrayList<Response>>();
	//		for (CalendarResponses resp: respCals.getCalendars()){
	//			ArrayList<Response> resps = new ArrayList<Response>();
	//			for (Response r: resp.getResponses()){
	//				if (Days.daysBetween(_thisMonday.plusDays(dayOfWeek), r.getStartTime()).getDays()==0){
	//					resps.add(r);
	//				}
	//			}
	//			responses.add(resps);
	//		}
	//		return responses;
	//
	//	}

	//	public ArrayList<CalendarSlots> getDaySlots(int dayOfWeek, CalendarGroup<CalendarSlots> slotCals){
	//
	//		ArrayList<CalendarSlots> slots = new ArrayList<CalendarSlots>();
	//		for (CalendarSlots s: slotCals.getCalendars()){
	//			Availability[][] oneDayAvail = {s.getAvail()[Days.daysBetween(s.getStartTime(), _thisMonday.plusDays(dayOfWeek)).getDays()]};
	//			CalendarSlots oneDayCal = new CalendarSlots(s.getStartTime(), s.getEndTime(), s.getOwner(), s.getMinInSlot(), oneDayAvail);
	//			slots.add(oneDayCal);	
	//		}
	//		return slots;
	//	}

	public void setViewDate(){

		if (_slotCals!=null){
			_startDay =  _slotCals.getStartTime();

			if (CalendarSlots.getDaysBetween(_slotCals.getStartTime(), _slotCals.getEndTime()) < 7)
				_endDay = _slotCals.getEndTime();
			else
				_endDay = _slotCals.getStartTime().plusDays(6);	
		}
		else {
			_startDay = DateTime.now();
			_endDay = _startDay.plusDays(6);
		}
	}


	@Override
	public void makeDays() {

		makeHourLabels();
		this.add(_hourOfDayLabels);

		_bigDays=new Day[7];

		for (int i=0; i<7; i++){
			_bigDays[i]=new Day(new ClickableDayPanel(), new DayPanel(), new DateTime());
			this.add(_bigDays[i]);
		}	

		//		_days = new DayPanel[14];
		//		for (int i=0; i<14; i+=2){
		//			_days[i]=new ClickableDayPanel();
		//			_days[i+1]=new DayPanel();
		//			this.add(_days[i]);
		//			this.add(_days[i+1]);
		//		}		
	}

	public void configDays(){

		updateHourLabels();

		int numDays = CalendarSlots.getDaysBetween(_startDay, _endDay) +1;
		this.setLayout(new GridLayout(1,numDays,DAY_SPACING,0));
		int ctr = 0;

		for (int i=0; i<7; i++){
			_bigDays[i].setStartHour(_startHour);
			_bigDays[i].setNumHours(_numHours);
			_bigDays[i].setDay(_startDay.plusDays(i));
			if ((_slotCals != null &&_startDay.plusDays(i).isAfter(_slotCals.getEndTime())) || (_slotCals == null && _startDay.plusDays(i).isAfter(_endDay))){
				_bigDays[i].setActive(false);
			} else {
				_bigDays[i].setActive(true);
				_bigDays[i].getClickableDay().setResponses(_respCals);
				_bigDays[i].getDay().setEvent((Event) _slotCals, ctr);

				if(_slotCals != null){
					_clicks = new CalendarGroup<CalendarSlots>(_slotCals.getStartTime(), _slotCals.getEndTime(), CalGroupType.When2MeetEvent);
					if(((When2MeetEvent) _slotCals).getUserResponse() != null) {
						_clicks.addCalendar(((When2MeetEvent) _slotCals).getUserResponse());
					}
					else{
						_clicks.addCalendar(new CalendarSlots(_slotCals.getStartTime(),
								_slotCals.getEndTime(),
								_slotCals.getCalendars().get(0).getMinInSlot(),
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
