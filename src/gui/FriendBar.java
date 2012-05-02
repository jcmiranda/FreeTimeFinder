package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;

import calendar.CalendarSlots;
import calendar.Event;
import calendar.Event.CalByThatNameNotFoundException;

public class FriendBar extends JPanel {
	
	private Event _event;
	private ArrayList<FriendLabel> _friendLabels = new ArrayList<FriendLabel>();
	private ArrayList<CalendarSlots> _tempInvisible = new ArrayList<CalendarSlots>();
	private GroupLayout _layout = new GroupLayout(this);
	private CalendarGui _gui;
	
	public FriendBar(CalendarGui gui){
		_gui = gui;
		this.setLayout(_layout);
	}
	
	public void setLabelColor(String labelName, Color color){
		for(FriendLabel label : _friendLabels){
			if(label.getText() == labelName){
				label.setColor(color);
				break;
			}
		}
	}
	
	
	private void initLabels(){
		_friendLabels.clear();
		SequentialGroup horizGrp = _layout.createSequentialGroup();
		ParallelGroup vertGrp = _layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
		
		for(CalendarSlots cal : _event.getCalendars()){
			
			FriendLabel toAdd = new FriendLabel(this, cal.getOwner().getName(), cal.getColor());
			toAdd.setVisible(true);
			
			_friendLabels.add(toAdd);
			
			vertGrp.addComponent(toAdd);
			horizGrp.addComponent(toAdd);
			horizGrp.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,
	                GroupLayout.DEFAULT_SIZE, 15);
			
		}
		
		//add to bar
		_layout.setHorizontalGroup(horizGrp);
		_layout.setVerticalGroup(vertGrp);
	}
	
	public void setEvent(Event event){
		_event = event;
		initLabels();
	}
	
	public void setCalVisible(String name, boolean visible){
		CalendarSlots cal;
		try {
			cal = _event.getCalByName(name);
			cal.setVisible(visible);
			_gui.repaint();
		} catch (CalByThatNameNotFoundException e) {
			//TODO handle this
		}
		
	}
	
	public void hideAllVisible(String calToKeep){
		for(CalendarSlots cal : _event.getCalendars()){
			if(cal.isVisible() && !cal.getOwner().getName().equals(calToKeep)){
				_tempInvisible.add(cal);
				cal.setVisible(false);
			}
		}
		_gui.repaint();
	}
	
	public void showAllHidden(){
		for(CalendarSlots cal : _tempInvisible){
			cal.setVisible(true);
		}
		_tempInvisible.clear();
		_gui.repaint();
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		for(FriendLabel label: _friendLabels){
			label.repaint();
		}
	}
	
	
	
	
	
	
	

}
