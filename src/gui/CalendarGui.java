package gui;

import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;
import static gui.GuiConstants.*;
import calendar.*;

public class CalendarGui {


	CalendarGroup _calGroup;
	JFrame _frame;
	CalPanel _calendar;

	
	public CalendarGui(CalendarGroup calGroup){
		_calGroup=calGroup;
		this.build();
	}
	
	public void build(){

		_frame = new JFrame("FTF");
		_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		_frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);

		_calendar = new CalPanel(_calGroup);
		_frame.add(_calendar);
		_frame.setVisible(true);
	}




}
