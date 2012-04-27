package calendar;
import gui.DayPanel;


import java.awt.Color;
import java.awt.Graphics2D;

import java.awt.geom.Rectangle2D;
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
	
	private int _indentation = 0;

	public int getIndentation(){
		return _indentation;
	}
	
	public void setIndentation(int indentation){
		_indentation = indentation;
	}
	
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

//		RoundRectangle2D.Double rect = new RoundRectangle2D.Double();
		Rectangle2D.Double rect = new Rectangle2D.Double();
		
		double endXDbl = (double) endX;
		double startXDbl = (double) startX;
		double spaceDbl = (double) RESPONSE_SPACING;
		
		double stMinDbl = (double) _startTime.getMinuteOfHour();
		double stHrDbl = (double) _startTime.getHourOfDay();
		double endMinDbl = (double) _endTime.getMinuteOfHour();
		double endHrDbl = (double) _endTime.getHourOfDay();
		double dayStDbl = (double) d.getStartHour();
		double numHrDbl = (double) d.getNumHours();
		double heightDbl = (double) d.getHeight();
		
		double startY = (stMinDbl/60.0 + stHrDbl - dayStDbl)/numHrDbl*heightDbl;
		double endY =  (endMinDbl/60.0 + endHrDbl - dayStDbl)/numHrDbl*heightDbl;
//		rect.setRoundRect(startX+RESPONSE_SPACING, startY, (int) ((double) endX-startX-2*RESPONSE_SPACING), endY - startY, RECT_ARC_DIM, RECT_ARC_DIM);		
		rect.setRect(startXDbl+spaceDbl, startY, endXDbl-startXDbl-2*spaceDbl, endY - startY);		

		brush.setColor(Color.BLACK);
		brush.draw(rect);
		brush.setColor(RESPONSE_COLOR);
		brush.fill(rect);
		brush.setColor(RESPONSE_NAME_COLOR);
		if (getName()!=null){
			brush.drawString(this.getName(), startX + RESPONSE_SPACING+RESPONSE_NAME_SPACING, (int) (startY + brush.getFont().getSize() + RESPONSE_NAME_SPACING));
		}
		brush.setColor(Color.BLACK);
		brush.draw(rect);

	}
}
