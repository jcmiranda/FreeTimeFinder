package gui;

import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JPanel;

import calendar.CalendarSlots;
import calendar.When2MeetEvent;

public class FriendBar extends JPanel {
	
	private When2MeetEvent _event;
	private ArrayList<FriendLabel> _friendLabels = new ArrayList<FriendLabel>();
	private ArrayList<CalendarSlots> _tempInvisible = new ArrayList<CalendarSlots>();
	private GroupLayout _layout = new GroupLayout(this);
	
	public FriendBar(){
		this.setLayout(_layout);
	}
	
	
	private void initLabels(){
		_friendLabels.clear();
		SequentialGroup horizGrp = _layout.createSequentialGroup();
		ParallelGroup vertGrp = _layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
		
		for(CalendarSlots cal : _event.getCalendars()){
			FriendLabel toAdd = new FriendLabel(this, cal.getOwner().getName());
			_friendLabels.add(toAdd);
			JPanel panel = new JPanel();
			panel.add(toAdd);
			vertGrp.addComponent(panel);
			horizGrp.addComponent(panel);
			horizGrp.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,
	                GroupLayout.DEFAULT_SIZE, 15);
		}
		//add to bar
		_layout.setHorizontalGroup(horizGrp);
		_layout.setVerticalGroup(vertGrp);
	}
	
	public void setEvent(When2MeetEvent event){
		_event = event;
		initLabels();
	}
	
	public void setCalVisible(String name, boolean visible){
		CalendarSlots cal = _event.getCalByName(name);
		cal.setVisible(visible);
	}
	
	public void hideAllVisible(String calToKeep){
		for(CalendarSlots cal : _event.getCalendars()){
			if(cal.isVisible() && !cal.getOwner().getName().equals(calToKeep)){
				_tempInvisible.add(cal);
				cal.setVisible(false);
			}
		}
	}
	
	public void showAllHidden(){
		for(CalendarSlots cal : _tempInvisible){
			cal.setVisible(true);
		}
		_tempInvisible.clear();
	}
	
	
	
	
	
	
	

}
