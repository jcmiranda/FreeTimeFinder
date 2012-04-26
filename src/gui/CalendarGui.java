package gui;

import static gui.GuiConstants.FRAME_HEIGHT;
import static gui.GuiConstants.FRAME_WIDTH;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.joda.time.DateTime;


import cal_master.Communicator;
import cal_master.NameIDPair;
import calendar.CalendarGroup;
import calendar.CalendarResponses;
import calendar.CalendarSlots;

public class CalendarGui {

	private CalendarGroup<CalendarResponses> _responseGroup;
	private CalendarGroup<CalendarSlots> _slotGroup;
	private int _startHour = 0;
	private int _endHour = 24;
	private JFrame _frame;
	private JButton _switch;
	private ReplyPanel _when2MeetCal;
	private JPanel _dayOfWeekLabels;
	private JPanel _hourOfDayLabels;
	private ArrayList<Integer> _hoursOfDay = new ArrayList<Integer>();
	private Communicator _communicator = new Communicator();
	private EventPanel _eventPanel = new EventPanel(_communicator, this);
	private UpdatesPanel _updatesPanel = new UpdatesPanel();

	public static enum DaysOfWeek {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday};

	// Represents the monday of the current week
	private DateTime _thisMonday;

	public CalendarGui(){
		_communicator.startUp();
		
		if(_communicator.hasEvent())  {
			_slotGroup=_communicator.getFirstEvent();
			_thisMonday = _slotGroup.getStartTime().minusDays(_slotGroup.getStartTime().getDayOfWeek()-1);
			_startHour = _slotGroup.getStartTime().getHourOfDay();
			_endHour = _slotGroup.getEndTime().getHourOfDay();
		} else {
			_thisMonday = new DateTime();
			_thisMonday = _thisMonday.minusDays(_thisMonday.getDayOfWeek()-1);
		}
		
		if(_communicator.hasUserCal())
			_responseGroup=_communicator.getUserCal();
		
		 assert _responseGroup != null;
		assert _slotGroup != null;
		System.out.println("User Cal Name" + _responseGroup.getCalGroupType());
		
		_when2MeetCal = new ReplyPanel(_thisMonday, _responseGroup, _slotGroup);
		
		ArrayList<NameIDPair> pairs = _communicator.getNameIDPairs();
		for(NameIDPair pair : pairs) {
			_eventPanel.addEvent(new EventLabel(pair.getName(), pair.getID(), _communicator, this));
		}
		
		
		//_eventPanel.addEvent(new EventLabel("TESTING TESTING", "1234", _communicator, this));
		
		makeDayLabels();
		
		makeHourLabels();
		buildFrame();
	}

	public CalendarGui(CalendarGroup<CalendarResponses> responseGroup, CalendarGroup<CalendarSlots> slotGroup){
		_slotGroup=slotGroup;
		_responseGroup=responseGroup;
		_thisMonday = _slotGroup.getStartTime().minusDays(_slotGroup.getStartTime().getDayOfWeek()-1);

		//		_myCal = new MyPanel(_thisMonday, _responseGroup);
		_when2MeetCal = new ReplyPanel(_thisMonday, _responseGroup, _slotGroup);

		_startHour = slotGroup.getStartTime().getHourOfDay();
		_endHour = slotGroup.getEndTime().getHourOfDay();

		_communicator.startUp();
		ArrayList<NameIDPair> pairs = _communicator.getNameIDPairs();
		for(NameIDPair pair : pairs) {
			_eventPanel.addEvent(new EventLabel(pair.getName(), pair.getID(), _communicator, this));
		}
		
		
		_eventPanel.addEvent(new EventLabel("TESTING TESTING", "1234", _communicator, this));
		
		makeDayLabels();
		makeHourLabels();
		buildFrame();
	}

	public void setSlots(CalendarGroup<CalendarSlots> slotGroup){
		_slotGroup= slotGroup;
		_when2MeetCal.setSlots(_slotGroup);
		_startHour = slotGroup.getStartTime().getHourOfDay();
		_endHour = slotGroup.getEndTime().getHourOfDay();
		_thisMonday = slotGroup.getStartTime().minusDays(slotGroup.getStartTime().getDayOfWeek()-1);
		updateHourLabels();
		updateDayLabels();

	}
	
	public void updateDayLabels(){
		_dayOfWeekLabels.removeAll();
		_dayOfWeekLabels.setLayout(new GridLayout(1, 7, GuiConstants.LINE_SPACING, 0));

		int counter=0;
		for (DaysOfWeek d: DaysOfWeek.values()){
			JPanel dayLabel = new JPanel();
			dayLabel.add(new JLabel(d.name() +" "  + _thisMonday.plusDays(counter).monthOfYear().getAsShortText() + " " + _thisMonday.plusDays(counter).dayOfMonth().get(), SwingConstants.CENTER));
			dayLabel.setBackground(GuiConstants.LABEL_COLOR);
			_dayOfWeekLabels.add(dayLabel);
			counter++;			
		}
		_dayOfWeekLabels.revalidate();
		this.repaint();
	}

	public void makeDayLabels(){

		_dayOfWeekLabels = new JPanel();
		_dayOfWeekLabels.setBackground(GuiConstants.LINE_COLOR);
		_dayOfWeekLabels.setLayout(new GridLayout(1, 7, GuiConstants.LINE_SPACING, 0));

		int counter=0;
		for (DaysOfWeek d: DaysOfWeek.values()){
			System.out.println(d);
			JPanel dayLabel = new JPanel();
			dayLabel.add(new JLabel(d.name() +" "  + _thisMonday.plusDays(counter).monthOfYear().getAsShortText() + " " + _thisMonday.plusDays(counter).dayOfMonth().get(), SwingConstants.CENTER));
			dayLabel.setBackground(GuiConstants.LABEL_COLOR);
			_dayOfWeekLabels.add(dayLabel);
			counter++;
			
		}
	}

