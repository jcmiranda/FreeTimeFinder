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
		Font newLabelFont = new Font(_titleLabel.getFont().getName(),Font.BOLD, _titleLabel.getFont().getSize());  
		_titleLabel.setFont(newLabelFont);
		
		_labelScrollPane = new JScrollPane(_scrollPaneInner);
		
		this.initLabels();
	}
	
	
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
	
	public void initLabels(){
		_labels.clear();
		
		CalendarGroup<CalendarResponses> userCal = _communicator.getUserCal();
		
		if(userCal != null && !userCal.getCalendars().isEmpty()){
			for(CalendarResponses calResp : userCal.getCalendars()){
				UserCalLabel label = new UserCalLabel(calResp.getName(), _communicator);
				label.setSelected(calResp.isSelected());
				_labels.add(label);
			}
		}
		
		setUp();
	}

}
