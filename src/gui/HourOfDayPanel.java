package gui;

import static gui.GuiConstants.HOUR_LABEL_SPACING;
import static gui.GuiConstants.LABEL_COLOR;
import static gui.GuiConstants.LINE_COLOR;
import static gui.GuiConstants.RESPONSE_NAME_COLOR;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class HourOfDayPanel extends JPanel{

	private int _startHour;
	private int _numHours;

	public HourOfDayPanel(int startHour, int numHours){
		super();
		_startHour = startHour;
		_numHours = numHours;
		this.setBackground(LABEL_COLOR);
	}

	public void updateHours(int startHour, int numHours){
		_startHour = startHour;
		_numHours = numHours;
		repaint();
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D brush = (Graphics2D) g;

		brush.setFont(new Font(GuiConstants.FONT_NAME, brush.getFont().getStyle(), brush.getFont().getSize()));
		
		double hrsDbl = (double) _numHours;
		double heightDbl = (double) this.getHeight();
		for (int i=_startHour; i< _startHour + _numHours; i++){
			int j = i - _startHour;
			double iDbl = (double) j;
			int height = (int) ((iDbl * heightDbl) / hrsDbl);
			
			brush.setColor(LINE_COLOR);
			brush.drawLine(0, height, this.getWidth(), height);
			
			brush.setColor(RESPONSE_NAME_COLOR);
			brush.drawString(i + ":00", HOUR_LABEL_SPACING, height + brush.getFont().getSize() + HOUR_LABEL_SPACING);
		}
	}
	
}
