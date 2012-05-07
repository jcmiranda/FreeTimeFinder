package gui;

import static gui.GuiConstants.BG_COLOR;

import static gui.GuiConstants.GRAY_OUT_COLOR;
import static gui.GuiConstants.LINE_COLOR;

import static gui.GuiConstants.DEFAULT_START_HOUR;
import static gui.GuiConstants.DEFAULT_END_HOUR;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import org.joda.time.DateTime;

import calendar.CalendarGroup;
import calendar.CalendarResponses;
import calendar.CalendarSlots;
import calendar.Event;

public class DayPanel extends JPanel{

	DateTime _today;
	int _day = 0;
	int _startHour = DEFAULT_START_HOUR;
	int _numHours = DEFAULT_END_HOUR - DEFAULT_START_HOUR;
	private Event _event;
	private CalendarGroup<CalendarSlots> _clicks;
	private CalendarGroup<CalendarResponses> _responses;
	private Boolean _active = true;
	private CalendarResponses _bestTimes;

	public DayPanel(){
		super();
		this.setBackground(BG_COLOR);
		//this.setBorder(new LineBorder(LINE_COLOR, 4, true));
		this.repaint();
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

	public void setBestTimes(CalendarResponses bestTimes){
		_bestTimes = bestTimes;
	}

	public void setEvent(Event event, int day) {
		_event = event;
		_day = day;
	}

	public CalendarGroup<CalendarSlots> getSlots() {
		return _clicks;
	}

	public void setSlots(CalendarGroup<CalendarSlots> slots){

		_clicks = slots;
	}

	public CalendarGroup<CalendarResponses> getResponses() {
		return _responses;
	}

	public void setResponses(CalendarGroup<CalendarResponses> responses){
		_responses = responses;
	}

	public void addSlotCal(CalendarSlots cal){
		_clicks.addCalendar(cal);
	}

	public void setDay(DateTime today){
		_today = today;
		if (_event != null){
			_day = CalendarSlots.getDaysBetween(_event.getStartTime(), _today);
		}
	}

	public DateTime getDay(){
		return _today;
	}

	//	public void nextWeek(){
	//		_today = _today.plusDays(7);
	//		_day+=7;
	//	}
	//
	//	public void lastWeek(){
	//		_today = _today.minusDays(7);
	//		_day-=7;
	//	}


	private void drawLines(Graphics2D brush){

		brush.setColor(LINE_COLOR);
		double hrsDbl = (double) _numHours;
		double heightDbl = (double) this.getHeight();
		for (int i=1; i< _numHours; i++){
			double iDbl = (double) i;
			int height = (int) (iDbl * heightDbl / hrsDbl);
			//int height = (int) ((double) i * (1.0 * this.getHeight()) / (1.0 * _numHours));
			brush.drawLine(0, height, this.getWidth(), height);
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
					r.paint(brush, this, numCals, GuiConstants.RESPONSE_COLOR);
				}
			} else if (_event != null){
				_event.paint(brush, this, CalendarSlots.getDaysBetween(_event.getStartTime(), this.getDay()));
			} 

			if(_clicks != null) {
				for (CalendarSlots s: _clicks.getCalendars()){
					s.paint(brush, this);
				}
			}
			if (_bestTimes!=null){
				_bestTimes.paint(brush,this, 1, GuiConstants.OPTIMAL_COLOR);
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
