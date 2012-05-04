package gui;

import java.awt.Graphics;


import static gui.GuiConstants.LINE_COLOR;
import static gui.GuiConstants.DEFAULT_START_HOUR;
import static gui.GuiConstants.DEFAULT_END_HOUR;

import javax.swing.JPanel;

import org.joda.time.DateTime;

public abstract class CalPanel extends JPanel{

	protected int _startHour = DEFAULT_START_HOUR;
	protected int _endHour = DEFAULT_END_HOUR;
	protected int _numHours =  DEFAULT_END_HOUR - DEFAULT_START_HOUR;
//	protected DateTime _thisMonday;
	protected DateTime _startDay;
	protected DateTime _endDay;
	protected DayPanel[] _days;

	public CalPanel(){
		super();
		this.setBackground(LINE_COLOR);
//		_thisMonday = thisMonday;
		_startDay = new DateTime();
		_endDay = _startDay.plusDays(6);
		makeDays();
		this.repaint();
	}
	
	public abstract void makeDays();
	
	public abstract void configDays();

	
	//TODO FIX THESE
//	public void nextWeek(){
//		_thisMonday= _thisMonday.plusDays(7);
//		for (DayPanel d: _days){
//			d.nextWeek();
//		}
//	}
//
//	public void lastWeek(){
//		_thisMonday = _thisMonday.minusDays(7);
//		for (DayPanel d: _days){
//			d.lastWeek();
//		}
//	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);
//		for(DayPanel day: _days){
//			day.repaint();
//		}
	}



}
