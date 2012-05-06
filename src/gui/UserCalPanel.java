package gui;

import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import cal_master.Communicator;
import calendar.CalendarGroup;
import calendar.CalendarResponses;

/**
 * 
 * Represents the panel that stores all the labels representing the sub-calendars of the user's calendar.
 * Includes a title (to alert the user of the use of this panel) and a scroll pane containing the names of all
 * the user's subcalendars with check marks beside those the user wishes to display
 *
 */

public class UserCalPanel extends JPanel {
	
	private ArrayList<UserCalLabel> _labels = new ArrayList<UserCalLabel>();
	private Communicator _communicator;
	private CalendarGui _gui;
	private JLabel _titleLabel;
	private JScrollPane _labelScrollPane;
	private JPanel _scrollPaneInner = new JPanel();
	private GroupLayout _layout= new GroupLayout(this);
	
	public UserCalPanel(Communicator communicator, CalendarGui gui){
		_communicator = communicator;
		_gui = gui;
		
		_titleLabel = new JLabel("My Calendar");
		Font newLabelFont = new Font(GuiConstants.FONT_NAME,Font.BOLD, _titleLabel.getFont().getSize());  
		_titleLabel.setFont(newLabelFont);
		
		_labelScrollPane = new JScrollPane(_scrollPaneInner);
		
		this.initLabels();
	}
	
	/**
	 * Sets up the layout of all the labels (grid layout inside scrollpane, title label above scroll pane)
	 */
	private void setUp(){
		_scrollPaneInner.removeAll();
		_scrollPaneInner.setLayout(new GridLayout(0, 1));
		
		if(_labels.size() > 0)
			for(UserCalLabel label : _labels){
				_scrollPaneInner.add(label);
			}
		
		this.setLayout(_layout);
		_layout.setAutoCreateGaps(true);
		_layout.setAutoCreateContainerGaps(true);
	
		
		SequentialGroup vertSeqGrp = _layout.createSequentialGroup();
		ParallelGroup horizParGrp = _layout.createParallelGroup(GroupLayout.Alignment.CENTER);
		
		vertSeqGrp.addComponent(_titleLabel);
		horizParGrp.addComponent(_titleLabel);
	
		horizParGrp.addComponent(_labelScrollPane);
		vertSeqGrp.addComponent(_labelScrollPane);

		_layout.setHorizontalGroup(horizParGrp);
		_layout.setVerticalGroup(vertSeqGrp);
		
		this.revalidate();
		_gui.repaint();
	}
	
	/**
	 * Adds a label for every subcalendar of the userCal stored in the communicator, setting the labels 
	 * with check marks based on whether the user has chosen to display the calendar as of the last save
	 * 
	 */
	public void initLabels(){
		_labels.clear();
		
		CalendarGroup<CalendarResponses> userCal = _communicator.getUserCal();
		
		if(userCal != null && !userCal.getCalendars().isEmpty()){
			for(CalendarResponses calResp : userCal.getCalendars()){
				UserCalLabel label = new UserCalLabel(calResp.getName(), calResp.getId(), _communicator);
				label.setSelected(calResp.isSelected());
				_labels.add(label);
			}
		}
		setUp();
	}

}
