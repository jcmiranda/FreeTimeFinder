package gui;

import static gui.GuiConstants.DEFAULT_END_HOUR;
import static gui.GuiConstants.DEFAULT_START_HOUR;
import static gui.GuiConstants.FRAME_HEIGHT;
import static gui.GuiConstants.FRAME_WIDTH;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import cal_master.Communicator;
import cal_master.NameIDPair;
import calendar.CalendarResponses;
import calendar.Event;
import calendar.Event.PaintMethod;
import calendar.UserCal;
import calendar.When2MeetEvent;

public class CalendarGui {

	private Event _slotGroup = null;
	private CalendarResponses _bestTimes = null;
	private int _bestTimesDuration = -1;
	private int _startHour = DEFAULT_START_HOUR;
	private int _numHours = DEFAULT_END_HOUR - DEFAULT_START_HOUR;
	private JFrame _frame;
	private ReplyPanel _replyPanel;
	private Communicator _communicator = new Communicator();
	private UserCalPanel _userCalPanel;
	private PaintMethod _eventDispStyle = PaintMethod.Bars;
	private EventPanel _eventPanel = new EventPanel(_communicator, this);
	private UpdatesPanel _updatesPanel = new UpdatesPanel();
	private FriendBar _friendBar = new FriendBar(this);

	//ImageIcons for different buttons on main display

	private ImageIcon _findTimeIcon = new ImageIcon("small_logo_button.png");
	private ImageIcon _findTimeIconInverted = new ImageIcon("small_logo_button_invert.png");

	private ImageIcon _toggleIcon = new ImageIcon("small_switch_button.png");
	private ImageIcon _toggleIconInverted = new ImageIcon("small_switch_button_invert.png");

	private ImageIcon _refreshIcon = new ImageIcon("small_refresh_button.png");
	private ImageIcon _refreshIconInverted = new ImageIcon("small_refresh_button_invert.png");

	private ImageIcon _submitIcon = new ImageIcon("small_submit_button.png");
	private ImageIcon _submitIconInverted = new ImageIcon("small_submit_button_invert.png");

	private ImageIcon _prevIcon = new ImageIcon("small_left_button.png");
	private ImageIcon _prevIconInverted = new ImageIcon("small_left_button_invert.png");

	private ImageIcon _nextIcon = new ImageIcon("small_right_button.png");
	private ImageIcon _nextIconInverted = new ImageIcon("small_right_button_invert.png");

	private ImageIcon _kairosLogo = new ImageIcon("KairosLogo.png");
	private ImageIcon _kairosIcon = new ImageIcon("KairosIcon.png");

	private JToggleButton _refreshButton = new JToggleButton(_refreshIcon);
	private JButton _eventDispButton = new JButton(_toggleIcon);
	private JToggleButton _timeFindButton = new JToggleButton(_findTimeIcon);
	private JButton _submitButton = new JButton(_submitIcon);
	private JButton _nextButton = new JButton(_nextIcon);
	private JButton _prevButton = new JButton(_prevIcon);
	public static enum DaysOfWeek {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday};
	private JLabel _picLabel;

