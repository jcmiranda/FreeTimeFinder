package gui;

import java.awt.Graphics;

import static gui.GuiConstants.LINE_COLOR;

import javax.swing.JPanel;

import org.joda.time.DateTime;

public abstract class CalPanel extends JPanel{


	protected int _startHour = 0;
	protected int _endHour = 24;
	protected int _numHours = 24;
	protected DateTime _thisMonday;
	protected DayPanel[] _days;

	public CalPanel(DateTime thisMonday){
		super();
		this.setBackground(LINE_COLOR);
		_thisMonday = thisMonday;
		makeDays();
		this.repaint();
	}
	
	public abstract void makeDays();
	
	public abstract void configDays();

	public void nextWeek(){
		_thisMonday= _thisMonday.plusDays(7);
		for (DayPanel d: _days){
			d.nextWeek();
		}
	}

	public void lastWeek(){
		_thisMonday = _thisMonday.minusDays(7);
		for (DayPanel d: _days){
			d.lastWeek();
		}
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);
		for(DayPanel day: _days){
			day.repaint();
		}
	}



}
