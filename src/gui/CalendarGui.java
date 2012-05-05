package gui;

import static gui.GuiConstants.FRAME_HEIGHT;
import static gui.GuiConstants.FRAME_WIDTH;

import static gui.GuiConstants.DEFAULT_START_HOUR;
import static gui.GuiConstants.DEFAULT_END_HOUR;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.joda.time.DateTime;

import cal_master.Communicator;
import cal_master.NameIDPair;
import calendar.CalendarGroup;
import calendar.CalendarResponses;
import calendar.Event;
import calendar.When2MeetEvent;

public class CalendarGui {

	private CalendarGroup<CalendarResponses> _responseGroup = null;
	private Event _slotGroup = null;
	private int _startHour = DEFAULT_START_HOUR;
	private int _numHours = DEFAULT_END_HOUR - DEFAULT_START_HOUR;
	private JFrame _frame;
	private ReplyPanel _replyPanel;
	private JPanel _dayOfWeekLabels;
	private JPanel _hourOfDayLabels;
	private ArrayList<Integer> _hoursOfDay = new ArrayList<Integer>();
	private Communicator _communicator = new Communicator();
	private UserCalPanel _userCalPanel;
	private EventPanel _eventPanel = new EventPanel(_communicator, this);
	private UpdatesPanel _updatesPanel = new UpdatesPanel();
	private FriendBar _friendBar = new FriendBar(this);
	private JButton _submitButton = new JButton("Submit Response");
	private JButton _timeFindButton = new JButton("Find Best Times");
	private JButton _nextButton = new JButton(">");
	private JButton _prevButton = new JButton("<");
	private JButton _refreshButton = new JButton("Refresh");
	public static enum DaysOfWeek {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday};

	public CalendarGui(){
		_communicator.startUp();

		//		if(_communicator.hasEvent())  {
		//
		//			Event toReturn = _communicator.getW2M(_communicator.getFirstEventID());
		//			_slotGroup = toReturn;
		//			_slotGroup.init();
		//			_startHour = _slotGroup.getStartTime().getHourOfDay();
		//			_friendBar.setEvent(_slotGroup);
		//		}
		//		else {
		_startHour = 9;
		_friendBar.setEvent(null);
		//		}

		if(_communicator.hasUserCal())
			_responseGroup=_communicator.getUserCal();

		_replyPanel = new ReplyPanel(_responseGroup, _slotGroup);

		ArrayList<NameIDPair> pairs = _communicator.getNameIDPairs();
		for(NameIDPair pair : pairs) {
			_eventPanel.addEvent(new EventLabel(pair.getName(), pair.getID(), _communicator, this));
		}

		_userCalPanel = new UserCalPanel(_communicator, this);

		_submitButton.addActionListener(new SubmitListener());
		_timeFindButton.addActionListener(new TimeFindListener());
		_nextButton.addActionListener(new NextListener());
		_prevButton.addActionListener(new PrevListener());

		_refreshButton.addActionListener(new RefreshListener());
		if(_slotGroup != null)
			_numHours = _slotGroup.getCalendars().get(0).getNumHours();
		else
			_numHours = 8;
		makeHourLabels();
		buildFrame();
	}

	public Event getEvent(){
		return _slotGroup;
	}

	public void setEvent(Event event){
		_slotGroup= event;
		if(_slotGroup != null)
			_slotGroup.init();
		_responseGroup = _communicator.getUserCal();
		System.out.println("SLOT GROUP IN SET EVENT: " + _slotGroup);
		_replyPanel.setSlots(_slotGroup);
		System.out.println("Setting event for reply panel");
		_replyPanel.setResps(_responseGroup);
		_replyPanel.repaint();
		if(_slotGroup != null){
			_startHour = event.getStartTime().getHourOfDay();
			_numHours = event.getNumHours();
		}
		else{
			_startHour = 9;
			_numHours = 8;
		}
		_updatesPanel.setEvent(_slotGroup);
		_friendBar.setEvent(_slotGroup);
		updateHourLabels();
		_eventPanel.refresh();

	}


