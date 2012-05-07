package calendar;
import static gui.GuiConstants.LINE_COLOR;
import static gui.GuiConstants.INTERLINE_SPACING;
import static gui.GuiConstants.RESPONSE_NAME_COLOR;
import static gui.GuiConstants.RESPONSE_NAME_SPACING;
import static gui.GuiConstants.RESPONSE_SPACING;
import gui.Day;
import gui.DayPanel;
import gui.GuiConstants;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

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

	public void paint(Graphics2D brush, Day d, int startX, int endX, Color color){

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
		rect.setRect(startXDbl+spaceDbl, startY, endXDbl-startXDbl-2*spaceDbl, endY - startY);		

		brush.setColor(color);
		brush.fill(rect);
		brush.setColor(RESPONSE_NAME_COLOR);
		brush.setFont(new Font(GuiConstants.FONT_NAME, brush.getFont().getStyle(), brush.getFont().getSize()));
		if (getName()!=null){
			drawStringRect(brush,
					(int) (startXDbl+spaceDbl + RESPONSE_NAME_SPACING),
					(int) (startY + RESPONSE_NAME_SPACING),
					(int) (endXDbl - spaceDbl - RESPONSE_NAME_SPACING),
					(int) (endY - brush.getFont().getSize() - RESPONSE_NAME_SPACING),
					INTERLINE_SPACING,
					this.getName());
		}
		brush.setColor(LINE_COLOR);
		brush.draw(rect);
	}
	
	 private void drawStringRect(Graphics2D graphics, int x1, int y1, int x2, int y2, 
		        float interline, String txt) {
		        AttributedString as = new AttributedString(txt);
		        as.addAttribute(TextAttribute.FOREGROUND, graphics.getPaint());
		        as.addAttribute(TextAttribute.FONT, graphics.getFont());
		        AttributedCharacterIterator aci = as.getIterator();
		        FontRenderContext frc = new FontRenderContext(null, true, false);
		        LineBreakMeasurer lbm = new LineBreakMeasurer(aci, frc);
		        float width = x2 - x1;

		        while (lbm.getPosition() < txt.length()) {
		            TextLayout tl = lbm.nextLayout(width);
		            y1 += tl.getAscent();
		            tl.draw(graphics, x1, y1);
		            y1 += tl.getDescent() + tl.getLeading() + (interline - 1.0f) * tl.getAscent();
		            if (y1 > y2) {
		                break;
		            }
		        }
		    }
	

}
