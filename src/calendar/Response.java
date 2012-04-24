package calendar;
import gui.DayPanel;


import java.awt.Graphics2D;

import java.awt.geom.RoundRectangle2D;

import static gui.GuiConstants.RESPONSE_NAME_COLOR;
import static gui.GuiConstants.RESPONSE_COLOR;
import static gui.GuiConstants.LINE_COLOR;
import static gui.GuiConstants.RESPONSE_SPACING;
import static gui.GuiConstants.RESPONSE_NAME_SPACING;
import static gui.GuiConstants.RECT_ARC_DIM;

import org.joda.time.DateTime;

public class Response implements Comparable<Response>{
	private DateTime _startTime;
	private DateTime _endTime;
	private String _name;

	public Response(DateTime st, DateTime et) {
		_startTime = st;
		_endTime = et;
		_name = null;
	}

	public Response(DateTime st, DateTime et, String name) {
		_startTime = st;
		_endTime = et;
		_name = name;
	}

	public DateTime getStartTime() {
		return _startTime;
	}
	public DateTime getEndTime() {
		return _endTime;
	}
	public String getName() {
		return _name;
	}
	private String timeToString(DateTime t) {
		return (t.getYear()+"-"+t.getMonthOfYear()+"-"+t.getDayOfMonth()+
				" " + t.getHourOfDay() + ":" + t.getMinuteOfHour());
	}

	public void print() {
		System.out.println("Start: " + timeToString(_startTime) 
				+ "\tEnd: " + timeToString(_endTime));
	}

	@Override
	public int compareTo(Response r) {
		return this.getStartTime().compareTo(r.getStartTime());
	}

	public void paint(Graphics2D brush, DayPanel d, int startX, int endX){

		RoundRectangle2D.Double rect = new RoundRectangle2D.Double();
		int startY = (int) ((double) (_startTime.getMinuteOfHour()/60 + _startTime.getHourOfDay() - d.getStartHour())/d.getNumHours()*d.getHeight());
		int endY = (int) ((double) (_endTime.getMinuteOfHour()/60 + _endTime.getHourOfDay() - d.getStartHour())/d.getNumHours()*d.getHeight());
		rect.setRoundRect(startX+RESPONSE_SPACING, startY, endX-2*RESPONSE_SPACING, endY - startY, RECT_ARC_DIM, RECT_ARC_DIM);		

		brush.setColor(LINE_COLOR);
		brush.draw(rect);
		brush.setColor(RESPONSE_COLOR);
		brush.fill(rect);
		brush.setColor(RESPONSE_NAME_COLOR);
		if (getName()!=null){
			brush.drawString(this.getName(), RESPONSE_SPACING+RESPONSE_NAME_SPACING, startY + brush.getFont().getSize() + RESPONSE_NAME_SPACING);
		}

	}
}
