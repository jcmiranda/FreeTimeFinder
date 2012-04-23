package calendar;
import java.awt.Graphics2D;

import java.awt.geom.RoundRectangle2D;

import javax.swing.JLabel;

import static gui.GuiConstants.RESPONSE_COLOR;
import static gui.GuiConstants.RESPONSE_SPACING;
import static gui.GuiConstants.LINE_COLOR;
import static gui.GuiConstants.RECT_ARC_DIM;

import org.joda.time.DateTime;

public class Response implements Comparable<Response>{
	private DateTime _startTime;
	private DateTime _endTime;
	private String _name;
	
	// Graphical information
	private int _startX;
	private int _panelWidth;
	private int _panelHeight;
	private int _dayStartTime;
	private int _dayEndTime;
	
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
	
	public void setGfxParams(int startX, int panelWidth, int panelHeight, int dayStartTime, int dayEndTime){
		_startX=startX;
		_panelWidth = panelWidth;
		_panelHeight= panelHeight;
		_dayStartTime = dayStartTime;
		_dayEndTime = dayEndTime;
	}
	
	
	public void paint(Graphics2D brush){
		
		RoundRectangle2D.Double rect = new RoundRectangle2D.Double();
		int _numHours = _dayEndTime - _dayStartTime;
		int startY = (int) ((double) (_startTime.getMinuteOfHour()/60 + _startTime.getHourOfDay() - _dayStartTime)/_numHours*_panelHeight);
		int endY = (int) ((double) (_endTime.getMinuteOfHour()/60 + _endTime.getHourOfDay() - _dayStartTime)/_numHours*_panelHeight);
		rect.setRoundRect(_startX+RESPONSE_SPACING, startY, _panelWidth-2*RESPONSE_SPACING, endY - startY, RECT_ARC_DIM, RECT_ARC_DIM);		
		
		brush.setColor(LINE_COLOR);
		brush.draw(rect);
		brush.setColor(RESPONSE_COLOR);
		brush.fill(rect);
		
	}
}