	public CalendarGui() throws URISyntaxException{

		this.displayButton(_refreshButton);
		this.displayButton(_eventDispButton);
		this.displayButton(_timeFindButton);
		this.displayButton(_submitButton);
		this.displayButton(_prevButton);
		this.displayButton(_nextButton);


		_communicator.startUp();

		_startHour = 9;
		_friendBar.setEvent(null);

		UserCal userCal = null;

		if(_communicator.hasUserCal())
			userCal =_communicator.getUserCal();

		_replyPanel = new ReplyPanel();

		ArrayList<NameIDPair> pairs = _communicator.getNameIDPairs();
		for(NameIDPair pair : pairs) {
			_eventPanel.addEvent(new EventLabel(pair.getName(), pair.getID(), _communicator, this));
		}

		_userCalPanel = new UserCalPanel(_communicator, this);

		// Instantiate Buttons and set Icons;
		_submitButton.addActionListener(new SubmitListener());
		_submitButton.setToolTipText("Submit Response");
		_submitButton.setPressedIcon(_submitIconInverted);

		_timeFindButton.addActionListener(new TimeFindListener());
		_timeFindButton.setToolTipText("Find Best Times");
		_timeFindButton.setSelectedIcon(_findTimeIconInverted);

		_nextButton.addActionListener(new NextListener());
		_nextButton.setPressedIcon(_nextIconInverted);
		_nextButton.setToolTipText("Next");

		_prevButton.addActionListener(new PrevListener());
		_prevButton.setFocusable(false);
		_prevButton.setPressedIcon(_prevIconInverted);
		_prevButton.setToolTipText("Previous");

		_eventDispButton.addActionListener(new EventDispButtonListener());
		_eventDispButton.setFocusable(false);
		_eventDispButton.setToolTipText("Toggle Display");

		_refreshButton.addActionListener(new RefreshListener());
		_refreshButton.setFocusable(false);
		_refreshButton.setToolTipText("Refresh");
		_refreshButton.setPressedIcon(_refreshIconInverted);

		_picLabel = new JLabel(_kairosLogo);

		_numHours = 8;

		buildFrame();

		_replyPanel.setUserCal(userCal);
		_replyPanel.setEvent(null);
	}

	// Set default behavior for a button
	public void displayButton(AbstractButton button) {
		button.setBorderPainted(false);  
		button.setContentAreaFilled(false);  
		button.setFocusPainted(false);  
		button.setOpaque(false); 
	}

	public Event getEvent(){
		return _slotGroup;
	}


	// Sets the current event viewed in the gui
	public void setEvent(Event event){
		_slotGroup= event;

		if(_slotGroup != null){
			_slotGroup.init();
			_slotGroup.setPaintMethod(_eventDispStyle);
			_eventPanel.setSelectedEvent(String.valueOf(_slotGroup.getID()));
		}
		else{
			_eventPanel.setSelectedEvent(null);
		}

		_replyPanel.setEvent(_slotGroup);
		UserCal userCal = _communicator.getUserCal();
		_replyPanel.setUserCal(userCal);
		_replyPanel.repaint();
		if(_slotGroup != null){
			_startHour = event.getStartTime().getHourOfDay();
			_numHours = event.getNumHours();
		}
		else{
			_startHour = 9;
			_numHours = 8;
		}

		_bestTimes = null;
		_bestTimesDuration = -1;
		_replyPanel.setBestTimes(_bestTimes);
		_timeFindButton.setSelected(false);

		_updatesPanel.setEvent(_slotGroup);
		_friendBar.setEvent(_slotGroup);
		_eventPanel.refresh();

	}

	public void setUserCal(UserCal userCal){
		_replyPanel.setUserCal(userCal);
	}

