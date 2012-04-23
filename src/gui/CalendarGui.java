package gui;

import static gui.GuiConstants.FRAME_HEIGHT;
import static gui.GuiConstants.FRAME_WIDTH;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.joda.time.DateTime;

import calendar.CalendarGroup;
import calendar.CalendarResponses;
import calendar.CalendarSlots;

public class CalendarGui {

	private CalendarGroup<CalendarResponses> _responseGroup;
	private CalendarGroup<CalendarSlots> _slotGroup;
	private JFrame _frame;
	private JButton _switch;
	private CalPanel _myCal;
	private CalPanel _when2MeetCal;

	// Represents the monday of the current week
	private DateTime _thisMonday;

	public CalendarGui(){
		_thisMonday = new DateTime();
		_myCal = new MyPanel(_thisMonday, _responseGroup);
		_when2MeetCal = new ReplyPanel(_thisMonday);
		buildFrame();
	}

	public CalendarGui(CalendarGroup<CalendarResponses> responseGroup, CalendarGroup<CalendarSlots> slotGroup){
		_slotGroup=slotGroup;
		_responseGroup=responseGroup;
		_thisMonday = _slotGroup.getStartTime().minusDays(_slotGroup.getStartTime().getDayOfWeek()-1);

		_myCal = new MyPanel(_thisMonday, _responseGroup);
		_when2MeetCal = new ReplyPanel(_thisMonday, _responseGroup, _slotGroup);
		_when2MeetCal.setName("w2m");
		_myCal.setName("mc");
		buildFrame();
	}

	public void buildFrame(){
		_frame = new JFrame("Kairos");
		//		_frame.add(_myCal, BorderLayout.CENTER);
		_frame.add(_when2MeetCal, BorderLayout.CENTER);
		_switch = new JButton("SWITCH");
		_switch.addActionListener(new MainListener());
		_frame.add(_switch, BorderLayout.EAST);
		_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		_frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		_frame.setVisible(true);
	}

	public void nextWeek(){
		_thisMonday = _thisMonday.plusDays(7);
		_myCal.nextWeek();
		_when2MeetCal.nextWeek();
	}

	public void lastWeek(){
		_thisMonday = _thisMonday.minusDays(7);
		_myCal.lastWeek();
		_when2MeetCal.lastWeek();
	}

	public void myView(){
		for (Component c: _frame.getContentPane().getComponents()){
			System.out.println(c.getName());
		}
		_frame.getContentPane().remove(_when2MeetCal);
		_frame.getContentPane().remove(_myCal);
		_frame.add(_myCal, BorderLayout.CENTER);

		this.repaint();
	}

	public void replyView(){
		_frame.remove(_when2MeetCal);
		_frame.remove(_myCal);
		_frame.add(_when2MeetCal);
		this.repaint();
	}

	
	public void repaint(){
		_frame.invalidate();
		_frame.validate();
	}

	class MainListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
//			if (_frame.getContentPane().contains(_when2MeetCal)){
//				myView();
//			}
			myView();
		}

	}

}
