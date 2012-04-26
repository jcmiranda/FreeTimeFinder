package gui;

import static gui.GuiConstants.BG_COLOR;


import static gui.GuiConstants.GRAY_OUT_COLOR;
import static gui.GuiConstants.LINE_COLOR;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.joda.time.DateTime;

import calendar.CalendarGroup;
import calendar.CalendarResponses;
import calendar.CalendarSlots;
import calendar.Availability;
import calendar.Event;
import calendar.Response;
import calendar.When2MeetEvent;

public class DayPanel extends JPanel{

	DateTime _today;
	int _day = 0;
	int _startHour = 0;
	int _numHours = 24;
	private Event _event;
	private CalendarGroup<CalendarSlots> _slots;
	private CalendarGroup<CalendarResponses> _responses;
	private Boolean _active = true;


	public DayPanel(){
		super();
		this.setBackground(BG_COLOR);
		//this.setBorder(new LineBorder(LINE_COLOR, 4, true));
		this.repaint();
		System.out.println("Default day panel created");
	}

	public DayPanel(int startHour, int numHours, DateTime today, int day, boolean active){
		this();
		_startHour = startHour;
		_numHours = numHours;
		_today = today;
		_active = active;
		this.repaint();
		System.out.println("Special Day Panel Created");
	}

	public int getStartHour() {
		return _startHour;
	}

	public void setStartHour(int startHour) {
		_startHour = startHour;
	}

	public int getNumHours() {
		return _numHours;
	}

	public void setNumHours(int numHours) {
		_numHours = numHours;
	}

	public void setActive(Boolean active){
		_active = active;
	}

	public boolean isActive(){
		return _active;
	}


	public void setEvent(Event event, int day) {
		System.out.println("Event set");
		_event = event;
		_day = day;
	}
	
	public CalendarGroup<CalendarSlots> getSlots() {
		return _slots;
	}

	public void setSlots(CalendarGroup<CalendarSlots> slots){
		
		_slots = slots;
	}

	public CalendarGroup<CalendarResponses> getResponses() {
		return _responses;
	}

	public void setResponses(CalendarGroup<CalendarResponses> responses){
		_responses = responses;
	}

	public void addSlotCal(CalendarSlots cal){
		_slots.addCalendar(cal);
	}

	public void setDay(DateTime today){
		_today = today;
	}

	public DateTime getDay(){
		return _today;
	}

	public void nextWeek(){
		_today = _today.plusDays(7);
	}

	public void lastWeek(){
		_today = _today.minusDays(7);
	}


	private void drawLines(Graphics2D brush){

		brush.setColor(LINE_COLOR);
		for (int i=1; i<_numHours; i++){
			brush.drawLine(0, i*this.getHeight()/_numHours, this.getWidth(), i*this.getHeight()/_numHours);
		}
	}


	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D brush = (Graphics2D) g;
		if (!_active){
			brush.setColor(GRAY_OUT_COLOR);
			brush.fillRect(0, 0, getWidth(), getHeight());
			drawLines(brush);
			return; 
		} 
		else {
			drawLines(brush);
			if (_responses!=null){
				int numCals = _responses.getCalendars().size();
				for (CalendarResponses r: _responses.getCalendars()){
					r.paint(brush, this, numCals);
				}
			} else if (_event != null){
				_event.paint(brush, this, _day);
			} 

			if(_slots != null) {
				for (CalendarSlots s: _slots.getCalendars()){
					s.paint(brush, this);
				}
			}
		}
		

	}

	// Maybe use this later
	//	_responses.get(i).get(j).setGfxParams(i*this.getWidth()/numCals+j*this.getWidth()/numCals/numResps,
	//			this.getWidth()/numCals/numResps,
	//			this.getHeight(),
	//			_startHour,
	//			_startHour+_numHours);



}
