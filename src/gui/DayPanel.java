package gui;

import java.awt.Graphics;
import java.awt.Graphics2D;

import org.joda.time.DateTime;

import calendar.CalendarResponses;
import calendar.CalendarSlots;

/**
 * Class representing a day with all invitees of an event's responses, as well as overlayed calculated best times
 * @author roie
 *
 */
public class DayPanel extends Day{

	private CalendarResponses _bestTimes;

	public DayPanel(){
		super();
	}

	public DayPanel(int startHour, int numHours, DateTime today, int day, boolean active){
		this();
		setStartHour(startHour);
		setNumHours(numHours);
		setDay(today);
		setActive(active);
		this.repaint();
	}

	public void setBestTimes(CalendarResponses bestTimes){
		_bestTimes = bestTimes;
	}
	
	/**
	 * In addition to painting lines and BG color, also paint otherinvitees responses for the day, and best times if any
	 */
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D brush = (Graphics2D) g;
		if (isActive()){
			if (getEvent() != null){
				getEvent().paint(brush, this, CalendarSlots.getDaysBetween(getEvent().getStartTime(), this.getDay()));
			} 
			if (_bestTimes!=null){
				_bestTimes.paint(brush,this, 1, GuiConstants.OPTIMAL_COLOR);
			}
		}
	}



}