	public void updateHourLabels(){
		_hourOfDayLabels.removeAll();
		_hourOfDayLabels.setLayout(new GridLayout(_endHour - _startHour, 1, 0, GuiConstants.LINE_SPACING));

		for (int i=_startHour; i<_endHour; i++){
			JPanel hourLabel = new JPanel();
			hourLabel.add(new JLabel(i+ ":00", SwingConstants.CENTER), SwingConstants.CENTER);
			hourLabel.setBackground(GuiConstants.LABEL_COLOR);
			_hourOfDayLabels.add(hourLabel);
		}
		_hourOfDayLabels.revalidate();
		_hourOfDayLabels.repaint();
		this.repaint();
	}

	public void makeHourLabels(){
		_hourOfDayLabels = new JPanel();
		_hourOfDayLabels.setBackground(GuiConstants.LINE_COLOR);
		_hourOfDayLabels.setLayout(new GridLayout(_endHour - _startHour, 1, 0, GuiConstants.LINE_SPACING));

		System.out.println("End Hour: " + _endHour);
		
		for (int i=_startHour; i<_endHour; i++){
			JPanel hourLabel = new JPanel();
			hourLabel.add(new JLabel(i+ ":00", SwingConstants.CENTER), SwingConstants.CENTER);
			hourLabel.setBackground(GuiConstants.LABEL_COLOR);
			_hourOfDayLabels.add(hourLabel);
		}
		
		
		
	}


	private class InnerWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			System.out.println("Window closing triggered");
			_communicator.saveAll();
		}
	}
	
	public void buildFrame(){
		_frame = new JFrame("Kairos");
		_frame.addWindowListener(new InnerWindowListener());

		JPanel calPanel = new JPanel();
		GroupLayout calLayout = new GroupLayout(calPanel);
		calPanel.setLayout(calLayout);

		_frame.setResizable(false);

		calLayout.setHorizontalGroup(
				calLayout.createSequentialGroup()
				.addComponent(_hourOfDayLabels, GroupLayout.PREFERRED_SIZE, _hourOfDayLabels.getPreferredSize().width,
						GroupLayout.PREFERRED_SIZE)
						.addGroup(calLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(_dayOfWeekLabels, GroupLayout.PREFERRED_SIZE, (int) (FRAME_WIDTH*.75),
										GroupLayout.PREFERRED_SIZE)
										.addComponent(_when2MeetCal, GroupLayout.PREFERRED_SIZE, (int) (FRAME_WIDTH*.75),
												GroupLayout.PREFERRED_SIZE))
				);

		calLayout.setVerticalGroup(
				calLayout.createSequentialGroup()
				.addComponent(_dayOfWeekLabels, GroupLayout.PREFERRED_SIZE, _dayOfWeekLabels.getPreferredSize().height,
						GroupLayout.PREFERRED_SIZE)
						.addGroup(calLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(_hourOfDayLabels, GroupLayout.PREFERRED_SIZE, FRAME_HEIGHT - _dayOfWeekLabels.getPreferredSize().height,
										GroupLayout.PREFERRED_SIZE)
										.addComponent(_when2MeetCal, GroupLayout.PREFERRED_SIZE, FRAME_HEIGHT - _dayOfWeekLabels.getPreferredSize().height,
												GroupLayout.PREFERRED_SIZE))
				);

		//		_frame.add(_dayOfWeekLabels, BorderLayout.NORTH);
		//		_frame.add(_hourOfDayLabels, BorderLayout.WEST);
		//		_frame.add(_when2MeetCal, BorderLayout.CENTER);

		_frame.add(calPanel, BorderLayout.CENTER);

		JPanel eastPanel = new JPanel(new GridLayout(0,1));
		eastPanel.add(_eventPanel);
		eastPanel.add(_updatesPanel);
		eastPanel.setPreferredSize(new Dimension((int) (FRAME_WIDTH*.25 - _hourOfDayLabels.getPreferredSize().width), 700));
		_frame.add(eastPanel, BorderLayout.EAST);

		//_frame.add(_eventPanel, BorderLayout.EAST);
		//_frame.add(_updatesPanel, BorderLayout.EAST);
		_switch = new JButton("SWITCH");
		_switch.addActionListener(new MainListener());
		//		_frame.add(_switch, BorderLayout.EAST);
		_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		_frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		_frame.setVisible(true);
	}

	public void nextWeek(){
		_thisMonday = _thisMonday.plusDays(7);
		//		_myCal.nextWeek();
		_when2MeetCal.nextWeek();
	}

	public void lastWeek(){
		_thisMonday = _thisMonday.minusDays(7);
		//		_myCal.lastWeek();
		_when2MeetCal.lastWeek();
	}
	//
	//	public void myView(){
	//		_frame.getContentPane().remove(_when2MeetCal);
	//		_frame.getContentPane().remove(_myCal);
	//		_frame.add(_myCal, BorderLayout.CENTER);
	//
	//		this.repaint();
	//	}
	//
	//	public void replyView(){
	//		_frame.remove(_when2MeetCal);
	//		_frame.remove(_myCal);
	//		_frame.add(_when2MeetCal);
	//		this.repaint();
	//	}


	public void repaint(){
		_frame.invalidate();
		_frame.validate();
		_frame.repaint();
	}

	class MainListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			//			myView();
		}

	}

}
