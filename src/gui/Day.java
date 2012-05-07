package gui;

import static gui.GuiConstants.BG_COLOR;
import static gui.GuiConstants.DEFAULT_END_HOUR;
import static gui.GuiConstants.DEFAULT_START_HOUR;
import static gui.GuiConstants.GRAY_OUT_COLOR;
import static gui.GuiConstants.LINE_COLOR;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import org.joda.time.DateTime;

import calendar.CalendarSlots;
import calendar.Event;

/**
 * Abstract panel class representing a day and planned items for the day
 * @author roie
 *
 */
public abstract class Day extends JPanel{

	//Flag representing whether the day is in the current selected event's range
	private boolean _active = true;
	private DateTime _today;
	// number of days since the start of the event
	private int _day = 0;
	private int _startHour = DEFAULT_START_HOUR;
	private int _numHours = DEFAULT_END_HOUR - DEFAULT_START_HOUR;
	
	private Event _event;
	
	public Day(){
		super();
		this.setBackground(BG_COLOR);
		this.repaint();
	}

	public Day(int startHour, int numHours, DateTime today, int day, boolean active){
		this();
		_startHour = startHour;
		_numHours = numHours;
		_today = today;
		_active = active;
		this.repaint();
	}
	
	public Event getEvent(){
		return _event;
	}

	public void setEvent(Event event, int day) {
		_event = event;
		_day = day;
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

	public void setDay(DateTime today){
		_today = today;
		if (_event != null){
			_day = CalendarSlots.getDaysBetween(_event.getStartTime(), _today);
		}
	}

	public DateTime getDay(){
		return _today;
	}
	

	public void setActive(Boolean active){
		_active = active;
	}

	public boolean isActive(){
		return _active;
	}
	

	/**
	 * Graphics method that draws lines dividing the day into hours
	 * @param brush
	 */
	private void drawLines(Graphics2D brush){

		brush.setColor(LINE_COLOR);
		double hrsDbl = (double) _numHours;
		double heightDbl = (double) this.getHeight();
		for (int i=1; i< _numHours; i++){
			double iDbl = (double) i;
			int height = (int) (iDbl * heightDbl / hrsDbl);
			brush.drawLine(0, height, this.getWidth(), height);
		}
	}

	
	/**
	 * Set background to a grayed out color if day is not active and paint lines
	 */
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
		}
	}
	
	
	
}
