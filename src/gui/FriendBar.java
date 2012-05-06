package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle;
import javax.swing.ScrollPaneConstants;

import calendar.CalendarSlots;
import calendar.Event;
import calendar.Event.CalByThatNameNotFoundException;

public class FriendBar extends JPanel {
	
	private Event _event;
	private ArrayList<FriendLabel> _friendLabels = new ArrayList<FriendLabel>();
	private ArrayList<CalendarSlots> _tempInvisible = new ArrayList<CalendarSlots>();
	private JPanel _scrollPaneInner = new JPanel();
	private JScrollPane _scrollPane;
	private GroupLayout _layout = new GroupLayout(_scrollPaneInner);
	private CalendarGui _gui;
	
	public FriendBar(CalendarGui gui){
		_gui = gui;
//		this.setLayout(_layout);
	//	_scrollPaneInner.setPreferredSize(new Dimension((int) (_scrollPane, 25));
//		this.setMaximumSize(new Dimension((int) (GuiConstants.FRAME_WIDTH*0.75), this.getMaximumSize().height));
		
//		this.setLayout(new GridLayout(0,(int) (GuiConstants.FRAME_WIDTH*0.75)/5));
		_scrollPaneInner.setLayout(_layout);
		_scrollPane = new JScrollPane(_scrollPaneInner);
		_scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.add(_scrollPane);
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
//		_scrollPaneInner.removeAll();
//		_scrollPaneInner.setLayout(_layout);
//		_scrollPaneInner.setPreferredSize(new Dimension((int) (GuiConstants.FRAME_WIDTH*0.75), 25));
////		this.setMaximumSize(new Dimension((int) (GuiConstants.FRAME_WIDTH*0.75), this.getMaximumSize().height));
////
//		SequentialGroup horizGrp = _layout.createSequentialGroup();
//		ParallelGroup vertGrp = _layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
//		
//		if(_event != null)
//			for(CalendarSlots cal : _event.getCalendars()){
//				
//				FriendLabel toAdd = new FriendLabel(this, cal.getOwner().getName(), cal.getColor());
//				toAdd.setVisible(true);
//				
//				_friendLabels.add(toAdd);
//				//this.add(toAdd);
//				
//				vertGrp.addComponent(toAdd);
//				horizGrp.addComponent(toAdd);
//				horizGrp.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,
//		                GroupLayout.DEFAULT_SIZE, 15);
//				
//			}
//		
//		//add to bar
//		_layout.setHorizontalGroup(horizGrp);
//		_layout.setVerticalGroup(vertGrp);
//		
//		this.revalidate();
//		_gui.repaint();
		
		_scrollPaneInner.removeAll();
		_scrollPaneInner.setLayout(_layout);
		_layout.setAutoCreateGaps(true);
		_layout.setAutoCreateContainerGaps(true);
		
		SequentialGroup vertGrp = _layout.createSequentialGroup();
		ParallelGroup horizGrp =  _layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		
		if(_event != null)
			for(CalendarSlots cal : _event.getCalendars()){
				
				FriendLabel toAdd = new FriendLabel(this, cal.getOwner().getName(), cal.getColor());
				toAdd.setVisible(true);
	
				_friendLabels.add(toAdd);
				
				vertGrp.addComponent(toAdd);
				horizGrp.addComponent(toAdd);
			}
		
		_layout.setVerticalGroup(vertGrp);
		_layout.setHorizontalGroup(horizGrp);
		
		
		this.revalidate();
		_gui.repaint();
	}
	
	public void setEvent(Event event){
		_event = event;
		initLabels();
	}
	
	public void setCalVisible(String name, boolean visible){
		CalendarSlots cal;
		try {
			if(_event != null){
				cal = _event.getCalByName(name);
				cal.setVisible(visible);
				_gui.repaint();
			}
		} catch (CalByThatNameNotFoundException e) {
			//TODO handle this
		}
		
	}
	
	public void hideAllVisible(String calToKeep){
		if(_event != null)
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