	public void setResponses(CalendarGroup<CalendarResponses> responseGroup){
		_responseGroup= responseGroup;
		_replyPanel.setResps(_responseGroup);
	}


	public void updateHourLabels(){
		_hourOfDayLabels.removeAll();
		_hourOfDayLabels.setLayout(new GridLayout(_numHours, 1, 0, 1));

		for (int i=_startHour; i<_startHour + _numHours; i++){
			JPanel hourLabel = new JPanel();
			hourLabel.add(new JLabel(i+ ":00", SwingConstants.CENTER), SwingConstants.CENTER);
			hourLabel.setBackground(GuiConstants.LABEL_COLOR);
			_hourOfDayLabels.add(hourLabel);
		}
		_hourOfDayLabels.revalidate();
		_hourOfDayLabels.repaint();
		this.repaint();
		System.out.println("BOOOOP");
	}

	public void makeHourLabels(){
		//		_hourOfDayLabels = new JPanel();
		//		_hourOfDayLabels.setBackground(GuiConstants.LINE_COLOR);
		//		_hourOfDayLabels.setLayout(new GridLayout(_numHours, 1, 0, 1));
		//		_hourOfDayLabels.setBorder(new EmptyBorder(0,0,0,0));
		//		
		//		for (int i=_startHour; i<_startHour + _numHours; i++){
		//			JPanel hourLabel = new JPanel();
		//			hourLabel.add(new JLabel(i+ ":00", SwingConstants.CENTER), SwingConstants.CENTER);
		//			hourLabel.setBorder(new EmptyBorder(0,0,0,0));
		//			hourLabel.setBackground(GuiConstants.LABEL_COLOR);
		//			_hourOfDayLabels.add(hourLabel);
		//		}
		_hourOfDayLabels = new JPanel();
		_hourOfDayLabels.setBackground(GuiConstants.LINE_COLOR);
		_hourOfDayLabels.setLayout(new GridBagLayout());
//		_hourOfDayLabels.setBorder(new EmptyBorder (0,0,0,0));
		GridBagConstraints c = new GridBagConstraints();

		for (int i=_startHour; i<_startHour + _numHours; i++){
			JPanel hourLabel = new JPanel();
			hourLabel.setBorder(null);
//			hourLabel.setBorder(new EmptyBorder (0,0,0,0));
			hourLabel.add(new JLabel(i+ ":00", SwingConstants.CENTER));
			hourLabel.setBackground(GuiConstants.LABEL_COLOR);
			c.weightx = 1.0;


			if (i==0){
				c.fill = GridBagConstraints.BOTH;
				c.insets = new Insets(0,0,0,0);
				c.weighty = 1.0;
			}
			else if (i==_startHour + _numHours -1) {
				c.fill = GridBagConstraints.BOTH;
				c.insets = new Insets(0,0,0,0);
				c.weighty = 1.0;
			} else if (i==_startHour + _numHours -2) {
				c.fill = GridBagConstraints.BOTH;
				c.insets = new Insets(1,0,1,0);
				c.weighty = 1.0;
			}
			else{
				c.fill = GridBagConstraints.BOTH;
				c.insets = new Insets(1,0,0,0);
				c.weighty = 1.0;
			}
			c.gridx = 0;
			c.gridy = i - _startHour;
			_hourOfDayLabels.add(hourLabel, c);
		}


	}


