package gui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.joda.time.DateTime;

import calendar.CalendarResponses;

public class OuterDayPanel extends JPanel{

	private DayPanel _day;
	private ClickableDayPanel _clickableDay;
	private DateTime _today;
	private JLabel _dateOfWeekLabel;
	private JPanel _labelPanel;
	
	public OuterDayPanel(ClickableDayPanel clickableDay, DayPanel day, DateTime today){
		super();
		_day = day;
		_clickableDay = clickableDay;
		
		_dateOfWeekLabel=new JLabel(today.dayOfWeek().getAsShortText());
		_dateOfWeekLabel.setFont(new Font(GuiConstants.FONT_NAME, _dateOfWeekLabel.getFont().getStyle(), _dateOfWeekLabel.getFont().getSize() - 1));
		
		GridBagConstraints c = new GridBagConstraints();

		setLayout(new GridBagLayout());
		this.setBackground(GuiConstants.LINE_COLOR);

		_labelPanel = new JPanel();
		_labelPanel.setBackground(GuiConstants.LABEL_COLOR);
		_labelPanel.add(_dateOfWeekLabel, JPanel.CENTER_ALIGNMENT);
		
		JPanel dayGroup = new JPanel();
		dayGroup.setLayout(new GridLayout(1,2,GuiConstants.LINE_SPACING,0));
		dayGroup.add(clickableDay);
		dayGroup.add(day);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weighty = 0.0;
		c.weightx = 1.0;
		c.insets = new Insets(0,0,1,0);
		c.gridx = 0;
		c.gridy = 0;
		add(_labelPanel, c);

		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.insets = new Insets(0,0,0,0);
		c.gridx = 0;
		c.gridy = 1;
		add(dayGroup, c);

	}
		
	public int getLabelHeight(){
		return _labelPanel.getPreferredSize().height;
	}
	
	public DayPanel getDay(){
		return _day;
	}

	public DayPanel getClickableDay(){
		return _clickableDay;
	}

	public void setStartHour(int start){
		_day.setStartHour(start);
		_clickableDay.setStartHour(start);	
	}

	public void setNumHours(int num){
		_day.setNumHours(num);
		_clickableDay.setNumHours(num);	
	}

	public void setActive(Boolean active){
		_day.setActive(active);
		_clickableDay.setActive(active);
	}

	public void setBestTimes(CalendarResponses bestTimes){
		_day.setBestTimes(bestTimes);
	}

	public void setDay(DateTime today){
		_day.setDay(today);
		_clickableDay.setDay(today);
		_today = today;
		_dateOfWeekLabel.setText(_today.dayOfWeek().getAsShortText() + " " + _today.monthOfYear().getAsShortText() + " " + _today.getDayOfMonth());
		this.repaint();
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);
		this.revalidate();

	}
}