	//Lays out components in the main frame
	public void buildFrame(){

		_frame = new JFrame("Kairos");

		JPanel calPanel = new JPanel();
		GroupLayout calLayout = new GroupLayout(calPanel);
		calPanel.setLayout(calLayout);

		_frame.setResizable(true);

		calLayout.setHorizontalGroup(
				calLayout.createSequentialGroup()
				.addComponent(_replyPanel, GroupLayout.PREFERRED_SIZE, (int) (FRAME_WIDTH*.70),
						GroupLayout.PREFERRED_SIZE));

		calLayout.setVerticalGroup(
				calLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(_replyPanel, GroupLayout.PREFERRED_SIZE, FRAME_HEIGHT - _replyPanel.getPreferredSize().height - 25,
						GroupLayout.PREFERRED_SIZE));

		_frame.add(calPanel, BorderLayout.CENTER);

		// Panel holding next and previous week buttons
		JPanel nextPrevPanel = new JPanel();
		nextPrevPanel.add(_prevButton);
		nextPrevPanel.add(_nextButton);

		// Panel holding buttons that provide basic functionality
		// Submit, Toggle View, FindBestTimes, Refresh
		JPanel buttonFunctionsPanel = new JPanel();

		buttonFunctionsPanel.add(_eventDispButton);
		buttonFunctionsPanel.add(_submitButton);
		buttonFunctionsPanel.add(_timeFindButton);
		buttonFunctionsPanel.add(_refreshButton);

		JPanel logoPanel = new JPanel();
		logoPanel.add(_picLabel);

		JPanel northPanel = new JPanel(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		c.ipady = 10;

		northPanel.add(logoPanel,c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridx = 1;
		c.gridy = 0;
		c.ipady = 0;

		northPanel.add(nextPrevPanel, c);

		c.fill = GridBagConstraints.EAST;
		c.weightx = 0.0;
		c.weighty = 1.0;
		c.gridx = 2;
		c.gridy = 0;
		c.ipady = 0;

		northPanel.add(buttonFunctionsPanel, c);

		_frame.add(northPanel, BorderLayout.NORTH);

		JPanel westPanel = new JPanel(new GridLayout(0, 1));
		westPanel.add(_friendBar);
		westPanel.setPreferredSize(new Dimension((int) ((FRAME_WIDTH*.25*.40)), 400));
		_friendBar.mySetSize(new Dimension((int) ((FRAME_WIDTH*.25*.40)), 400));
		_frame.add(westPanel, BorderLayout.WEST);

		JPanel eastPanel = new JPanel(new GridLayout(0,1));
		eastPanel.add(_userCalPanel);
		eastPanel.add(_eventPanel);
		eastPanel.add(_updatesPanel);

		eastPanel.setPreferredSize(new Dimension((int) (FRAME_WIDTH*.25), 600));

		_frame.add(eastPanel, BorderLayout.EAST);

		_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		_frame.pack();
		_frame.setVisible(true);
	}

	// Posts the user's availability input to the event
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
			_bestTimesDuration = duration;
			_bestTimes = _communicator.getBestTimes(String.valueOf(_slotGroup.getID()), duration);
			_replyPanel.setBestTimes(_bestTimes);
			repaint();
		}
	}

	public void updateBestTimes(){
		if(_bestTimes != null && _bestTimesDuration >= 0)
			setBestTimes(_bestTimesDuration);
	}

	private class SubmitListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(_slotGroup != null){
				int selection = JOptionPane.showConfirmDialog(null,"Are you sure you want to submit?", "", 
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, _kairosIcon);
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

	private class EventDispButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			if(_slotGroup != null) {
				if(_eventDispStyle == PaintMethod.Bars){
					_eventDispStyle = PaintMethod.HeatMap;
				} else if(_eventDispStyle == PaintMethod.HeatMap) {
					_eventDispStyle = PaintMethod.Bars;
				}

				if(_eventDispButton.getIcon() == _toggleIcon) {
					_eventDispButton.setIcon(_toggleIconInverted);
				}
				else {
					_eventDispButton.setIcon(_toggleIcon);
				}

				CalendarGui.this.getEvent().setPaintMethod(_eventDispStyle);
				repaint();
			}
		}
	}

	private class TimeFindListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			System.out.println(_timeFindButton.isSelected());
			if(_timeFindButton.isSelected()){
				if(_slotGroup != null && !_slotGroup.getCalendars().isEmpty() && !(_slotGroup.getCalendars().size() ==1 && _slotGroup.userHasSubmitted())){
					new SliderPane(_numHours, CalendarGui.this);
				}
			}
			else{
				_bestTimes = null;
				_bestTimesDuration = -1;
				_replyPanel.setBestTimes(_bestTimes);
				repaint();
			}
		}

	}

	private class RefreshListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			URL url=null;
			try {
				url = new URL("http://www.google.com");
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			if (Communicator.webConnected(url)) {

				_communicator.refresh();
				// Retrieve this when2meet in case it has changed
				if(_slotGroup != null){
					setEvent(_communicator.getEvent(""+_slotGroup.getID()));
					System.out.println("After setting event in GUI");
					_slotGroup.printUpdates();
					System.out.println("=====");
				}

				setUserCal(_communicator.getUserCal());
				_userCalPanel.initLabels();
				_refreshButton.setSelected(false);
				repaint();
			}
			else {
				ImageIcon grey = new ImageIcon("small_logo_button.png");
				JOptionPane.showMessageDialog(null, "You are not connected to the Internet.\nKairos cannot import current data.", "Connection Error", JOptionPane.ERROR_MESSAGE, grey);
			}
		}



	}

}