	private class InnerWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			//System.out.println("Window closing triggered");
			//_communicator.saveAll();
		}
	}

	public void buildFrame(){
		_frame = new JFrame("Kairos");
		_frame.addWindowListener(new InnerWindowListener());

		JPanel calPanel = new JPanel();
		GroupLayout calLayout = new GroupLayout(calPanel);
		calPanel.setLayout(calLayout);

		// TODO change to false
		_frame.setResizable(true);

		calLayout.setHorizontalGroup(
				calLayout.createSequentialGroup()
				.addComponent(_hourOfDayLabels, GroupLayout.PREFERRED_SIZE, _hourOfDayLabels.getPreferredSize().width,
						GroupLayout.PREFERRED_SIZE)
						.addComponent(_replyPanel, GroupLayout.PREFERRED_SIZE, (int) (FRAME_WIDTH*.75),
								GroupLayout.PREFERRED_SIZE));

		calLayout.setVerticalGroup(
				calLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(_replyPanel, GroupLayout.PREFERRED_SIZE, FRAME_HEIGHT - _replyPanel.getPreferredSize().height,
						GroupLayout.PREFERRED_SIZE)
						.addComponent(_hourOfDayLabels, GroupLayout.PREFERRED_SIZE, FRAME_HEIGHT - _replyPanel.getPreferredSize().height - _replyPanel.getWeekDayPanelHeight(),
								GroupLayout.PREFERRED_SIZE));

		_frame.add(calPanel, BorderLayout.CENTER);

		JPanel submitPanel = new JPanel();
		submitPanel.add(_submitButton);
		JPanel timeFindPanel = new JPanel();
		timeFindPanel.add(_timeFindButton);

		JPanel prevPanel = new JPanel();
		prevPanel.add(_prevButton);
		prevPanel.add(_nextButton);
//		JPanel nextPanel = new JPanel();
//		nextPanel.add(_nextButton);

		JPanel refreshPanel = new JPanel();
		refreshPanel.add(_refreshButton);


		JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
		buttonPanel.add(prevPanel);
//		buttonPanel.add(nextPanel);
		buttonPanel.add(submitPanel);
		buttonPanel.add(timeFindPanel);
		buttonPanel.add(refreshPanel);

		JPanel northPanel = new JPanel(new GridLayout(2,1));
		northPanel.add(buttonPanel);
		northPanel.add(_friendBar);

		_frame.add(northPanel, BorderLayout.NORTH);

		JPanel eastPanel = new JPanel(new GridLayout(0,1));
		eastPanel.add(_userCalPanel);
		eastPanel.add(_eventPanel);
		eastPanel.add(_updatesPanel);
		eastPanel.setPreferredSize(new Dimension((int) (FRAME_WIDTH*.25 - _hourOfDayLabels.getPreferredSize().width), 700));
		_frame.add(eastPanel, BorderLayout.EAST);

		_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		_frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		_frame.setVisible(true);
	}

	public void replyToEvent(){
		if(_slotGroup != null && _replyPanel.getClicks() != null)
			_communicator.submitResponse(Integer.toString(((When2MeetEvent) _slotGroup).getID()), _replyPanel.getClicks());
	}


	public void repaint(){
		if(_frame != null){
			_frame.invalidate();
			_frame.validate();
			_frame.repaint();
		}
	}

	public void setBestTimes(int duration){
		if(_slotGroup != null){
			CalendarResponses bestTimes = _communicator.getBestTimes(String.valueOf(_slotGroup.getID()), duration);
			_replyPanel.setBestTimes(bestTimes);
			repaint();
		}
	}

	private class SubmitListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(_slotGroup != null){
				int selection = JOptionPane.showConfirmDialog(null,"Are you sure you want to submit?", "", 
						JOptionPane.YES_NO_OPTION);
				if(selection == JOptionPane.YES_OPTION)
					replyToEvent();
			}
		}

	}

	private class NextListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(_slotGroup != null)
				_replyPanel.nextWeek();
		}

	}
	private class PrevListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(_slotGroup != null)
				_replyPanel.prevWeek();
		}

	}

	private class TimeFindListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(_slotGroup != null && !_slotGroup.getCalendars().isEmpty() && !(_slotGroup.getCalendars().size() ==1 && _slotGroup.userHasSubmitted()))
				new SliderPane(_numHours, CalendarGui.this);
		}

	}

	private class RefreshListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			_communicator.refresh();
			// Retrieve this when2meet in case it has changed
			if(_slotGroup != null){
				setEvent(_communicator.getW2M(""+_slotGroup.getID()));
				System.out.println("After setting event in GUI");
				_slotGroup.printUpdates();
				System.out.println("=====");
			}
			
			setResponses(_communicator.getUserCal());

			repaint();
		}



	}

}
